package com.example.serviceapplication.data.model

data class IdTokenInfo(
    var jwtId: String,
    var issuer: String,
    var subject: String,
    var issuedAt: String,
    var expirationTime: String,
    var audience: String
)
