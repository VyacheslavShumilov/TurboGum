package com.vshum.turbogum.util

/** Helpers for showing a user as initials when no photo avatar is available. */
object AvatarUtil {

    /** First letter of [nickname], uppercased, or "?" if blank. */
    fun initialsFor(nickname: String): String =
        nickname.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}
