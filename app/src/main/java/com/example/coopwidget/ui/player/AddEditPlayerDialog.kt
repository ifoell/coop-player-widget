package com.example.coopwidget.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.coopwidget.domain.model.DefaultAccentColors
import com.example.coopwidget.domain.model.DefaultRoles
import com.example.coopwidget.domain.model.Player
import com.example.coopwidget.domain.model.Stat
import com.example.coopwidget.domain.usecase.AgeCalculator
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlayerDialog(
    playerToEdit: Player? = null,
    onDismiss: () -> Unit,
    onSave: (Player) -> Unit
) {
    var name by remember { mutableStateOf(playerToEdit?.name ?: "") }
    var selectedRole by remember { mutableStateOf(playerToEdit?.role ?: "DAD") }
    var customRole by remember { mutableStateOf(if (playerToEdit?.role !in DefaultRoles.ALL) playerToEdit?.role ?: "" else "") }
    var birthDateStr by remember { mutableStateOf(playerToEdit?.birthDate ?: "1991-05-10") }
    var accentColorHex by remember { mutableStateOf(playerToEdit?.accentColorHex ?: DefaultAccentColors.PALETTE.first()) }
    var tagline by remember { mutableStateOf(playerToEdit?.tagline ?: DefaultRoles.getDefaultTaglineForRole(playerToEdit?.role ?: "DAD")) }
    var stats by remember {
        mutableStateOf(
            playerToEdit?.stats ?: listOf(
                Stat(name = "STR", value = 80),
                Stat(name = "DEF", value = 75),
                Stat(name = "INT", value = 85)
            )
        )
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    val computedLevel = remember(birthDateStr) {
        AgeCalculator.calculateLevel(birthDateStr)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (playerToEdit == null) "ADD PLAYER" else "EDIT PLAYER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF5F7FA)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFFF4F7B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Name Input
                Text("NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Dad") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color(0xFFF5F7FA),
                        unfocusedTextColor = Color(0xFFF5F7FA)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role Selector
                Text("ROLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = roleDropdownExpanded,
                    onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedRole in DefaultRoles.ALL) selectedRole else "OTHER",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color(0xFFF5F7FA),
                            unfocusedTextColor = Color(0xFFF5F7FA)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false }
                    ) {
                        DefaultRoles.ALL.forEach { roleOption ->
                            DropdownMenuItem(
                                text = { Text(roleOption) },
                                onClick = {
                                    selectedRole = roleOption
                                    roleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedRole == "OTHER" || selectedRole !in DefaultRoles.ALL) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customRole,
                        onValueChange = { customRole = it },
                        placeholder = { Text("Enter Custom Role") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color(0xFFF5F7FA),
                            unfocusedTextColor = Color(0xFFF5F7FA)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Birth Date Input & LV Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("BIRTHDAY (YYYY-MM-DD)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                    Text("LV. $computedLevel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = birthDateStr,
                    onValueChange = { birthDateStr = it },
                    placeholder = { Text("1991-05-10") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color(0xFFF5F7FA),
                        unfocusedTextColor = Color(0xFFF5F7FA)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Accent Color Selector
                Text("ACCENT COLOR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DefaultAccentColors.PALETTE.forEach { colorHex ->
                        val isSelected = accentColorHex.equals(colorHex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parseColor(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { accentColorHex = colorHex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tagline Input
                Text("TAGLINE / KEYWORDS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    placeholder = { Text("e.g. CODE • BUILD • SOLVE • PROTECT") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color(0xFFF5F7FA),
                        unfocusedTextColor = Color(0xFFF5F7FA)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("STATS (${stats.size}/5)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                    if (stats.size < 5) {
                        TextButton(
                            onClick = {
                                stats = stats + Stat(name = "STAT", value = 80)
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ADD STAT", fontSize = 12.sp)
                        }
                    }
                }

                stats.forEachIndexed { index, stat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = stat.name,
                                    onValueChange = { newName ->
                                        stats = stats.toMutableList().apply {
                                            this[index] = this[index].copy(name = newName.take(12))
                                        }
                                    },
                                    label = { Text("Stat Name") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF2196F3),
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = Color(0xFFF5F7FA),
                                        unfocusedTextColor = Color(0xFFF5F7FA)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${stat.value}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF5F7FA),
                                    modifier = Modifier.width(36.dp)
                                )
                                IconButton(
                                    onClick = {
                                        stats = stats.toMutableList().apply { removeAt(index) }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Stat",
                                        tint = Color(0xFFFF4F7B)
                                    )
                                }
                            }
                            Slider(
                                value = stat.value.toFloat(),
                                onValueChange = { newValue ->
                                    stats = stats.toMutableList().apply {
                                        this[index] = this[index].copy(value = newValue.toInt())
                                    }
                                },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = parseColor(accentColorHex),
                                    activeTrackColor = parseColor(accentColorHex),
                                    inactiveTrackColor = Color(0xFF334155)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        val finalRole = if (selectedRole == "OTHER") customRole.ifBlank { "OTHER" } else selectedRole
                        val parsedDate = AgeCalculator.parseDate(birthDateStr)

                        when {
                            name.isBlank() -> errorMessage = "Name is required"
                            parsedDate == null -> errorMessage = "Invalid date format. Use YYYY-MM-DD"
                            parsedDate.isAfter(LocalDate.now()) -> errorMessage = "Birthdate cannot be in the future"
                            finalRole.isBlank() -> errorMessage = "Role is required"
                            stats.any { it.name.isBlank() } -> errorMessage = "All stat names must be filled out"
                            else -> {
                                val player = (playerToEdit ?: Player(name = "", role = "", birthDate = "")).copy(
                                    name = name.trim(),
                                    role = finalRole.trim().uppercase(),
                                    birthDate = birthDateStr.trim(),
                                    accentColorHex = accentColorHex,
                                    tagline = tagline.trim(),
                                    stats = stats,
                                    updatedAt = System.currentTimeMillis()
                                )
                                onSave(player)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (playerToEdit == null) "CREATE PLAYER" else "SAVE CHANGES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private fun parseColor(hex: String): Color {
    return try {
        val clean = if (hex.startsWith("#")) hex.substring(1) else hex
        val colorInt = clean.toLong(16).toInt()
        val full = if (clean.length == 6) colorInt or 0xFF000000.toInt() else colorInt
        Color(full)
    } catch (e: Exception) {
        Color(0xFF2196F3)
    }
}
