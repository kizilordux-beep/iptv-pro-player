package com.iptvpro.player.model
data class AddonSource(
    val id: String, val name: String, val version: String = "1.0",
    val description: String = "", val catalogs: List<AddonCatalog> = emptyList(),
    val baseUrl: String = ""
)
data class AddonCatalog(val type: String, val id: String, val name: String)
data class AddonStream(val url: String, val title: String = "", val name: String = "")
