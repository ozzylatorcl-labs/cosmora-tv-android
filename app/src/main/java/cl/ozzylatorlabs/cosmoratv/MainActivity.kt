package cl.ozzylatorlabs.cosmoratv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
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
import androidx.compose.ui.input.key.nativeKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
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

    MaterialTheme(
        colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Focus)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
                .onPreviewKeyEvent { event ->
                    val native = event.nativeKeyEvent
                    if (native.action != KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent false
                    when (native.keyCode) {
                        KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_PAGE_UP -> {
                            changeChannel(-1); true
                        }
                        KeyEvent.KEYCODE_CHANNEL_DOWN, KeyEvent.KEYCODE_PAGE_DOWN -> {
                            changeChannel(1); true
                        }
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                            if (player.isPlaying) player.pause() else player.play(); true
                        }
                        KeyEvent.KEYCODE_MEDIA_STOP -> {
                            player.pause(); true
                        }
                        else -> false
                    }
                }
        ) {
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
                    channels = channels,
                    selectedIndex = selectedIndex,
                    player = player,
                    playerError = playerError,
                    muted = muted,
                    onSelected = { selectedIndex = it },
                    onPrevious = { changeChannel(-1) },
                    onNext = { changeChannel(1) },
                    onMute = { muted = !muted },
                    onFullScreen = { fullScreen = true },
                    onRetry = { player.prepare(); player.play() }
                )

                else -> MobileHome(
                    channels = channels,
                    selectedIndex = selectedIndex,
                    player = player,
                    playerError = playerError,
                    muted = muted,
                    onSelected = { selectedIndex = it },
                    onPrevious = { changeChannel(-1) },
                    onNext = { changeChannel(1) },
                    onMute = { muted = !muted },
                    onFullScreen = { fullScreen = true },
                    onRetry = { player.prepare(); player.play() }
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
    Box(Modifier.fillMaxSize()) {
        PlayerPanel(player, channel, playerError, Modifier.fillMaxSize(), onRetry)
        FocusButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(22.dp),
            onClick = onExit
        ) {
            Icon(Icons.Default.FullscreenExit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Salir de pantalla completa")
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
        Modifier.fillMaxSize().padding(26.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        NavigationRailPanel(Modifier.width(185.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Header()
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Column(Modifier.weight(1.75f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    PlayerPanel(
                        player,
                        channels[selectedIndex],
                        playerError,
                        Modifier.weight(1f),
                        onRetry
                    )
                    Controls(
                        player,
                        channels[selectedIndex],
                        muted,
                        onPrevious,
                        onNext,
                        onMute,
                        onFullScreen
                    )
                }
                ChannelList(
                    channels,
                    selectedIndex,
                    onSelected,
                    Modifier.weight(1f).fillMaxHeight()
                )
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Header()
        PlayerPanel(
            player,
            channels[selectedIndex],
            playerError,
            Modifier.fillMaxWidth().aspectRatio(16f / 9f),
            onRetry
        )
        Controls(
            player,
            channels[selectedIndex],
            muted,
            onPrevious,
            onNext,
            onMute,
            onFullScreen
        )
        Text("Canales", fontSize = 22.sp, color = Color.White)
        ChannelList(channels, selectedIndex, onSelected, Modifier.weight(1f).fillMaxWidth())
    }
}

@Composable
private fun Header() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("Cosmora TV", color = Color.White, fontSize = 30.sp)
            Text("Tu universo en una pantalla · Ozzylator Labs", color = TextMuted, fontSize = 14.sp)
        }
        Surface(color = Live, shape = RoundedCornerShape(18.dp)) {
            Text(
                "EN VIVO",
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun NavigationRailPanel(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxHeight()
            .background(Panel, RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("COSMORA TV", color = Color.White, fontSize = 19.sp, modifier = Modifier.padding(8.dp))
        NavItem("Inicio", Icons.Default.Home, true)
        NavItem("Canales", Icons.Default.LiveTv, false)
        NavItem("Favoritos", Icons.Default.Star, false)
        NavItem("Ajustes", Icons.Default.Settings, false)
        Spacer(Modifier.weight(1f))
        Text(
            "↑ ↓ ← →  OK\nAtrás · Play/Pausa",
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (active) Panel2 else Color.Transparent, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (active) Color.White else TextMuted)
        Spacer(Modifier.width(10.dp))
        Text(label, color = if (active) Color.White else TextMuted, fontSize = 15.sp)
    }
}

@Composable
private fun PlayerPanel(
    player: ExoPlayer,
    channel: Channel,
    playerError: String?,
    modifier: Modifier,
    onRetry: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier.background(Color.Black, RoundedCornerShape(24.dp))) {
        if (channel.playsInsideApp) {
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
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101827), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Public, null, tint = Focus, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Señal oficial", color = Color.White, fontSize = 24.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Se abre desde el sitio oficial del canal.",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    if (!channel.websiteUrl.isNullOrBlank()) {
                        FocusButton(onClick = {
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(channel.websiteUrl))
                                )
                            }
                        }) {
                            Icon(Icons.Default.OpenInNew, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Abrir señal oficial")
                        }
                    }
                }
            }
        }

        Column(Modifier.align(Alignment.TopStart).padding(18.dp)) {
            Text(channel.name, color = Color.White, fontSize = 24.sp)
            Text(channel.category, color = TextMuted, fontSize = 13.sp)
        }

        if (playerError != null && channel.playsInsideApp) {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .background(Color(0xDD161A22), RoundedCornerShape(18.dp))
                    .padding(22.dp),
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
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(value: Boolean) {
                isPlaying = value
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
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
            item {
                FocusButton(onClick = onFullScreen) {
                    Icon(Icons.Default.Fullscreen, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Pantalla")
                }
            }
        } else if (!channel.websiteUrl.isNullOrBlank()) {
            item {
                FocusButton(onClick = {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(channel.websiteUrl)))
                    }
                }) {
                    Icon(Icons.Default.OpenInNew, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Señal oficial")
                }
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
    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 18.dp)
    ) {
        itemsIndexed(channels) { index, channel ->
            ChannelCard(
                channel,
                selected = index == selectedIndex,
                onClick = { onSelected(index) }
            )
        }
    }
}

@Composable
private fun ChannelCard(channel: Channel, selected: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val borderColor = when {
        focused -> Focus
        selected -> Color(0xFF526C9E)
        else -> Color.Transparent
    }
    val bg = if (focused || selected) Panel2 else Panel

    Row(
        Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(18.dp))
            .border(if (focused) 3.dp else 1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(48.dp).background(Color(0xFF34425C), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.LiveTv, null, tint = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(channel.name, color = Color.White, fontSize = 16.sp)
            Text(channel.category, color = TextMuted, fontSize = 12.sp)
        }
        if (selected) Icon(Icons.Default.PlayCircle, null, tint = Focus)
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
            .border(
                if (focused) 3.dp else 1.dp,
                if (focused) Focus else Color(0xFF39445A),
                RoundedCornerShape(15.dp)
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
