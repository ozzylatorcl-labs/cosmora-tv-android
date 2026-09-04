package cl.ozzylatorlabs.cosmoratv

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

private val Bg = Color(0xFF0B0E14)
private val Panel = Color(0xFF151A24)
private val Panel2 = Color(0xFF202838)
private val Focus = Color(0xFF7DA7FF)
private val TextMuted = Color(0xFFAAB3C4)
private val Live = Color(0xFFD93D4A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { CosmoraTvApp() }
    }
}

@Composable
private fun CosmoraTvApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val channels = remember { ChannelCatalog.channels }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var fullScreen by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                playerError = "Señal no disponible ahora"
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(selectedIndex) {
        playerError = null
        val channel = channels[selectedIndex]
        if (channel.playsInsideApp) {
            player.setMediaItem(MediaItem.fromUri(channel.streamUrl!!))
            player.prepare()
            player.play()
        } else {
            player.stop()
            player.clearMediaItems()
        }
    }

    LaunchedEffect(muted) {
        player.volume = if (muted) 0f else 1f
    }

    fun changeChannel(delta: Int) {
        selectedIndex = (selectedIndex + delta + channels.size) % channels.size
    }

    BackHandler(enabled = fullScreen) { fullScreen = false }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Focus)) {
        BoxWithConstraints(Modifier.fillMaxSize().background(Bg)) {
            val tvLayout = maxWidth >= 820.dp
            when {
                fullScreen -> FullScreenPlayer(
                    player = player,
                    channel = channels[selectedIndex],
                    playerError = playerError,
                    onExit = { fullScreen = false },
                    onRetry = { player.prepare(); player.play() }
                )
                tvLayout -> TvHome(
                    channels, selectedIndex, player, playerError, muted,
                    { selectedIndex = it }, { changeChannel(-1) }, { changeChannel(1) },
                    { muted = !muted }, { fullScreen = true },
                    { player.prepare(); player.play() }
                )
                else -> MobileHome(
                    channels, selectedIndex, player, playerError, muted,
                    { selectedIndex = it }, { changeChannel(-1) }, { changeChannel(1) },
                    { muted = !muted }, { fullScreen = true },
                    { player.prepare(); player.play() }
                )
            }
        }
    }
}

@Composable
private fun FullScreenPlayer(
    player: ExoPlayer,
    channel: Channel,
    playerError: String?,
    onExit: () -> Unit,
    onRetry: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        PlayerPanel(player, channel, playerError, Modifier.fillMaxSize(), onRetry)
        FocusButton(Modifier.align(Alignment.TopEnd).padding(18.dp), onExit) {
            Icon(Icons.Default.FullscreenExit, null)
            Spacer(Modifier.width(8.dp))
            Text("Volver")
        }
    }
}

@Composable
private fun TvHome(
    channels: List<Channel>,
    selectedIndex: Int,
    player: ExoPlayer,
    playerError: String?,
    muted: Boolean,
    onSelected: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMute: () -> Unit,
    onFullScreen: () -> Unit,
    onRetry: () -> Unit
) {
    Row(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        NavigationRailPanel(Modifier.width(185.dp))

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Header(channels.size)

            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                PlayerPanel(
                    player,
                    channels[selectedIndex],
                    playerError,
                    Modifier.weight(1f).fillMaxHeight(),
                    onRetry
                )

                Column(
                    Modifier.width(225.dp).fillMaxHeight().background(Panel, RoundedCornerShape(22.dp)).padding(18.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Tv, null, tint = Focus, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(14.dp))
                    Text("Pensado para mando", color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("↑  ↓  ←  →   OK\nAtrás · Play/Pausa", color = TextMuted, fontSize = 14.sp, lineHeight = 22.sp)
                    Spacer(Modifier.height(22.dp))
                    Text("${channels.size} señales", color = Color.White, fontSize = 17.sp)
                    Text("Nacionales, noticias, música y regionales", color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }

            Controls(player, channels[selectedIndex], muted, onPrevious, onNext, onMute, onFullScreen)

            Text("Canales", color = Color.White, fontSize = 23.sp)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 4.dp)
            ) {
                itemsIndexed(channels) { index, channel ->
                    ChannelCard(
                        channel = channel,
                        selected = index == selectedIndex,
                        onClick = { onSelected(index) },
                        modifier = Modifier.width(190.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MobileHome(
    channels: List<Channel>,
    selectedIndex: Int,
    player: ExoPlayer,
    playerError: String?,
    muted: Boolean,
    onSelected: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMute: () -> Unit,
    onFullScreen: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Header(channels.size)
        PlayerPanel(
            player,
            channels[selectedIndex],
            playerError,
            Modifier.fillMaxWidth().aspectRatio(16f / 9f),
            onRetry
        )
        Controls(player, channels[selectedIndex], muted, onPrevious, onNext, onMute, onFullScreen)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Canales", color = Color.White, fontSize = 22.sp, modifier = Modifier.weight(1f))
            Text("${channels.size} señales", color = TextMuted, fontSize = 13.sp)
        }
        ChannelList(channels, selectedIndex, onSelected, Modifier.weight(1f).fillMaxWidth())
    }
}

@Composable
private fun Header(channelCount: Int) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("Cosmora TV", color = Color.White, fontSize = 30.sp)
            Text("Tu universo en una pantalla · $channelCount señales", color = TextMuted, fontSize = 14.sp)
        }
        Surface(color = Live, shape = RoundedCornerShape(18.dp)) {
            Text("EN VIVO", color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp))
        }
    }
}

