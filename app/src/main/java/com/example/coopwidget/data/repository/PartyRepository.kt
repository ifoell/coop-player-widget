package com.example.coopwidget.data.repository

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.example.coopwidget.data.datastore.PartyDataStore
import com.example.coopwidget.domain.model.PartyData
import com.example.coopwidget.domain.model.Player
import com.example.coopwidget.widget.CoOpWidget
import com.example.coopwidget.widget.SinglePlayerWidget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PartyRepository(
    private val context: Context,
    private val dataStore: PartyDataStore = PartyDataStore(context)
) {

    val partyDataFlow: Flow<PartyData> = dataStore.partyDataFlow

    suspend fun getPartyData(): PartyData {
        return partyDataFlow.first()
    }

    suspend fun savePartyData(partyData: PartyData) {
        dataStore.savePartyData(partyData)
        notifyWidgetUpdate()
    }

    suspend fun addPlayer(player: Player) {
        val current = getPartyData()
        val updatedPlayers = current.players + player
        savePartyData(current.copy(players = updatedPlayers))
    }

    suspend fun updatePlayer(player: Player) {
        val current = getPartyData()
        val updatedPlayers = current.players.map { existing ->
            if (existing.id == player.id) player.copy(updatedAt = System.currentTimeMillis()) else existing
        }
        savePartyData(current.copy(players = updatedPlayers))
    }

    suspend fun deletePlayer(playerId: String) {
        val current = getPartyData()
        val updatedPlayers = current.players.filterNot { it.id == playerId }
        savePartyData(current.copy(players = updatedPlayers))
    }

    suspend fun reorderPlayers(reordered: List<Player>) {
        val current = getPartyData()
        savePartyData(current.copy(players = reordered))
    }

    suspend fun updateSettings(
        familyName: String? = null,
        subTitle: String? = null,
        footerText: String? = null,
        showFooter: Boolean? = null,
        showStats: Boolean? = null,
        compactMode: Boolean? = null
    ) {
        val current = getPartyData()
        val updated = current.copy(
            familyName = familyName ?: current.familyName,
            subTitle = subTitle ?: current.subTitle,
            footerText = footerText ?: current.footerText,
            showFooter = showFooter ?: current.showFooter,
            showStats = showStats ?: current.showStats,
            compactMode = compactMode ?: current.compactMode
        )
        savePartyData(updated)
    }

    private suspend fun notifyWidgetUpdate() {
        try {
            CoOpWidget().updateAll(context)
            SinglePlayerWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
