#!/data/data/com.termux/files/usr/bin/bash
set -e
echo "📁 Dizin yapısı oluşturuluyor..."

PKG="app/src/main/java/com/iptvpro/player"
mkdir -p $PKG/{theme,model,parser,repository,viewmodel,player,ui}
mkdir -p app/src/main/res/{values,drawable,mipmap-hdpi}
mkdir -p gradle/wrapper

echo "📝 Dosyalar yazılıyor..."

# ─── .gitignore ───
cat << 'EOF' > .gitignore
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
/app/build
EOF

# ─── settings.gradle.kts ───
cat << 'EOF' > settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "IPTVPro"
include(":app")
EOF

# ─── build.gradle.kts (root) ───
cat << 'EOF' > build.gradle.kts
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}
EOF

# ─── gradle.properties ───
cat << 'EOF' > gradle.properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
EOF

# ─── gradle-wrapper.properties ───
cat << 'EOF' > gradle/wrapper/gradle-wrapper.properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF

# ─── app/build.gradle.kts ───
cat << 'EOF' > app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "com.iptvpro.player"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.iptvpro.player"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.5.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.5.0")
    implementation("androidx.media3:media3-ui:1.5.0")
    implementation("androidx.media3:media3-datasource-okhttp:1.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.code.gson:gson:2.11.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
EOF

# ─── AndroidManifest.xml ───
cat << 'EOF' > app/src/main/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-feature android:name="android.software.leanback" android:required="false"/>
    <uses-feature android:name="android.hardware.touchscreen" android:required="false"/>
    <application
        android:name=".IPTVApplication"
        android:allowBackup="true"
        android:banner="@mipmap/ic_launcher"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.IPTVPro">
        <activity
            android:name=".MainActivity"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:exported="true"
            android:screenOrientation="landscape">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
                <category android:name="android.intent.category.LEANBACK_LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>
</manifest>
EOF

# ─── strings.xml ───
cat << 'EOF' > app/src/main/res/values/strings.xml
<?xml version="1.0" encoding="utf-8"?>
<resources><string name="app_name">IPTV Pro</string></resources>
EOF

# ─── themes.xml ───
cat << 'EOF' > app/src/main/res/values/themes.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.IPTVPro" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="android:windowBackground">@android:color/black</item>
        <item name="android:windowFullscreen">true</item>
    </style>
</resources>
EOF

# ─── IPTVApplication.kt ───
cat << 'EOF' > $PKG/IPTVApplication.kt
package com.iptvpro.player
import android.app.Application
class IPTVApplication : Application()
EOF

# ─── MainActivity.kt ───
cat << 'EOF' > $PKG/MainActivity.kt
package com.iptvpro.player
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.iptvpro.player.theme.IPTVProTheme
import com.iptvpro.player.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val c = WindowInsetsControllerCompat(window, window.decorView)
        c.hide(WindowInsetsCompat.Type.systemBars())
        c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        enableEdgeToEdge()
        setContent { IPTVProTheme { MainScreen() } }
    }
}
EOF

# ─── Theme.kt ───
cat << 'EOF' > $PKG/theme/Theme.kt
package com.iptvpro.player.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CrimsonRed = Color(0xFFDC143C)
val DarkRed = Color(0xFF8B0000)
val DeepBlack = Color(0xFF0A0A0A)
val SurfaceBlack = Color(0xFF1A1A1A)
val CardBlack = Color(0xFF252525)
val DimWhite = Color(0xFFE0E0E0)
val BrightWhite = Color(0xFFFFFFFF)
val AccentRed = Color(0xFFFF4444)
val SubtleGray = Color(0xFF888888)
val DarkGray = Color(0xFF333333)
val EpgNow = Color(0xFF00C853)
val EpgNext = Color(0xFFFFAB00)

private val Scheme = darkColorScheme(
    primary = CrimsonRed, onPrimary = BrightWhite,
    primaryContainer = DarkRed, onPrimaryContainer = BrightWhite,
    secondary = AccentRed, onSecondary = BrightWhite,
    background = DeepBlack, onBackground = DimWhite,
    surface = SurfaceBlack, onSurface = DimWhite,
    surfaceVariant = CardBlack, onSurfaceVariant = SubtleGray,
    outline = DarkGray, error = Color(0xFFFF6B6B)
)

