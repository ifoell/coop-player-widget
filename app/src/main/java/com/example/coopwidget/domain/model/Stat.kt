package com.example.coopwidget.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Stat(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val value: Int // 0 to 100
)
