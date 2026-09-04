package cl.ozzylatorlabs.cosmoratv

object ChannelCatalog {
    // Cosmora TV V1.1
    // Criterio para publicación: reproductor nativo solo cuando la fuente está verificada para integración.
    // Para señales nacionales sin autorización expresa de redistribución, Cosmora abre la señal oficial del canal.
    val channels = listOf(
        Channel(
            name = "Mega",
            category = "Chile · Nacional · Señal oficial",
            websiteUrl = "https://www.mega.cl/senal-en-vivo/",
            note = "Abrir señal oficial de Mega"
        ),
        Channel(
            name = "Chilevisión",
            category = "Chile · Nacional · Señal oficial",
            websiteUrl = "https://www.chilevision.cl/senal-online/",
            note = "Abrir señal oficial de Chilevisión"
        ),
        Channel(
            name = "Canal 13",
            category = "Chile · Nacional · Señal oficial",
            websiteUrl = "https://www.13go.cl/en-vivo?sid=13",
            note = "Abrir señal oficial de Canal 13"
        ),
        Channel(
            name = "TVN",
            category = "Chile · Nacional · Player oficial",
            webEmbedUrl = "https://live.tvn.cl/?volume=0",
            websiteUrl = "https://www.tvn.cl/embed-no-borrar-senal-en-vivo",
            note = "Player oficial de TVN"
        ),
        Channel(
            name = "24 Horas",
            category = "Chile · Noticias",
            streamUrl = "https://mdstrm.com/live-stream-playlist/689ba606ecfe7915e1f8f741.m3u8",
            websiteUrl = "https://www.24horas.cl/envivo/",
            note = "Señal 24 Horas",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TVN3",
            category = "Chile · TVN",
            streamUrl = "https://mdstrm.com/live-stream-playlist/5653641561b4eba30a7e4929.m3u8",
            websiteUrl = "https://www.tvn.cl/tvn3",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TV Chile",
            category = "Chile · TVN Internacional",
            streamUrl = "https://mdstrm.com/live-stream-playlist/533adcc949386ce765657d7c.m3u8",
            websiteUrl = "https://www.tvn.cl/tvchile/envivo",
            verifiedDirectStream = true
        ),
        Channel(
            name = "NTV",
            category = "Chile · Cultura · TVN",
            streamUrl = "https://mdstrm.com/live-stream-playlist/5aaabe9e2c56420918184c6d.m3u8",
            websiteUrl = "https://www.tvn.cl/ntv",
            verifiedDirectStream = true
        ),
        Channel(
            name = "T13",
            category = "Chile · Noticias · Señal oficial",
            websiteUrl = "https://www.t13.cl/en-vivo",
            note = "Abrir señal oficial de T13"
        ),
        Channel(
            name = "TV+",
            category = "Chile · Entretención · Señal oficial",
            websiteUrl = "https://www.tvmas.tv/page/en-vivo/",
            note = "Abrir señal oficial de TV+"
        ),
        Channel(
            name = "Canal 9 Bío Bío",
            category = "Chile · Regional · Player oficial",
            webEmbedUrl = "https://rudo.video/live/c9",
            websiteUrl = "https://www.canal9.cl/",
            note = "Player enlazado por Canal 9"
        ),
        Channel(
            name = "TVU",
            category = "Chile · Universidad de Concepción · Player oficial",
            webEmbedUrl = "https://rudo.video/live/tvu",
            websiteUrl = "https://www.tvu.cl/vivo",
            note = "Player enlazado por TVU"
        ),
        Channel(
            name = "UChile TV",
            category = "Chile · Universidad de Chile · Player oficial",
            webEmbedUrl = "https://rudo.video/live/uchiletv",
            websiteUrl = "https://tv.uchile.cl/",
            note = "Player enlazado por UChileTV"
        ),
        Channel(
            name = "Antofagasta TV",
            category = "Chile · Regional",
            streamUrl = "https://unlimited6-cl.dps.live/atv/atv.smil/playlist.m3u8",
            websiteUrl = "https://www.antofagasta.tv/senal-en-vivo",
            verifiedDirectStream = true
        ),
        Channel(
            name = "UCV TV",
            category = "Chile · Valparaíso",
            streamUrl = "https://unlimited2-cl-isp.dps.live/ucvtv2/ucvtv2.smil/playlist.m3u8",
            websiteUrl = "https://www.ucvtv.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "ADN Noticias",
            category = "México · Noticias",
            streamUrl = "https://mdstrm.com/live-stream-playlist/60b578b060947317de7b57ac.m3u8",
            websiteUrl = "https://www.adn40.mx/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Supermúsica TV",
            category = "Música",
            streamUrl = "https://backupmaxmedia.hvmultiplay.com/hls/stream4/supermusica.m3u8",
            websiteUrl = "https://supermusica.tv/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Sol Música",
            category = "España · Música",
            streamUrl = "https://d2glyu450vvghm.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-21u4g5cjglv02/sm.m3u8",
            websiteUrl = "https://www.solmusica.com/",
            verifiedDirectStream = true
        )
    )
}
