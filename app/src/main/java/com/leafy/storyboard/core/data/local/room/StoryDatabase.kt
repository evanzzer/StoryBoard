package com.leafy.storyboard.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.leafy.storyboard.core.data.local.entity.RemoteKeys
import com.leafy.storyboard.core.data.local.entity.StoryEntity

@Database(
    entities = [
        StoryEntity::class,
        RemoteKeys::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}