@Composable
fun IPTVProTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
EOF

# ─── Channel.kt ───
cat << 'EOF' > $PKG/model/Channel.kt
package com.iptvpro.player.model
data class Channel(
    val id: String = "", val name: String, val url: String,
    val logo: String = "", val group: String = "Genel",
    val epgId: String = "", val sourceType: SourceType = SourceType.M3U,
    val headers: Map<String, String> = emptyMap(),
    val isAddon: Boolean = false, val addonName: String = ""
)
enum class SourceType { M3U, MANIFEST_ADDON, WORKER }
EOF

# ─── EpgProgram.kt ───
cat << 'EOF' > $PKG/model/EpgProgram.kt
package com.iptvpro.player.model
data class EpgProgram(
    val channelId: String, val title: String, val description: String = "",
    val startTime: Long, val endTime: Long, val category: String = ""
) {
    val isLive: Boolean get() = System.currentTimeMillis() in startTime..endTime
    val progress: Float get() {
        val n = System.currentTimeMillis()
        if (n < startTime) return 0f; if (n > endTime) return 1f
        return (n - startTime).toFloat() / (endTime - startTime).toFloat()
    }
}
EOF

# ─── AddonSource.kt ───
cat << 'EOF' > $PKG/model/AddonSource.kt
package com.iptvpro.player.model
data class AddonSource(
    val id: String, val name: String, val version: String = "1.0",
    val description: String = "", val catalogs: List<AddonCatalog> = emptyList(),
    val baseUrl: String = ""
)
data class AddonCatalog(val type: String, val id: String, val name: String)
data class AddonStream(val url: String, val title: String = "", val name: String = "")
EOF

# ─── M3UParser.kt ───
cat << 'EOF' > $PKG/parser/M3UParser.kt
package com.iptvpro.player.parser
import com.iptvpro.player.model.*

object M3UParser {
    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF:")) {
                val name = line.substringAfterLast(',', "Kanal")
                val logo = Regex("""tvg-logo="([^"]*?)"""").find(line)?.groupValues?.get(1) ?: ""
                val group = Regex("""group-title="([^"]*?)"""").find(line)?.groupValues?.get(1) ?: "Genel"
                val epgId = Regex("""tvg-id="([^"]*?)"""").find(line)?.groupValues?.get(1) ?: ""
                val headers = mutableMapOf<String, String>()
                var url = ""; var j = i + 1
                while (j < lines.size) {
                    val nl = lines[j].trim()
                    when {
                        nl.startsWith("#EXTVLCOPT:http-referrer=") -> headers["Referer"] = nl.substringAfter("=")
                        nl.startsWith("#EXTVLCOPT:http-user-agent=") -> headers["User-Agent"] = nl.substringAfter("=")
                        nl.startsWith("#") -> {}
                        nl.isNotEmpty() -> { url = nl; break }
                    }; j++
                }
                if (url.startsWith("http") || url.startsWith("rtmp"))
                    channels.add(Channel(id = "${channels.size}", name = name, url = url,
                        logo = logo, group = group, epgId = epgId, headers = headers))
                i = j + 1
            } else i++
        }
        return channels
    }
}
EOF

# ─── ManifestParser.kt ───
cat << 'EOF' > $PKG/parser/ManifestParser.kt
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
EOF

