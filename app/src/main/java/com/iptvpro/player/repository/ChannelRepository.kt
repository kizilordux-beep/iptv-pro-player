package com.iptvpro.player.repository
import com.iptvpro.player.model.*
import com.iptvpro.player.parser.*
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChannelRepository {
    private val client = OkHttpClient.Builder().followRedirects(true).followSslRedirects(true).build()
    private val m3uUrls = listOf("https://room.coinmybook.workers.dev/playlist.m3u")
    private val manifestUrls = listOf("https://tvvoo.hayd.uk/cfg-tr/manifest.json")
    private var cached = listOf<Channel>()
    private var epgMap = mapOf<String, List<EpgProgram>>()
    private var addons = listOf<AddonSource>()

    suspend fun loadAll(): List<Channel> = withContext(Dispatchers.IO) {
        val all = mutableListOf<Channel>()
        m3uUrls.forEach { u -> try { all.addAll(M3UParser.parse(fetch(u))) } catch(_:Exception){} }
        manifestUrls.forEach { u ->
            try {
                val base = u.substringBeforeLast("/")
                val addon = ManifestParser.parseManifest(fetch(u), base)
                addons = addons + addon
                addon.catalogs.forEach { c ->
                    try { all.addAll(ManifestParser.parseCatalog(fetch("$base/catalog/${c.type}/${c.id}.json"), addon.name)) }
                    catch(_:Exception){}
                }
                if (addon.catalogs.isEmpty()) try {
                    ManifestParser.parseStreams(fetch("$base/stream/tv/.json")).forEachIndexed { i, s ->
                        all.add(Channel("a_${i}", s.name, s.url, group = addon.name,
                            sourceType = SourceType.MANIFEST_ADDON, isAddon = true, addonName = addon.name))
                    }
                } catch(_:Exception){}
            } catch(_:Exception){}
        }
        cached = all; all
    }
    suspend fun resolveUrl(ch: Channel): String = withContext(Dispatchers.IO) {
        if (ch.url.isNotEmpty()) return@withContext ch.url
        addons.find { it.name == ch.addonName }?.let { a ->
            try {
                val s = ManifestParser.parseStreams(fetch("${a.baseUrl}/stream/${ch.id.replace(":","%3A")}.json"))
                if (s.isNotEmpty()) return@withContext s[0].url
            } catch(_:Exception){}
        }; ch.url
    }
    fun getNow(chId: String) = epgMap[chId]?.find { it.isLive }
    fun getNext(chId: String) = epgMap[chId]?.filter { it.startTime > System.currentTimeMillis() }?.minByOrNull { it.startTime }
    fun getEpg(chId: String) = epgMap[chId] ?: emptyList()
    fun getGroups() = (listOf("Tümü") + cached.map { it.group }.distinct()).distinct()
    fun getByGroup(g: String) = if (g == "Tümü") cached else cached.filter { it.group == g }

    private suspend fun fetch(url: String): String = suspendCoroutine { c ->
        client.newCall(Request.Builder().url(url)
            .header("User-Agent","Mozilla/5.0 IPTVPro/1.0").build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { c.resumeWithException(e) }
                override fun onResponse(call: Call, r: Response) { c.resume(r.body?.string()?:"") }
            })
    }
}
