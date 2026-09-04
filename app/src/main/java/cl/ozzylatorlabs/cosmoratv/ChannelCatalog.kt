package cl.ozzylatorlabs.cosmoratv

object ChannelCatalog {
    // Cosmora TV prioriza señales gratuitas/públicas y proveedores usados por los propios canales.
    // TVN y Canal 13 principal se muestran dentro de la app desde sus páginas oficiales.
    // No se incluyen listas Xtream, canales premium retransmitidos ni proxies jmp2.uk.
    val channels = listOf(
        Channel(
            name = "Mega",
            category = "Chile · Nacional",
            streamUrl = "https://unlimited1-cl-isp.dps.live/mega/mega.smil/playlist.m3u8",
            websiteUrl = "https://www.mega.cl/senal-en-vivo/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Chilevisión",
            category = "Chile · Nacional",
            streamUrl = "https://vmf.edge-online.chv.cl/chv/index.m3u8",
            websiteUrl = "https://www.chilevision.cl/senal-online/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "24 Horas",
            category = "Chile · Noticias",
            streamUrl = "https://mdstrm.com/live-stream-playlist/689ba606ecfe7915e1f8f741.m3u8",
            websiteUrl = "https://www.24horas.cl/envivo/",
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
            name = "TVN",
            category = "Chile · Nacional · Web oficial",
            webEmbedUrl = "https://www.tvn.cl/en-vivo",
            websiteUrl = "https://www.tvn.cl/en-vivo",
            note = "Señal oficial dentro de Cosmora"
        ),
        Channel(
            name = "Canal 13",
            category = "Chile · Nacional · Web oficial",
            webEmbedUrl = "https://www.13.cl/en-vivo",
            websiteUrl = "https://www.13.cl/en-vivo",
            note = "Señal oficial dentro de Cosmora"
        ),
        Channel(
            name = "13C",
            category = "Chile · Canal 13",
            streamUrl = "https://origin.dpsgo.com/ssai/event/GI-9cp_bT8KcerLpZwkuhw/master.m3u8",
            websiteUrl = "https://www.13.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "13 Festival",
            category = "Chile · Música",
            streamUrl = "https://origin.dpsgo.com/ssai/event/Nftd0fM2SXasfDlRphvUsg/master.m3u8",
            websiteUrl = "https://www.13.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "13 Realities",
            category = "Chile · Entretención",
            streamUrl = "https://origin.dpsgo.com/ssai/event/g7_JOM0ORki9SR5RKHe-Kw/master.m3u8",
            websiteUrl = "https://www.13.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "13 Teleseries",
            category = "Chile · Entretención",
            streamUrl = "https://origin.dpsgo.com/ssai/event/f4TrySe8SoiGF8Lu3EIq1g/master.m3u8",
            websiteUrl = "https://www.13.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "13 Humor",
            category = "Chile · Entretención",
            streamUrl = "https://origin.dpsgo.com/ssai/event/cKWySXKgSK-SzlJmESkOWw/master.m3u8",
            websiteUrl = "https://www.13.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "13 Kids",
            category = "Chile · Infantil",
            streamUrl = "https://origin.dpsgo.com/ssai/event/LhHrVtyeQkKZ-Ye_xEU75g/master.m3u8",
            websiteUrl = "https://www.13.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "T13",
            category = "Chile · Noticias",
            streamUrl = "https://jireh-4-hls-video-cl-isp.dps.live/hls-video/10b92cafdf3646cbc1e727f3dc76863621a327fd/t13/t13.smil/playlist_900.m3u8",
            websiteUrl = "https://www.t13.cl/en-vivo",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TV Chile",
            category = "Chile · Internacional",
            streamUrl = "https://mdstrm.com/live-stream-playlist/533adcc949386ce765657d7c.m3u8",
            websiteUrl = "https://www.tvn.cl/tvchile/envivo",
            verifiedDirectStream = true
        ),
        Channel(
            name = "NTV",
            category = "Chile · Cultura",
            streamUrl = "https://mdstrm.com/live-stream-playlist/5aaabe9e2c56420918184c6d.m3u8",
            websiteUrl = "https://www.tvn.cl/ntv",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TV+",
            category = "Chile · Entretención",
            streamUrl = "https://mdstrm.com/live-stream-playlist/5c0e8b19e4c87f3f2d3e6a59.m3u8",
            websiteUrl = "https://www.tvmas.tv/envivo/",
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
            name = "Canal 9 Bío Bío",
            category = "Chile · Regional",
            streamUrl = "https://unlimited6-cl.dps.live/c9/c9.smil/playlist.m3u8",
            websiteUrl = "https://www.canal9.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Antofagasta TV",
            category = "Chile · Regional",
            streamUrl = "https://unlimited6-cl.dps.live/atv/atv.smil/playlist.m3u8",
            websiteUrl = "https://www.antofagasta.tv/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "UChile TV",
            category = "Chile · Cultura",
            streamUrl = "https://unlimited1-us.dps.live/uchiletv/uchiletv.smil/playlist.m3u8",
            websiteUrl = "https://uchile.tv/",
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
