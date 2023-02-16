package com.vshum.turbogum.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.vshum.turbogum.model.LinersFavourite

@Dao
interface LinersDao {

    @Insert
    fun insertLiner(liner: LinersFavourite)

    @Delete
    fun deleteFavoriteLiner(liner: LinersFavourite)

    @Query("SELECT * FROM linersFavourite ORDER BY id DESC")
    fun getAllFavouriteLiners(): List<LinersFavourite>

    @Query("SELECT * FROM linersFavourite WHERE uniqueNumber =:uniqueNumber")
    fun getLinerFavorite(uniqueNumber: String): LinersFavourite

    @Query("UPDATE linersFavourite SET note = :note  WHERE uniqueNumber =:uniqueNumber")
    fun editNoteLiner(uniqueNumber: String, note:String)

    /***
     * Запрос выбирает заметку (note) для строки, где номер вкладыша (uniqueNumber) равен заданному.
     */
    @Query("SELECT note FROM linersFavourite WHERE uniqueNumber = :uniqueNumber")
    fun getNoteLiner(uniqueNumber: String): String

}