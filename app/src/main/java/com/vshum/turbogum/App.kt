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

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "liners_database")
//            .addMigration(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }
    /***
     * Можно позволить Room выполнять деструктивные миграции, когда изменяю схему базы данных.
     * Это означает, что при обновлении схемы базы данных существующие данные будут потеряны. Чтобы это сделать, можно использовать метод fallbackToDestructiveMigration
     */

    fun getDatabase(): AppDatabase {
        return database
    }

    /***
     * Чтобы предоставить путь миграции, нужно добавить код миграции в файл конфигурации базы данных Room
     * Здесь MIGRATION_1_2 является объектом класса Migration, который описывает миграцию из версии 1 базы данных в версию 2. В методе migrate() вы можете указать операции миграции, которые необходимо выполнить для обновления существующих данных. Обычно это включает в себя изменение структуры таблицы или добавление новых столбцов.
     */
//    companion object {
//        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Perform the migration operations here
//            }
//        }
//    }
}