# ─── EpgParser.kt ───
cat << 'EOF' > $PKG/parser/EpgParser.kt
package com.iptvpro.player.parser
import com.iptvpro.player.model.EpgProgram
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object EpgParser {
    private val df = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US)
    fun parse(input: InputStream): List<EpgProgram> {
        val list = mutableListOf<EpgProgram>()
        try {
            val p = XmlPullParserFactory.newInstance().newPullParser()
            p.setInput(input, "UTF-8")
            var ch = ""; var title = ""; var desc = ""; var st = 0L; var et = 0L; var tag = ""; var inP = false
            while (p.eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (p.eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> {
                        tag = p.name
                        if (p.name == "programme") {
                            inP = true; ch = p.getAttributeValue(null,"channel") ?: ""
                            st = try { df.parse(p.getAttributeValue(null,"start")?:"")?.time?:0 } catch(_:Exception){0}
                            et = try { df.parse(p.getAttributeValue(null,"stop")?:"")?.time?:0 } catch(_:Exception){0}
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.TEXT -> if (inP) when(tag) {
                        "title" -> title = p.text?.trim()?:""
                        "desc" -> desc = p.text?.trim()?:""
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> if (p.name == "programme" && inP) {
                        if (ch.isNotEmpty() && title.isNotEmpty())
                            list.add(EpgProgram(ch, title, desc, st, et))
                        ch=""; title=""; desc=""; st=0; et=0; inP=false
                    }
                }; p.next()
            }
        } catch (_: Exception) {}
        return list
    }
}
EOF

# ─── ChannelRepository.kt ───
cat << 'EOF' > $PKG/repository/ChannelRepository.kt
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
EOF

# ─── PlayerManager.kt ───
cat << 'EOF' > $PKG/player/PlayerManager.kt
package com.iptvpro.player.player
import android.content.Context
import androidx.media3.common.*
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.iptvpro.player.model.Channel

class PlayerManager(ctx: Context) {
    private val ts = DefaultTrackSelector(ctx).apply {
        setParameters(buildUponParameters().setMaxVideoSizeSd().setPreferredAudioLanguage("tur"))
    }
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(ctx).setTrackSelector(ts)
        .setHandleAudioBecomingNoisy(true).build().apply { playWhenReady = true }

    fun play(ch: Channel) {
        if (ch.url.isEmpty()) return
        val dsf = DefaultHttpDataSource.Factory().apply {
            if (ch.headers.isNotEmpty()) setDefaultRequestProperties(ch.headers)
            setUserAgent(ch.headers["User-Agent"] ?: "IPTVPro/1.0")
            setConnectTimeoutMs(15000); setReadTimeoutMs(15000)
            setAllowCrossProtocolRedirects(true)
        }
        val src = when {
            ch.url.contains(".mpd", true) -> DashMediaSource.Factory(dsf).createMediaSource(MediaItem.fromUri(ch.url))
            else -> HlsMediaSource.Factory(dsf).setAllowChunklessPreparation(true).createMediaSource(MediaItem.fromUri(ch.url))
        }
        exoPlayer.stop(); exoPlayer.setMediaSource(src); exoPlayer.prepare()
    }
    fun setQuality(q: String) = ts.setParameters(ts.buildUponParameters().apply {
        when(q) { "SD" -> setMaxVideoSizeSd(); "HD" -> setMaxVideoSize(1920,1080); else -> clearVideoSizeConstraints() }
    })
    fun release() = exoPlayer.release()
}
EOF

# ─── PlayerViewModel.kt ───
cat << 'EOF' > $PKG/viewmodel/PlayerViewModel.kt
package com.iptvpro.player.viewmodel
import android.app.Application
import androidx.lifecycle.*
import com.iptvpro.player.model.*
import com.iptvpro.player.player.PlayerManager
import com.iptvpro.player.repository.ChannelRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PlayerViewModel(app: Application) : AndroidViewModel(app) {
    val pm = PlayerManager(app)
    private val repo = ChannelRepository()
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()
    private val _current = MutableStateFlow<Channel?>(null)
    val current = _current.asStateFlow()
    private val _idx = MutableStateFlow(0)
    val idx = _idx.asStateFlow()
    private val _groups = MutableStateFlow(listOf("Tümü"))
    val groups = _groups.asStateFlow()
    private val _group = MutableStateFlow("Tümü")
    val group = _group.asStateFlow()
    private val _filtered = MutableStateFlow<List<Channel>>(emptyList())
    val filtered = _filtered.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    private val _showList = MutableStateFlow(false)
    val showList = _showList.asStateFlow()
    private val _showBottom = MutableStateFlow(false)
    val showBottom = _showBottom.asStateFlow()
    private val _showEpg = MutableStateFlow(false)
    val showEpg = _showEpg.asStateFlow()
    private val _showMenu = MutableStateFlow(false)
    val showMenu = _showMenu.asStateFlow()
    private val _now = MutableStateFlow<EpgProgram?>(null)
    val now = _now.asStateFlow()
    private val _next = MutableStateFlow<EpgProgram?>(null)
    val next = _next.asStateFlow()
    private val _epgList = MutableStateFlow<List<EpgProgram>>(emptyList())
    val epgList = _epgList.asStateFlow()
    private val _state = MutableStateFlow(PState.IDLE)
    val state = _state.asStateFlow()
    private val _err = MutableStateFlow<String?>(null)
    val err = _err.asStateFlow()
    private var hideJob: Job? = null

    init { load() }

    private fun load() = viewModelScope.launch {
        _loading.value = true
        try {
            val all = repo.loadAll()
            _channels.value = all; _groups.value = repo.getGroups(); _filtered.value = all
            if (all.isNotEmpty()) select(0)
        } catch (e: Exception) { _err.value = e.message }
        finally { _loading.value = false }
    }

    fun select(i: Int) {
        val list = _filtered.value
        if (i < 0 || i >= list.size) return
        _current.value = list[i]; _idx.value = i
        viewModelScope.launch {
            _state.value = PState.LOADING
            try {
                val url = if (list[i].url.isEmpty() && list[i].isAddon) repo.resolveUrl(list[i]) else list[i].url
                pm.play(list[i].copy(url = url)); _state.value = PState.PLAYING
                val eid = list[i].epgId.ifEmpty { list[i].id }
                _now.value = repo.getNow(eid); _next.value = repo.getNext(eid); _epgList.value = repo.getEpg(eid)
                showBottomTemp()
            } catch (e: Exception) { _state.value = PState.ERROR; _err.value = e.message }
        }
    }
    fun chUp() { val s = _filtered.value.size; if (s>0) select((_idx.value+1)%s) }
    fun chDown() { val s = _filtered.value.size; if (s>0) select(if (_idx.value-1<0) s-1 else _idx.value-1) }
    fun setGroup(g: String) { _group.value = g; _filtered.value = repo.getByGroup(g) }
    fun toggleList() { _showList.value = !_showList.value; _showMenu.value = false; _showEpg.value = false }
    fun toggleBottom() { _showBottom.value = !_showBottom.value; showBottomTemp() }
    fun toggleEpg() { _showEpg.value = !_showEpg.value; _showList.value = false; _showMenu.value = false }
    fun toggleMenu() { _showMenu.value = !_showMenu.value; _showList.value = false; _showEpg.value = false }
    fun hideAll() { _showList.value = false; _showBottom.value = false; _showEpg.value = false; _showMenu.value = false }
    fun showBottomTemp() { _showBottom.value = true; hideJob?.cancel(); hideJob = viewModelScope.launch { delay(5000); _showBottom.value = false } }
    fun refresh() = load()
    fun clearErr() { _err.value = null }
    override fun onCleared() { super.onCleared(); pm.release() }
}
enum class PState { IDLE, LOADING, PLAYING, PAUSED, ERROR }
EOF

# ─── PlayerScreen.kt ───
cat << 'EOF' > $PKG/ui/PlayerScreen.kt
package com.iptvpro.player.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun PlayerScreen(vm: PlayerViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Color.Black).clickable { vm.showBottomTemp() }) {
        AndroidView(
            factory = { c -> PlayerView(c).apply { player = vm.pm.exoPlayer; useController = false; keepScreenOn = true } },
            modifier = Modifier.fillMaxSize(),
            update = { it.player = vm.pm.exoPlayer }
        )
    }
}
EOF

