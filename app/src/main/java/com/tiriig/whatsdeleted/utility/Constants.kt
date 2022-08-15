package com.tiriig.whatsdeleted.utility

import com.tiriig.whatsdeleted.data.model.Chat

object Constants {

    val chatList = listOf(
        Chat("1", "Mohamed Jama", "Hi, How are you doing bro?", 1660579599722, "com.whatsapp"),
        Chat("2", "Kice smith", "Finished, check your email", 1660579872091, "org.telegram.messenger"),
        Chat("3", "Project Startup", "Ayan: Sounds like a plan tomorrow at 9PM", 1660470600000, "com.whatsapp.w4b", isGroup = true),
        Chat("4", "Abdirahman", "OK, I got it", 1660470600000, "org.thoughtcrime.securesms"),
        Chat("5", "Barkulanhub Members", "Sumaya: I request from you to Bring a book and a pen", 1660470600000, "com.whatsapp",isGroup = true),
        Chat("6", "Mohamed Kuwait", "Which time we'll meet tomorrow?", 1659937463000, "org.thoughtcrime.securesms"),
        Chat("7", "Ridwan Ahmed", "Haye wanku sugayaa intad soo dhamaynayso ee ku dhakhso uun", 1659937463000, "org.telegram.messenger"),
        Chat("8", "Sadio Allen", "Yep, That's what we want", 1654627667000, "com.whatsapp")
    )

    const val chatDetail =
        "[{\"id\":\"1\",\"isDeleted\":false,\"message\":\"Finished, check your email\",\"time\":1653487863000,\"user\":\"Kice smith\"},{\"id\":\"1\",\"isDeleted\":true,\"message\":\"Can you please give me another one hour\",\"time\":1659911243000,\"user\":\"Kice smith\"},{\"id\":\"1\",\"isDeleted\":false,\"message\":\"Just give two hours to do it\",\"time\":1653475378485,\"user\":\"Kice smith\"},{\"id\":\"1\",\"isDeleted\":false,\"message\":\"Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.\",\"time\":1653119352000,\"user\":\"Kice smith\"},{\"id\":\"1\",\"isDeleted\":false,\"message\":\"Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\\u0027s standard dummy text ever since the 1500s, when an unknown printer took a galley\",\"time\":1653412146000,\"user\":\"Kice smith\"}]"
}