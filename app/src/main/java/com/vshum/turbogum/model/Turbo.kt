package com.vshum.turbogum.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LinersList(
    @SerializedName("NameLiner") val nameLiner: String,
    @SerializedName("ImageUrlLiner") val imageUrlLiner: String
): Serializable

data class Liner(
    @SerializedName("Id") val id: String,
    @SerializedName("NumberLiner") val numberLiner: Int,
    @SerializedName("ImageUrl") val imageUrlLiner: String,
    @SerializedName("Brand") val brand: String,
    @SerializedName("Model") val model: String,
    @SerializedName("WikiArticle") val wikiArticle: String,
    @SerializedName("VkArticle") val vkArticle: String,
    @SerializedName("Video") val video: String,
    @SerializedName("nameLiner") val nameLiner: String
): Serializable


