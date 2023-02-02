package com.aldikitta.data.responses

data class BasicApiResponse(
    val successful: Boolean,
    val message: String? = null,
)
