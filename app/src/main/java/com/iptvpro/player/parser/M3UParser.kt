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
