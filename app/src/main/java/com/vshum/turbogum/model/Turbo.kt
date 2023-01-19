package com.vshum.turbogum.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//data class WrappersList(
//    @SerializedName("NameWrapper") val nameWrapper: String,
//    @SerializedName("ImageUrlWrapper") val imageUrlWrapper: String
//): Serializable

data class Liner(
    @SerializedName("Id") val id: String,
    @SerializedName("NumberLiner") val numberLiner: Int,
    @SerializedName("ImageUrlLiner") val imageUrlLiner: String,
    @SerializedName("Brand") val brand: String,
    @SerializedName("Model") val model: String,
    @SerializedName("WikiArticle") val wikiArticle: String,
    @SerializedName("VkArticle") val vkArticle: String,
    @SerializedName("Video") val video: String,
    @SerializedName("NameWrapper") val nameWrapper: String
): Serializable


