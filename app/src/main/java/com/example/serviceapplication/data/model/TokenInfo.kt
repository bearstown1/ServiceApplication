package com.example.serviceapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TokenInfo")
data class TokenInfo (
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,

    var idToken: String?,

    var accessToken: String?
)