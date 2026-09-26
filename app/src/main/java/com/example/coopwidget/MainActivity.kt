package com.example.coopwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.coopwidget.theme.CoOpWidgetTheme
import com.example.coopwidget.ui.home.PartyHomeScreen
import com.example.coopwidget.ui.home.PartyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PartyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoOpWidgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090D16)
                ) {
                    val partyData by viewModel.partyDataState.collectAsState()

                    PartyHomeScreen(
                        partyData = partyData,
                        onAddPlayer = { viewModel.addPlayer(it) },
                        onUpdatePlayer = { viewModel.updatePlayer(it) },
                        onDeletePlayer = { viewModel.deletePlayer(it) },
                        onReorderPlayers = { viewModel.reorderPlayers(it) },
                        onUpdateSettings = { familyName, subTitle, footerText, showFooter, showStats ->
                            viewModel.updateSettings(familyName, subTitle, footerText, showFooter, showStats)
                        }
                    )
                }
            }
        }
    }
}
