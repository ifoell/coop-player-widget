package com.example.coopwidget.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PartyData(
    val familyName: String = "THE FAMILY",
    val subTitle: String = "SAME TEAM • SAME LIFE",
    val footerText: String = "SAME TEAM • BIGGER ADVENTURES",
    val showFooter: Boolean = true,
    val showStats: Boolean = true,
    val compactMode: Boolean = false,
    val players: List<Player> = emptyList()
)
