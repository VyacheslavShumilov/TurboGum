package com.vshum.turbogum.model

import com.google.gson.annotations.SerializedName

data class TurboLiners(
    @SerializedName("Id") val id: String,
    @SerializedName("NumberLiner") val numberLiner: Int,
    @SerializedName("ImageURL") val imageURL: String,
    @SerializedName("Brand") val brand: String,
    @SerializedName("Model") val model: String,
    @SerializedName("WikiArticle") val wikiArticle: String,
    @SerializedName("VkArticle") val vkArticle: String,
    @SerializedName("Video") val video: String
)
