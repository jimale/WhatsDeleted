package com.tiriig.whatsdeleted.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Step 1: Rename the table to preserve the old data
        db.execSQL("ALTER TABLE Chat RENAME TO Chat_old")

        // Step 2: Create the new schema for the Chat table (with correct column names)
        db.execSQL("""
            CREATE TABLE Chat (
                id TEXT NOT NULL PRIMARY KEY,
                user TEXT NOT NULL,
                message TEXT NOT NULL,
                dateTime INTEGER NOT NULL,  -- Correct column name
                app TEXT NOT NULL DEFAULT '',
                isDeleted INTEGER NOT NULL DEFAULT 0,
                isGroup INTEGER NOT NULL DEFAULT 0
            )
        """)

        // Step 3: Copy data from the old table to the new one (mapping 'time' to 'dateTime')
        db.execSQL("""
            INSERT INTO Chat (id, user, message, dateTime, app, isDeleted, isGroup)
            SELECT id, user, message, time, app, isDeleted, isGroup FROM Chat_old
        """)

        // Step 4: Drop the old table after data has been transferred
        db.execSQL("DROP TABLE Chat_old")
    }
}
