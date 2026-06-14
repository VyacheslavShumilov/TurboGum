package com.vshum.turbogum.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room cache of the full sticker catalog (~820 liners across all series), used to render placeholders for not-yet-collected stickers. */
@Entity(tableName = "linerCatalog")
data class LinerCatalog(
    @PrimaryKey val uniqueNumber: String,
    val id: String,
    val numberLiner: String,
    val brand: String,
    val model: String,
    val wikiArticle: String,
    val websiteSociete: String,
    val video: String,
    val vkArticle: String,
    val imageUrlLiner: String,
    val index: String,
    val series: String,
    val note: String
)

fun Liner.toCatalogEntity() = LinerCatalog(
    uniqueNumber = uniqueNumber,
    id = id,
    numberLiner = numberLiner,
    brand = brand,
    model = model,
    wikiArticle = wikiArticle,
    websiteSociete = websiteSociete,
    video = video,
    vkArticle = vkArticle,
    imageUrlLiner = imageUrlLiner,
    index = index,
    series = series,
    note = note
)
