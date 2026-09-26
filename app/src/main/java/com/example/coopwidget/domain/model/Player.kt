package com.example.coopwidget.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String, // e.g. DAD, MOM, BABY, SON, DAUGHTER, GRANDPA, GRANDMA, BROTHER, SISTER, OTHER
    val birthDate: String, // ISO YYYY-MM-DD
    val avatarUri: String? = null,
    val accentColorHex: String = "#2196F3",
    val tagline: String = "", // e.g. "CODE • BUILD • SOLVE • PROTECT" or "CARE • SUPPORT • BALANCE"
    val stats: List<Stat> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

object DefaultRoles {
    val ALL = listOf(
        "DAD",
        "MOM",
        "SON",
        "DAUGHTER",
        "BABY",
        "GRANDPA",
        "GRANDMA",
        "BROTHER",
        "SISTER",
        "OTHER"
    )

    fun getDefaultTaglineForRole(role: String): String {
        return when (role.uppercase()) {
            "DAD" -> "CODE • BUILD • SOLVE • PROTECT"
            "MOM" -> "CARE • SUPPORT • BALANCE • KEEP US STRONG"
            "BABY" -> "OUR GREATEST ADVENTURE"
            "SON" -> "EXPLORE • LEARN • GROW • PLAY"
            "DAUGHTER" -> "CREATIVE • SHINE • JOY • BRAVE"
            else -> "SAME TEAM • SAME LIFE"
        }
    }
}

object DefaultAccentColors {
    val PALETTE = listOf(
        "#2196F3", // Blue
        "#FF4F7B", // Pink/Red
        "#31D17C", // Green
        "#A855F7", // Purple
        "#F59E0B", // Amber
        "#06B6D4", // Cyan
        "#EC4899", // Magenta
        "#10B981"  // Emerald
    )

    fun getColorForIndex(index: Int): String {
        return PALETTE[index % PALETTE.size]
    }
}
