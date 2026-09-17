package com.iptvpro.player.model
data class Channel(
    val id: String = "", val name: String, val url: String,
    val logo: String = "", val group: String = "Genel",
    val epgId: String = "", val sourceType: SourceType = SourceType.M3U,
    val headers: Map<String, String> = emptyMap(),
    val isAddon: Boolean = false, val addonName: String = ""
)
enum class SourceType { M3U, MANIFEST_ADDON, WORKER }
