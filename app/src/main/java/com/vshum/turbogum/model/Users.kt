package com.vshum.turbogum.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Users(
    @SerializedName("UniqueNumber") val uniqueNumber: Int,
    @SerializedName("Email") val email: String,
    @SerializedName("Password") val password: String
    ): Serializable
