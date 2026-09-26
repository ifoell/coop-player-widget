package com.example.coopwidget.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.coopwidget.domain.model.PartyData

@Composable
fun PartySettingsDialog(
    currentData: PartyData,
    onDismiss: () -> Unit,
    onSave: (familyName: String, subTitle: String, footerText: String, showFooter: Boolean, showStats: Boolean) -> Unit
) {
    var familyName by remember { mutableStateOf(currentData.familyName) }
    var subTitle by remember { mutableStateOf(currentData.subTitle) }
    var footerText by remember { mutableStateOf(currentData.footerText) }
    var showFooter by remember { mutableStateOf(currentData.showFooter) }
    var showStats by remember { mutableStateOf(currentData.showStats) }

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
                        text = "PARTY & WIDGET SETTINGS",
                        fontSize = 16.sp,
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

                // Family Name
                Text("FAMILY / TEAM NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
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

                // Subtitle
                Text("SUBTITLE / TAGLINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = subTitle,
                    onValueChange = { subTitle = it },
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

                // Footer Text
                Text("FOOTER TEXT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = footerText,
                    onValueChange = { footerText = it },
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

                // Toggle Show Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Footer on Widget", fontSize = 14.sp, color = Color(0xFFF5F7FA))
                    Switch(
                        checked = showFooter,
                        onCheckedChange = { showFooter = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2196F3))
                    )
                }

                // Toggle Show Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Stats Progress Bars", fontSize = 14.sp, color = Color(0xFFF5F7FA))
                    Switch(
                        checked = showStats,
                        onCheckedChange = { showStats = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2196F3))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSave(
                            familyName.ifBlank { "CO-OP FAMILY" },
                            subTitle.ifBlank { "SAME TEAM • SAME LIFE" },
                            footerText.ifBlank { "SAME TEAM • BIGGER ADVENTURES" },
                            showFooter,
                            showStats
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SAVE SETTINGS", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                }
            }
        }
    }
}
