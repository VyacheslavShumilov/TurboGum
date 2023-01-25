package com.vshum.turbogum.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.vshum.turbogum.model.LinersFavourite

@Dao
interface LinersDao {
    @Query("SELECT * FROM linersFavourite ORDER BY id DESC")
    fun getAllFavouriteLiners(): List<LinersFavourite>

    @Insert
    fun insertLiner(liner: LinersFavourite)

    @Delete
    fun deleteLiner(liner: LinersFavourite)

    @Query("SELECT * FROM linersFavourite WHERE id =:id")
    fun getLinerFavorite(id: String): LinersFavourite?
}