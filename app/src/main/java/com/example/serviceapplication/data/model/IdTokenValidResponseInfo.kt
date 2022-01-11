package com.example.serviceapplication.data.model

data class IdTokenValidResponseInfo(
    val success: Boolean,
    val error: String,
    val error_description:String,
    val statusCode:String,
    val dataMap: Map<String, String>
)
