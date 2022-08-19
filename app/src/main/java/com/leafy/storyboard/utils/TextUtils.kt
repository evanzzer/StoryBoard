package com.leafy.storyboard.utils

object TextUtils {
    fun emailMatcher(input: String) =
        input.matches("^[a-zA-Z0-9][\\w.-]+@[a-zA-Z0-9-]+(?:\\.[\\w-]{2,4})+$".toRegex())

    fun passwordMatcher(input: String) = input.length >= 6
}