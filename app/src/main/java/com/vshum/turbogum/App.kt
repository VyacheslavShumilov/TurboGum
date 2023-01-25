package com.vshum.turbogum

import android.app.Application
import androidx.room.Room
import com.vshum.turbogum.dao.AppDatabase
import com.vshum.turbogum.services.ServicesLocator

class App : Application() {

    private lateinit var database: AppDatabase

    lateinit var servicesLocator: ServicesLocator

    override fun onCreate() {
        super.onCreate()
        servicesLocator = ServicesLocator()

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "liners_database").build()
    }

    fun getDatabase(): AppDatabase {
        return database
    }
}