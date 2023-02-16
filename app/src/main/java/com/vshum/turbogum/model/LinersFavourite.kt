package com.vshum.turbogum.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linersFavourite")
data class LinersFavourite(
    @PrimaryKey(autoGenerate = true) val key: Int,
    @ColumnInfo(name = "uniqueNumber") val uniqueNumber: String,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name ="numberLiner") val numberLiner: String,
    @ColumnInfo(name ="brand") val brand: String,
    @ColumnInfo(name ="model") val model: String,
    @ColumnInfo(name ="wikiArticle") val wikiArticle: String,
    @ColumnInfo(name ="video") val video: String,
    @ColumnInfo(name ="vkArticle") val vkArticle: String,
    @ColumnInfo(name ="imageUrlLiner") val imageUrlLiner: String,
    @ColumnInfo(name ="index") val index: String,
    @ColumnInfo(name ="series") val series: String,
    @ColumnInfo(name = "note") val note: String
    )