# WhatsDeleted

Android app that recovers deleted WhatsApp/WhatsApp Business/Telegram/Signal
messages by reading the system notifications those apps post, storing them
locally, and flagging the ones that get retracted. Everything lives in
`com.tiriig.whatsdeleted`, Kotlin, single-module (`app`).

## How message capture actually works

This is the part that's easy to get wrong, so read it before touching
`NLService`.

1. `NLService` (a `NotificationListenerService`) gets `onNotificationPosted`
   for every notification on the device and filters to the four supported
   packages via `isValidApp()` (`utility/GeneralExtensions.kt`).
2. It pulls `android.title` (contact or group name) out of the notification
   extras. If the notification has a `MessagingStyle` (WhatsApp/Telegram
   bundle every unread line of a chat into one when the phone is locked),
   each line is saved separately with its own sender and timestamp via
   `saveBundledMessages()`; lines with no sender (or the device owner as
   sender) are the user's own replies and are skipped. Otherwise it falls
   back to `android.text`, which only ever holds the last line. There's no
   structured contract for any of this — it's whatever WhatsApp/Telegram/
   Signal happen to put in those fields, so the parsing is fragile by nature.
3. Group vs. direct chat is inferred from whether `title` contains `:` —
   WhatsApp posts group notification titles as `"GroupName: SenderName"` —
   or from `MessagingStyle.isGroupConversation`. `(N messages)` suffixes
   get stripped off the group name (`String.groupName()`).
4. **Deletion isn't a separate system event.** When a message is deleted,
   the messaging app just posts a new notification for the same chat whose
   body is `"This message was deleted"` (WhatsApp/Telegram) or the same
   text with a trailing period (Signal). `String.isDeletionNotice()`
   catches both. When that happens, `NLService.flagDeleted()` marks the
   stored message `isDeleted = true` instead of saving the notice as a new
   message. For a bundled notification the notice replaces the deleted line
   in place and keeps its timestamp, so the exact message is looked up by
   `(user, app, dateTime)`; for a plain notification all we have is the
   chat's most recent stored message.
5. `Notifications.notify()` then posts a local heads-up notification so the
   user knows something was deleted; tapping it sends `MainActivity` the
   `user`/`app`/`notificationDeleted` extras it needs to jump straight into
   that chat's detail screen (see `MainActivity.navigateToChatDetail()`).

If you change how titles/bodies are parsed, keep in mind the `user` key is
what dedup and deletion detection both hang off, so a malformed one
silently breaks both. Plain notifications go through
`ChatRepository.saveMessage()`, which de-dupes against the last stored
message for that `user`. Bundled ones go through `saveTimestampedMessage()`,
which de-dupes on `(user, app, dateTime)` instead — the whole conversation
is re-posted with every new message, so the last-message check would
re-insert every earlier line.

**Deletion detection is best-effort, not guaranteed.** WhatsApp doesn't
reliably re-post a "This message was deleted" notification for every
deletion anymore, so `flagDeleted()` only catches the cases
where it still does. Don't rip this out — it's free when it works — but
don't advertise it as reliable either. The fallback the app actually
depends on is manual: the user notices a message vanished inside WhatsApp
itself, then opens WhatsDeleted to read what it said, since the original
notification was already captured and stored before it was deleted.

## Package layout

```
data/
  model/       Room entity (Chat) + small view/projection models (ChatItem, DeletedMessage)
  database/    Room database, DAO, migrations
  repository/  ChatRepository — the only thing that talks to the DAO
di/            Hilt modules (Room instance)
services/      NLService — the notification listener
ui/
  main/        SplashActivity (routes to intro or main), MainActivity (nav host + service bootstrap)
  intro/       First-run AppIntro flow, ends with the notification-access permission slide
  chat/        ChatViewModel shared by list + detail
  chat/list/   Chat list screen (grouped by conversation)
  chat/detail/ Single conversation, messages grouped by date
utility/       Extension functions, Constants-free — no dead sample data, keep it that way
```

- Everything is wired through Hilt (`@AndroidEntryPoint` / `@HiltViewModel` / `@Inject`).
  `WhatsDeleted` (the `Application` class) is the `@HiltAndroidApp` root.
- `ChatRepository` is the only class that touches `UserDao` — don't call
  the DAO directly from ViewModels or the service.
- Navigation is a single-Activity setup: `MainActivity` hosts a Navigation
  Component graph (`res/navigation/main_navigation.xml`) with two
  destinations, `chatListFragment` (start) and `chatDetailFragment`.
- The app draws edge-to-edge (`MainActivity.enableEdgeToEdge()`), so don't
  reintroduce `android:statusBarColor`/`navigationBarColor` theme items —
  they're ignored from Android 15 on anyway. `applyEdgeToEdgeInsets()` pads
  `appBarLayout` and `nav_host_fragment` with the system bar insets instead;
  if you add another top-level container to `activity_main.xml`, it needs
  the same treatment or it'll sit under the status/nav bar.

## Database

Room, entity `Chat` (table `chat`), currently at schema version 3
(`app/schemas/`, `exportSchema = true`). `MIGRATION_2_3` is a manual
migration (table rename/rebuild); `AutoMigration(from = 2, to = 3)` is also
declared in `@Database` — if you add a new migration, follow the existing
pattern of one manual `Migration` object per non-trivial schema change
rather than relying on auto-migration for anything that isn't a pure
column add.

`isDeleted` and `id` (a random 10-char base32 string from `getRandomNum()`,
not an autoincrement) are the two columns the deletion-detection flow
depends on — see above.

## Permissions the app needs to actually work

- `BIND_NOTIFICATION_LISTENER_SERVICE` — granted by the user manually via
  system settings, not a runtime permission dialog. `IntroPermissionFragment`
  checks `enabled_notification_listeners` and links out to
  `ACTION_NOTIFICATION_LISTENER_SETTINGS`; this is the gate for the whole
  app, enforced as the last slide of the intro (`SlidePolicy`).
- `POST_NOTIFICATIONS` (API 33+) — needed for `Notifications.notify()` to
  actually show the "message deleted" alert. Requested once from
  `MainActivity` on launch; it's a soft ask (no gating), since the
  recover-and-store flow works without it.

## Build

- `google-services.json` is required (Firebase Crashlytics/Analytics) and
  is gitignored — you need your own to build locally.
- Firebase Crashlytics collection is only enabled in release builds
  (`WhatsDeleted.onCreate`), and explicitly disabled via manifest metadata
  as a second safety net.
- `minSdk 23`, `targetSdk`/`compileSdk 36`.

## Branches

- `main` — active development, where new work lands before a release.
- `production` — mirrors the last released version.