# ─── ChannelListPanel.kt ───
cat << 'EOF' > $PKG/ui/ChannelListPanel.kt
package com.iptvpro.player.ui
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun ChannelListPanel(vm: PlayerViewModel) {
    val channels by vm.filtered.collectAsState()
    val ci by vm.idx.collectAsState()
    val groups by vm.groups.collectAsState()
    val sg by vm.group.collectAsState()
    val ls = rememberLazyListState()
    LaunchedEffect(ci) { if (channels.isNotEmpty()) ls.animateScrollToItem(ci.coerceIn(0, channels.size-1)) }

    Surface(
        modifier = Modifier.width(360.dp).fillMaxHeight().padding(8.dp),
        shape = RoundedCornerShape(16.dp), color = DeepBlack.copy(.95f),
        border = BorderStroke(1.dp, CrimsonRed.copy(.3f)), shadowElevation = 16.dp
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(DarkRed, CrimsonRed))).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LiveTv, null, tint = BrightWhite, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Kanallar", color = BrightWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("${channels.size}", color = BrightWhite.copy(.7f), fontSize = 13.sp)
                }
            }
            LazyRow(modifier = Modifier.fillMaxWidth().background(SurfaceBlack).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(groups) { g ->
                    val sel = g == sg
                    Surface(modifier = Modifier.clickable { vm.setGroup(g) }, shape = RoundedCornerShape(20.dp),
                        color = if (sel) CrimsonRed else CardBlack, border = if (!sel) BorderStroke(1.dp, DarkGray) else null) {
                        Text(g, modifier = Modifier.padding(10.dp, 6.dp), color = if (sel) BrightWhite else SubtleGray, fontSize = 12.sp)
                    }
                }
            }
            LazyColumn(state = ls, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                itemsIndexed(channels) { i, ch ->
                    val sel = i == ci
                    val bg by animateColorAsState(if (sel) CrimsonRed.copy(.3f) else Color.Transparent, label = "bg")
                    Surface(modifier = Modifier.fillMaxWidth().clickable { vm.select(i) },
                        shape = RoundedCornerShape(12.dp), color = bg,
                        border = if (sel) BorderStroke(1.dp, CrimsonRed) else null) {
                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${i+1}", color = if (sel) CrimsonRed else SubtleGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                            if (ch.logo.isNotEmpty()) AsyncImage(ch.logo, ch.name, modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(CardBlack), contentScale = ContentScale.Fit)
                            else Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(CardBlack), contentAlignment = Alignment.Center) { Icon(Icons.Default.Tv, null, tint = SubtleGray, modifier = Modifier.size(18.dp)) }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(ch.name, color = if (sel) BrightWhite else DimWhite, fontSize = 14.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(ch.group, color = SubtleGray, fontSize = 11.sp)
                            }
                            if (ch.isAddon) Surface(shape = RoundedCornerShape(4.dp), color = AccentRed.copy(.2f)) {
                                Text("A", Modifier.padding(6.dp, 2.dp), color = AccentRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
EOF

# ─── BottomInfoPanel.kt ───
cat << 'EOF' > $PKG/ui/BottomInfoPanel.kt
package com.iptvpro.player.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BottomInfoPanel(vm: PlayerViewModel) {
    val ch by vm.current.collectAsState()
    val ci by vm.idx.collectAsState()
    val now by vm.now.collectAsState()
    val next by vm.next.collectAsState()
    val st by vm.state.collectAsState()
    val tf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val clock = remember { mutableStateOf("") }
    LaunchedEffect(Unit) { while(true) { clock.value = tf.format(Date()); kotlinx.coroutines.delay(1000) } }

    Surface(modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp), shape = RoundedCornerShape(20.dp), color = Color.Transparent, shadowElevation = 24.dp) {
        Box(modifier = Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(DeepBlack.copy(.85f), DeepBlack.copy(.95f))), RoundedCornerShape(20.dp))
            .border(1.dp, Brush.horizontalGradient(listOf(CrimsonRed.copy(.5f), DarkRed.copy(.3f))), RoundedCornerShape(20.dp))
            .padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = CrimsonRed, modifier = Modifier.size(52.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("${ci+1}", color = BrightWhite, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }
                }
                Spacer(Modifier.width(12.dp))
                ch?.let { if (it.logo.isNotEmpty()) { AsyncImage(it.logo, it.name, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(CardBlack), contentScale = ContentScale.Fit); Spacer(Modifier.width(12.dp)) } }
                Column(Modifier.weight(1f)) {
                    Text(ch?.name ?: "Kanal Seçiniz", color = BrightWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(EpgNow)); Spacer(Modifier.width(6.dp))
                        Text("Şimdi: ${now?.title ?: ch?.group ?: ""}", color = EpgNow, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    now?.let { Spacer(Modifier.height(3.dp)); LinearProgressIndicator(progress = { it.progress }, modifier = Modifier.fillMaxWidth(.7f).height(3.dp).clip(RoundedCornerShape(2.dp)), color = CrimsonRed, trackColor = DarkGray) }
                    next?.let { Spacer(Modifier.height(3.dp)); Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(EpgNext)); Spacer(Modifier.width(6.dp))
                        Text("Sıradaki: ${it.title}", color = EpgNext, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    } }
                }
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    val sc = when(st) { PState.PLAYING -> EpgNow; PState.LOADING -> EpgNext; PState.ERROR -> AccentRed; else -> SubtleGray }
                    val si = when(st) { PState.PLAYING -> Icons.Default.PlayArrow; PState.LOADING -> Icons.Default.HourglassTop; PState.PAUSED -> Icons.Default.Pause; PState.ERROR -> Icons.Default.Error; else -> Icons.Default.Stop }
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(si, null, tint = sc, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp))
                        Text(when(st) { PState.PLAYING -> "CANLI"; PState.LOADING -> "Yükleniyor"; PState.PAUSED -> "Duraklatıldı"; PState.ERROR -> "Hata"; else -> "" }, color = sc, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = CardBlack, border = BorderStroke(1.dp, DarkGray)) {
                        Text(clock.value, Modifier.padding(12.dp, 4.dp), color = BrightWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    ch?.let { Surface(shape = RoundedCornerShape(4.dp), color = if (it.isAddon) AccentRed.copy(.2f) else DarkGray.copy(.5f)) {
                        Text(if (it.isAddon) "ADDON" else "M3U", Modifier.padding(8.dp, 2.dp), color = if (it.isAddon) AccentRed else SubtleGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    } }
                }
            }
        }
    }
}
EOF

# ─── EpgPanel.kt ───
cat << 'EOF' > $PKG/ui/EpgPanel.kt
package com.iptvpro.player.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iptvpro.player.model.EpgProgram
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EpgPanel(vm: PlayerViewModel) {
    val ch by vm.current.collectAsState()
    val epg by vm.epgList.collectAsState()
    val now by vm.now.collectAsState()
    val next by vm.next.collectAsState()
    val tf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Surface(modifier = Modifier.fillMaxWidth().fillMaxHeight(.55f).padding(12.dp, 8.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = DeepBlack.copy(.97f), border = BorderStroke(1.dp, CrimsonRed.copy(.4f)), shadowElevation = 24.dp) {
        Column {
            Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(DarkRed, CrimsonRed, DarkRed))).padding(20.dp, 14.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.EventNote, null, tint = BrightWhite, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(8.dp)); Text("Program Rehberi", color = BrightWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
                    Text(ch?.name ?: "", color = BrightWhite.copy(.7f), fontSize = 13.sp)
                }
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EpgCard("ŞİMDİ", EpgNow, now?.title, now, tf)
                EpgCard("SIRADAKİ", EpgNext, next?.title, next, tf)
            }
            if (epg.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.EventBusy, null, tint = SubtleGray, modifier = Modifier.size(44.dp)); Spacer(Modifier.height(8.dp)); Text("EPG mevcut değil", color = SubtleGray, fontSize = 13.sp) }
            } else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(epg) { p ->
                    val live = p.isLive; val past = p.endTime < System.currentTimeMillis()
                    Surface(shape = RoundedCornerShape(10.dp), color = if (live) CrimsonRed.copy(.15f) else CardBlack.copy(.3f),
                        border = if (live) BorderStroke(1.dp, CrimsonRed.copy(.5f)) else null) {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.width(56.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tf.format(Date(p.startTime)), color = if (live) CrimsonRed else if (past) SubtleGray.copy(.5f) else DimWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(tf.format(Date(p.endTime)), color = SubtleGray.copy(.6f), fontSize = 10.sp)
                            }
                            Box(Modifier.width(3.dp).height(32.dp).clip(RoundedCornerShape(2.dp)).background(if (live) CrimsonRed else DarkGray))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) { Text(p.title, color = if (past) SubtleGray.copy(.5f) else BrightWhite, fontSize = 13.sp, fontWeight = if (live) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            if (live) Surface(shape = RoundedCornerShape(6.dp), color = CrimsonRed) { Text("CANLI", Modifier.padding(8.dp, 3.dp), color = BrightWhite, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpgCard(label: String, color: androidx.compose.ui.graphics.Color, title: String?, prog: EpgProgram?, tf: SimpleDateFormat) {
    Surface(Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = color.copy(.1f), border = BorderStroke(1.dp, color.copy(.3f))) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(10.dp).clip(CircleShape).background(color)); Spacer(Modifier.width(6.dp)); Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(5.dp))
            Text(title ?: "Bilgi yok", color = BrightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            prog?.let { Spacer(Modifier.height(3.dp)); Text("${tf.format(Date(it.startTime))} - ${tf.format(Date(it.endTime))}", color = SubtleGray, fontSize = 10.sp)
                if (it.isLive) { Spacer(Modifier.height(4.dp)); LinearProgressIndicator(progress = { it.progress }, Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)), color = color, trackColor = DarkGray) }
            }
        }
    }
}
EOF

