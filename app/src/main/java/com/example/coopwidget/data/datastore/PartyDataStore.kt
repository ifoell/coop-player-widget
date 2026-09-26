package com.example.coopwidget.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.coopwidget.domain.model.PartyData
import com.example.coopwidget.domain.model.Player
import com.example.coopwidget.domain.model.Stat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "coop_party_prefs")

class PartyDataStore(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    companion object {
        private val PARTY_DATA_KEY = stringPreferencesKey("party_data_json")
    }

    val partyDataFlow: Flow<PartyData> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[PARTY_DATA_KEY]
        if (jsonString.isNullOrBlank()) {
            val defaultData = createDefaultPartyData()
            // Asynchronously save initial default data if empty
            defaultData
        } else {
            try {
                json.decodeFromString<PartyData>(jsonString)
            } catch (e: Exception) {
                createDefaultPartyData()
            }
        }
    }

    suspend fun savePartyData(partyData: PartyData) {
        val jsonString = json.encodeToString(partyData)
        context.dataStore.edit { preferences ->
            preferences[PARTY_DATA_KEY] = jsonString
        }
    }

    private fun createDefaultPartyData(): PartyData {
        return PartyData(
            familyName = "CO-OP FAMILY",
            subTitle = "SAME TEAM • SAME LIFE",
            footerText = "SAME TEAM • BIGGER ADVENTURES",
            showFooter = true,
            showStats = true,
            compactMode = false,
            players = listOf(
                Player(
                    name = "Dad",
                    role = "DAD",
                    birthDate = "1991-05-10",
                    accentColorHex = "#2196F3",
                    tagline = "CODE • BUILD • SOLVE • PROTECT",
                    stats = listOf(
                        Stat(name = "STR", value = 85),
                        Stat(name = "DEF", value = 75),
                        Stat(name = "INT", value = 90),
                        Stat(name = "SPD", value = 70),
                        Stat(name = "LUK", value = 80)
                    )
                ),
                Player(
                    name = "Mom",
                    role = "MOM",
                    birthDate = "1994-08-15",
                    accentColorHex = "#FF4F7B",
                    tagline = "CARE • SUPPORT • BALANCE • KEEP US STRONG",
                    stats = listOf(
                        Stat(name = "CARE", value = 95),
                        Stat(name = "SUPPORT", value = 90),
                        Stat(name = "DEF", value = 75),
                        Stat(name = "INT", value = 85),
                        Stat(name = "LUK", value = 80)
                    )
                ),
                Player(
                    name = "Baby",
                    role = "BABY",
                    birthDate = "2026-01-10",
                    accentColorHex = "#31D17C",
                    tagline = "OUR GREATEST ADVENTURE",
                    stats = listOf(
                        Stat(name = "CUTE", value = 100),
                        Stat(name = "JOY", value = 100),
                        Stat(name = "HP", value = 90),
                        Stat(name = "SPD", value = 30),
                        Stat(name = "LUK", value = 99)
                    )
                )
            )
        )
    }
}
