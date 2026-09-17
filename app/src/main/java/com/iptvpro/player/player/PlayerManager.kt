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
