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
