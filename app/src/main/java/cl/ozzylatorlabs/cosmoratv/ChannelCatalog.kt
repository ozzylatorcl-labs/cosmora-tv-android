package cl.ozzylatorlabs.cosmoratv

object ChannelCatalog {
    // Catálogo revisado para Cosmora TV.
    // Prioridad: player oficial enlazado por el propio canal, YouTube oficial o HLS público del proveedor del canal.
    // No se usan proxies jmp2.uk ni URLs extraídas desde APIs privadas de otras apps.
    // Las señales de 13Go que actualmente requieren suscripción no se incluyen como streams libres.
    val channels = listOf(
        Channel(
            name = "Mega",
            category = "Chile · Nacional",
            streamUrl = "https://unlimited1-cl-isp.dps.live/mega/mega.smil/playlist.m3u8",
            websiteUrl = "https://www.mega.cl/senal-en-vivo/",
            note = "Señal online de Mega",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TVN",
            category = "Chile · Nacional · Embed oficial",
            webEmbedUrl = "https://live.tvn.cl/?volume=0",
            websiteUrl = "https://www.tvn.cl/embed-no-borrar-senal-en-vivo",
            note = "Player oficial de TVN"
        ),
        Channel(
            name = "24 Horas",
            category = "Chile · Noticias",
            streamUrl = "https://mdstrm.com/live-stream-playlist/689ba606ecfe7915e1f8f741.m3u8",
            websiteUrl = "https://www.24horas.cl/envivo/",
            note = "Señal oficial 24 Horas",
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
            name = "Chilevisión",
            category = "Chile · YouTube oficial",
            webEmbedUrl = "https://www.youtube.com/embed/live_stream?channel=UC8EdTmyUaFIfZvVttJ9lgIA&autoplay=1&playsinline=1",
            websiteUrl = "https://www.chilevision.cl/senal-online",
            note = "YouTube oficial cuando CHV mantiene una transmisión en vivo"
        ),
        Channel(
            name = "Canal 13",
            category = "Chile · YouTube oficial",
            webEmbedUrl = "https://www.youtube.com/embed/live_stream?channel=UCd4D3LfXC_9MY2zSv_3gMgw&autoplay=1&playsinline=1",
            websiteUrl = "https://www.13go.cl/en-vivo?sid=13",
            note = "YouTube oficial cuando El 13 transmite en vivo"
        ),
        Channel(
            name = "T13",
            category = "Chile · Noticias · YouTube oficial",
            webEmbedUrl = "https://www.youtube.com/embed/live_stream?channel=UCsRnhjcUCR78Q3Ud6OXCTNg&autoplay=1&playsinline=1",
            websiteUrl = "https://www.t13.cl/en-vivo",
            note = "T13 EN VIVO oficial"
        ),
        Channel(
            name = "TV+",
            category = "Chile · Entretención · Player oficial",
            webEmbedUrl = "https://rudo.video/live/tvmas",
            websiteUrl = "https://www.tvmas.tv/page/en-vivo/",
            note = "Player enlazado por TV+"
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
