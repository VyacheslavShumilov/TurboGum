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

    @Query("SELECT * FROM linersFavourite WHERE numberLiner =:numberLiner")
    fun getLinerFavorite(numberLiner: String): LinersFavourite
}