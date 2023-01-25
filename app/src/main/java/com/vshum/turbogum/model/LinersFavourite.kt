package com.vshum.turbogum.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linersFavourite")
data class LinersFavourite(
    @PrimaryKey(autoGenerate = true) val key: Int,
    @ColumnInfo(name = "Id") val id: String,
    @ColumnInfo(name ="NumberLiner") val numberLiner: String,
    @ColumnInfo(name ="Brand") val brand: String,
    @ColumnInfo(name ="Model") val model: String,
    @ColumnInfo(name ="WikiArticle") val wikiArticle: String,
    @ColumnInfo(name ="Video") val video: String,
    @ColumnInfo(name ="VkArticle") val vkArticle: String,
    @ColumnInfo(name ="ImageUrlLiner") val imageUrlLiner: String,
    @ColumnInfo(name ="NameWrapper") val nameWrapper: String
    )