# ─── QuickMenu.kt ───
cat << 'EOF' > $PKG/ui/QuickMenu.kt
package com.iptvpro.player.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun QuickMenu(vm: PlayerViewModel) {
    var quality by remember { mutableStateOf("Auto") }
    Surface(modifier = Modifier.width(280.dp).fillMaxHeight().padding(8.dp),
        shape = RoundedCornerShape(16.dp), color = DeepBlack.copy(.97f),
        border = BorderStroke(1.dp, CrimsonRed.copy(.3f)), shadowElevation = 24.dp) {
        Column {
            Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(CrimsonRed, DarkRed))).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Tune, null, tint = BrightWhite, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(8.dp)); Text("Hızlı Menü", color = BrightWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
            }
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionTitle("Kalite", Icons.Default.HighQuality)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("SD","HD","Max").forEach { q ->
                        val s = quality == q
                        Surface(Modifier.weight(1f).clickable { quality = q; vm.pm.setQuality(q) },
                            shape = RoundedCornerShape(10.dp), color = if (s) CrimsonRed else CardBlack,
                            border = if (!s) BorderStroke(1.dp, DarkGray) else null) {
                            Text(q, Modifier.padding(vertical = 10.dp).fillMaxWidth(), color = BrightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                MItem(Icons.Default.Audiotrack, "Ses Parçası", "Varsayılan") {}
                MItem(Icons.Default.Subtitles, "Altyazı", "Kapalı") {}
                Divider(color = DarkGray, thickness = .5.dp, modifier = Modifier.padding(vertical = 4.dp))
                MItem(Icons.Default.EventNote, "Program Rehberi", "EPG") { vm.toggleEpg() }
                MItem(Icons.Default.FormatListBulleted, "Kanal Listesi", "Kanallar") { vm.toggleList() }
                Divider(color = DarkGray, thickness = .5.dp, modifier = Modifier.padding(vertical = 4.dp))
                MItem(Icons.Default.Refresh, "Yenile", "Tüm kaynaklar") { vm.refresh() }
                Spacer(Modifier.height(12.dp))
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = CardBlack, border = BorderStroke(1.dp, DarkGray.copy(.5f))) {
                    Column(Modifier.padding(12.dp)) {
                        Text("🎮 Kumanda", color = CrimsonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        listOf("▲▼" to "Kanal", "◄" to "Liste", "►" to "Menü", "OK" to "Bilgi", "INFO" to "EPG").forEach { (k, d) ->
                            Row(Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(4.dp), color = DarkGray, modifier = Modifier.width(42.dp)) { Text(k, Modifier.padding(4.dp, 2.dp), color = BrightWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
                                Spacer(Modifier.width(8.dp)); Text(d, color = SubtleGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable private fun SectionTitle(t: String, i: ImageVector) { Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Icon(i, null, tint = CrimsonRed, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text(t, color = CrimsonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
@Composable private fun MItem(icon: ImageVector, title: String, sub: String, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp), color = CardBlack.copy(.5f)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = CrimsonRed, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, color = BrightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold); Text(sub, color = SubtleGray, fontSize = 11.sp) }
            Icon(Icons.Default.ChevronRight, null, tint = SubtleGray, modifier = Modifier.size(16.dp))
        }
    }
}
EOF

# ─── MainScreen.kt ───
cat << 'EOF' > $PKG/ui/MainScreen.kt
package com.iptvpro.player.ui
import android.view.KeyEvent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iptvpro.player.theme.DeepBlack
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun MainScreen(vm: PlayerViewModel = viewModel()) {
    val loading by vm.loading.collectAsState()
    val showList by vm.showList.collectAsState()
    val showBot by vm.showBottom.collectAsState()
    val showEpg by vm.showEpg.collectAsState()
    val showMenu by vm.showMenu.collectAsState()
    val err by vm.err.collectAsState()

    Box(Modifier.fillMaxSize().background(DeepBlack).onKeyEvent { e ->
        if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
        when (e.nativeKeyEvent.keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> { vm.chUp(); true }
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> { vm.chDown(); true }
            KeyEvent.KEYCODE_DPAD_LEFT -> { vm.toggleList(); true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { vm.toggleMenu(); true }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { vm.toggleBottom(); true }
            KeyEvent.KEYCODE_BACK -> { vm.hideAll(); true }
            KeyEvent.KEYCODE_MENU -> { vm.toggleMenu(); true }
            KeyEvent.KEYCODE_INFO, KeyEvent.KEYCODE_TV_DATA_SERVICE, KeyEvent.KEYCODE_GUIDE -> { vm.toggleEpg(); true }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { vm.pm.exoPlayer.playWhenReady = !vm.pm.exoPlayer.playWhenReady; true }
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> { vm.select(e.nativeKeyEvent.keyCode - KeyEvent.KEYCODE_0); true }
            else -> false
        }
    }) {
        PlayerScreen(vm, Modifier.fillMaxSize())
        if (loading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 4.dp, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(14.dp)); Text("Kanallar yükleniyor...", color = Color.White)
            }
        }
        AnimatedVisibility(showList, enter = slideInHorizontally { -it } + fadeIn(), exit = slideOutHorizontally { -it } + fadeOut(), modifier = Modifier.align(Alignment.CenterStart)) { ChannelListPanel(vm) }
        AnimatedVisibility(showBot, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut(), modifier = Modifier.align(Alignment.BottomCenter)) { BottomInfoPanel(vm) }
        AnimatedVisibility(showEpg, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut(), modifier = Modifier.align(Alignment.BottomCenter)) { EpgPanel(vm) }
        AnimatedVisibility(showMenu, enter = slideInHorizontally { it } + fadeIn(), exit = slideOutHorizontally { it } + fadeOut(), modifier = Modifier.align(Alignment.CenterEnd)) { QuickMenu(vm) }
        err?.let { m -> Snackbar(modifier = Modifier.align(Alignment.TopCenter).padding(16.dp), action = { TextButton(onClick = { vm.clearErr() }) { Text("Kapat", color = Color.White) } }, containerColor = MaterialTheme.colorScheme.error) { Text(m, color = Color.White) } }
    }
}
EOF

# ─── README.md ───
cat << 'EOF' > README.md
# 📺 IPTV Pro Player
Siyah-Kırmızı temalı, kumanda destekli IPTV oynatıcı.

## Kaynaklar
- M3U: `room.coinmybook.workers.dev`
- Addon: `tvvoo.hayd.uk/cfg-tr/manifest.json`

## Kurulum
1. Android Studio'da açın
2. Gradle Sync yapın
3. Run

## Kumanda
| Tuş | İşlev |
|-----|-------|
| ▲▼ | Kanal değiştir |
| ◄ | Kanal listesi |
| ► | Hızlı menü |
| OK | Bilgi paneli |
| INFO | EPG rehber |
| 0-9 | Direkt kanal |
EOF

echo "✅ Tüm dosyalar oluşturuldu!"
echo "📊 Dosya sayısı: $(find . -type f | grep -v '.git/' | wc -l)"
