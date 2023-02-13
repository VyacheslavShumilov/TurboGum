package com.vshum.turbogum.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vshum.turbogum.model.LinersFavourite

@Database(entities = [LinersFavourite::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun linersDao(): LinersDao
}