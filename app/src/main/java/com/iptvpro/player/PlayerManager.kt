package com.iptvpro.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource

class PlayerManager(private val context: Context) {

    private val TAG = "IPTV_PLAYER"
    private val defaultUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent(defaultUserAgent)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(15000)

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(
            DefaultMediaSourceFactory(context)
                .setDataSourceFactory(httpDataSourceFactory)
        )
        .build()

    init {
        setupErrorHandling()
    }

    private fun setupErrorHandling() {
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                
                Log.e(TAG, "------------------ PLAYER HATA DETAYI ------------------")
                Log.e(TAG, "Hata Mesajı: ${error.message}")
                Log.e(TAG, "Hata Kodu Name: ${error.errorCodeName} (${error.errorCode})")

                val cause = error.cause
                if (cause is HttpDataSource.InvalidResponseCodeException) {
                    Log.e(TAG, "-> HTTP Yanıt Kodu: ${cause.responseCode}")
                    Log.e(TAG, "-> Yanıt Mesajı: ${cause.responseMessage}")
                    Log.e(TAG, "-> Bağlanılmaya Çalışılan URL: ${cause.dataSpec.uri}")
                } else if (cause != null) {
                    Log.e(TAG, "-> Alt Sebep: ${cause.localizedMessage}")
                }
                Log.e(TAG, "--------------------------------------------------------")
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> Log.d(TAG, "Yayın tamponlanıyor (Buffering)...")
                    Player.STATE_READY -> Log.d(TAG, "Yayın yükleme başarılı, oynatılıyor.")
                    Player.STATE_ENDED -> Log.d(TAG, "Yayın akışı sona erdi.")
                    Player.STATE_IDLE -> Log.d(TAG, "Oynatıcı boşta (Idle).")
                }
            }
        })
    }

    @OptIn(UnstableApi::class)
    fun playStream(url: String, headers: Map<String, String> = emptyMap()) {
        if (headers.isNotEmpty()) {
            httpDataSourceFactory.setDefaultRequestProperties(headers)
        }

        val mediaItem = MediaItem.fromUri(url)
        
        val mediaSource = when {
            url.contains(".m3u8", ignoreCase = true) -> {
                HlsMediaSource.Factory(httpDataSourceFactory).createMediaSource(mediaItem)
            }
            url.contains(".mpd", ignoreCase = true) -> {
                DashMediaSource.Factory(httpDataSourceFactory).createMediaSource(mediaItem)
            }
            else -> {
                ProgressiveMediaSource.Factory(httpDataSourceFactory).createMediaSource(mediaItem)
            }
        }

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true
    }

    fun release() {
        player.release()
    }
}
