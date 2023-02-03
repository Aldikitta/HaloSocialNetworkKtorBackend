package com.aldikitta.util

object ApiResponseMessages {
    const val COMMENT_TO_LONG = "The comment must not exceed ${Constants.MAX_COMMENT_LENGTH} characters"
    const val USER_ALREADY_EXIST = "A user with this email already exist"
    const val FIELD_BLANK = "The field cannot be empty"
    const val INVALID_CREDENTIALS = "Oops, that is not correct, please try again"
    const val USER_NOT_FOUND = "The user couldn't be found"
}