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