@Composable
private fun NavigationRailPanel(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxHeight().background(Panel, RoundedCornerShape(22.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("COSMORA TV", color = Color.White, fontSize = 19.sp, modifier = Modifier.padding(8.dp))
        NavItem("Inicio", Icons.Default.Home, true)
        NavItem("Canales", Icons.Default.LiveTv, false)
        NavItem("Favoritos", Icons.Default.Star, false)
        NavItem("Ajustes", Icons.Default.Settings, false)
        Spacer(Modifier.weight(1f))
        Text("Ozzylator Labs", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun NavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean) {
    Row(
        Modifier.fillMaxWidth().background(if (active) Panel2 else Color.Transparent, RoundedCornerShape(14.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (active) Color.White else TextMuted)
        Spacer(Modifier.width(10.dp))
        Text(label, color = if (active) Color.White else TextMuted, fontSize = 15.sp)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PlayerPanel(
    player: ExoPlayer,
    channel: Channel,
    playerError: String?,
    modifier: Modifier,
    onRetry: () -> Unit
) {
    Box(modifier.background(Color.Black, RoundedCornerShape(22.dp))) {
        when {
            channel.playsInsideApp -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            this.player = player
                            useController = false
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { it.player = player }
                )
            }

            channel.playsAsWeb -> {
                key(channel.webEmbedUrl) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                setBackgroundColor(android.graphics.Color.BLACK)
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                settings.loadsImagesAutomatically = true
                                webViewClient = WebViewClient()
                                webChromeClient = WebChromeClient()
                                loadUrl(channel.webEmbedUrl!!)
                            }
                        },
                        update = { webView ->
                            if (webView.url != channel.webEmbedUrl) webView.loadUrl(channel.webEmbedUrl!!)
                        }
                    )
                }
            }

            else -> {
                Box(Modifier.fillMaxSize().background(Color(0xFF101827)), contentAlignment = Alignment.Center) {
                    Text("Señal no disponible", color = Color.White, fontSize = 20.sp)
                }
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
            color = Color(0xCC11151D),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(horizontal = 13.dp, vertical = 8.dp)) {
                Text(channel.name, color = Color.White, fontSize = 19.sp)
                Text(channel.category, color = TextMuted, fontSize = 11.sp)
            }
        }

        if (playerError != null && channel.playsInsideApp) {
            Column(
                Modifier.align(Alignment.Center).background(Color(0xE8161A22), RoundedCornerShape(18.dp)).padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(playerError, color = Color.White)
                Spacer(Modifier.height(12.dp))
                FocusButton(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
private fun Controls(
    player: ExoPlayer,
    channel: Channel,
    muted: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMute: () -> Unit,
    onFullScreen: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(value: Boolean) { isPlaying = value }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(9.dp), contentPadding = PaddingValues(vertical = 2.dp)) {
        item {
            FocusButton(onClick = onPrevious) {
                Icon(Icons.Default.SkipPrevious, null)
                Spacer(Modifier.width(6.dp))
                Text("Anterior")
            }
        }
        if (channel.playsInsideApp) {
            item {
                FocusButton(onClick = { if (player.isPlaying) player.pause() else player.play() }) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (isPlaying) "Pausa" else "Play")
                }
            }
            item {
                FocusButton(onClick = onMute) {
                    Icon(if (muted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (muted) "Sonido" else "Silenciar")
                }
            }
        } else if (channel.playsAsWeb) {
            item {
                Surface(color = Color(0xFF263146), shape = RoundedCornerShape(15.dp)) {
                    Row(Modifier.padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, null, tint = Focus)
                        Spacer(Modifier.width(7.dp))
                        Text("Web oficial", color = Color.White)
                    }
                }
            }
        }
        item {
            FocusButton(onClick = onFullScreen) {
                Icon(Icons.Default.Fullscreen, null)
                Spacer(Modifier.width(6.dp))
                Text("Pantalla")
            }
        }
        item {
            FocusButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, null)
                Spacer(Modifier.width(6.dp))
                Text("Siguiente")
            }
        }
    }
}

@Composable
private fun ChannelList(
    channels: List<Channel>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(9.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
        itemsIndexed(channels) { index, channel ->
            ChannelCard(channel, index == selectedIndex, { onSelected(index) }, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ChannelCard(
    channel: Channel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val borderColor = if (focused) Focus else if (selected) Color(0xFF526C9E) else Color.Transparent
    val bg = if (focused || selected) Panel2 else Panel

    Row(
        modifier
            .heightIn(min = 66.dp)
            .background(bg, RoundedCornerShape(17.dp))
            .border(if (focused) 3.dp else 1.dp, borderColor, RoundedCornerShape(17.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(42.dp).background(Color(0xFF34425C), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(if (channel.playsAsWeb) Icons.Default.Public else Icons.Default.LiveTv, null, tint = Color.White)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(channel.name, color = Color.White, fontSize = 15.sp, maxLines = 1)
            Text(channel.category, color = TextMuted, fontSize = 11.sp, maxLines = 1)
        }
        if (selected) Icon(Icons.Default.PlayCircle, null, tint = Focus, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun FocusButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Row(
        modifier
            .background(if (focused) Color(0xFF2B3B59) else Panel2, RoundedCornerShape(15.dp))
            .border(if (focused) 3.dp else 1.dp, if (focused) Focus else Color(0xFF39445A), RoundedCornerShape(15.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
