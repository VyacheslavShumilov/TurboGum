package com.vshum.turbogum.model

/** Firestore document model for collection "users/{uid}". */
data class UserProfile(
    val uid: String = "",
    val nickname: String = "",
    val totalPercent: Float = 0f,
    val series1: Float = 0f,
    val series2: Float = 0f,
    val series3: Float = 0f,
    val series4: Float = 0f,
    val series5: Float = 0f,
    val super1: Float = 0f,
    val super2: Float = 0f,
    val super3: Float = 0f,
    val sport1: Float = 0f,
    val sport2: Float = 0f,
    val classic1: Float = 0f,
    val classic2: Float = 0f
) {
    fun percentFor(field: String): Float = when (field) {
        "totalPercent" -> totalPercent
        "series1" -> series1
        "series2" -> series2
        "series3" -> series3
        "series4" -> series4
        "series5" -> series5
        "super1" -> super1
        "super2" -> super2
        "super3" -> super3
        "sport1" -> sport1
        "sport2" -> sport2
        "classic1" -> classic1
        "classic2" -> classic2
        else -> 0f
    }
}
