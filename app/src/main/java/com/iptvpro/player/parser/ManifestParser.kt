package com.iptvpro.player.parser
import com.iptvpro.player.model.*
import org.json.JSONObject

object ManifestParser {
    fun parseManifest(json: String, baseUrl: String): AddonSource {
        val j = JSONObject(json)
        val cats = mutableListOf<AddonCatalog>()
        if (j.has("catalogs")) {
            val a = j.getJSONArray("catalogs")
            for (i in 0 until a.length()) {
                val c = a.getJSONObject(i)
                cats.add(AddonCatalog(c.optString("type","tv"), c.optString("id",""), c.optString("name","")))
            }
        }
        return AddonSource(j.optString("id","addon"), j.optString("name","Addon"),
            j.optString("version","1.0"), j.optString("description",""), cats, baseUrl)
    }
    fun parseCatalog(json: String, addonName: String): List<Channel> {
        val ch = mutableListOf<Channel>()
        try {
            val j = JSONObject(json)
            val arr = if (j.has("metas")) j.getJSONArray("metas") else return ch
            for (i in 0 until arr.length()) {
                val m = arr.getJSONObject(i)
                ch.add(Channel(id = m.optString("id","$i"), name = m.optString("name","Kanal $i"),
                    url = "", logo = m.optString("poster",""), group = addonName,
                    sourceType = SourceType.MANIFEST_ADDON, isAddon = true, addonName = addonName))
            }
        } catch (_: Exception) {}
        return ch
    }
    fun parseStreams(json: String): List<AddonStream> {
        val s = mutableListOf<AddonStream>()
        try {
            val j = JSONObject(json)
            if (j.has("streams")) {
                val a = j.getJSONArray("streams")
                for (i in 0 until a.length()) {
                    val st = a.getJSONObject(i)
                    val u = st.optString("url", st.optString("externalUrl",""))
                    if (u.isNotEmpty()) s.add(AddonStream(u, st.optString("title",""), st.optString("name","Stream")))
                }
            }
        } catch (_: Exception) {}
        return s
    }
}
