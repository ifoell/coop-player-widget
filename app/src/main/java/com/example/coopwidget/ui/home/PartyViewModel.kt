package com.example.coopwidget.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopwidget.data.repository.PartyRepository
import com.example.coopwidget.domain.model.PartyData
import com.example.coopwidget.domain.model.Player
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PartyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PartyRepository(application)

    val partyDataState: StateFlow<PartyData> = repository.partyDataFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PartyData()
    )

    fun addPlayer(player: Player) {
        viewModelScope.launch {
            repository.addPlayer(player)
        }
    }

    fun updatePlayer(player: Player) {
        viewModelScope.launch {
            repository.updatePlayer(player)
        }
    }

    fun deletePlayer(playerId: String) {
        viewModelScope.launch {
            repository.deletePlayer(playerId)
        }
    }

    fun reorderPlayers(players: List<Player>) {
        viewModelScope.launch {
            repository.reorderPlayers(players)
        }
    }

    fun updateSettings(
        familyName: String,
        subTitle: String,
        footerText: String,
        showFooter: Boolean,
        showStats: Boolean
    ) {
        viewModelScope.launch {
            repository.updateSettings(
                familyName = familyName,
                subTitle = subTitle,
                footerText = footerText,
                showFooter = showFooter,
                showStats = showStats
            )
        }
    }
}
