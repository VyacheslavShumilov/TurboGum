package com.vshum.turbogum.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Liner(
    @SerializedName("UniqueNumber") val uniqueNumber: String,
    @SerializedName("Id") val id: String,
    @SerializedName("NumberLiner") val numberLiner: String,
    @SerializedName("Brand") val brand: String,
    @SerializedName("Model") val model: String,
    @SerializedName("WikiArticle") val wikiArticle: String,
//    @SerializedName("WebsiteSociete") val websiteSociete: String,
    @SerializedName("Video") val video: String,
    @SerializedName("VkArticle") val vkArticle: String,
    @SerializedName("ImageUrlLiner") val imageUrlLiner: String,
    @SerializedName("Index") val index: String,
    @SerializedName("Series") val series: String,
    @SerializedName("Note") val note: String

): Serializable


