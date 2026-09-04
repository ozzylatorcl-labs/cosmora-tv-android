package cl.ozzylatorlabs.cosmoratv

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RadioPlaybackState {
    private val _stationId = MutableStateFlow<String?>(null)
    val stationId: StateFlow<String?> = _stationId.asStateFlow()

    private val _stationName = MutableStateFlow<String?>(null)
    val stationName: StateFlow<String?> = _stationName.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    internal fun setStation(id: String?, name: String?) {
        _stationId.value = id
        _stationName.value = name
    }

    internal fun setPlaying(value: Boolean) {
        _isPlaying.value = value
    }

    internal fun setError(value: String?) {
        _error.value = value
    }
}

@UnstableApi
class RadioPlaybackService : MediaSessionService() {

    companion object {
        const val ACTION_PLAY_STATION = "cl.ozzylatorlabs.cosmoratv.PLAY_RADIO"
        const val ACTION_TOGGLE_PAUSE = "cl.ozzylatorlabs.cosmoratv.TOGGLE_RADIO"
        const val ACTION_STOP = "cl.ozzylatorlabs.cosmoratv.STOP_RADIO"

        const val EXTRA_ID = "station_id"
        const val EXTRA_NAME = "station_name"
        const val EXTRA_SUBTITLE = "station_subtitle"
        const val EXTRA_STREAM = "station_stream"
        const val EXTRA_ARTWORK = "station_artwork"
    }

    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId("cosmora_radio_playback")
                .setChannelName(R.string.radio_playback_channel_name)
                .setNotificationId(616)
                .build()
        )

        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            setHandleAudioBecomingNoisy(true)
            setWakeMode(C.WAKE_MODE_LOCAL)
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    RadioPlaybackState.setPlaying(isPlaying)
                    if (isPlaying) RadioPlaybackState.setError(null)
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    RadioPlaybackState.setPlaying(false)
                    RadioPlaybackState.setError("No se pudo reproducir esta radio")
                }
            })
        }

        val openApp = PendingIntent.getActivity(
            this,
            616,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(openApp)
            .build()

        mediaSession?.let { session ->
            if (!isSessionAdded(session)) addSession(session)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_PLAY_STATION -> playStation(intent)
            ACTION_TOGGLE_PAUSE -> {
                if (player.isPlaying) player.pause() else player.play()
            }
            ACTION_STOP -> stopRadio()
        }

        return START_STICKY
    }

    private fun playStation(intent: Intent) {
        val id = intent.getStringExtra(EXTRA_ID).orEmpty()
        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val subtitle = intent.getStringExtra(EXTRA_SUBTITLE).orEmpty()
        val stream = intent.getStringExtra(EXTRA_STREAM).orEmpty()
        val artwork = intent.getStringExtra(EXTRA_ARTWORK)

        if (stream.isBlank()) {
            RadioPlaybackState.setError("La radio no tiene una fuente válida")
            return
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(name.ifBlank { "Cosmora Radios" })
            .setArtist(subtitle)
            .setAlbumTitle("Cosmora TV · Radios de Chile")
            .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
            .setIsPlayable(true)
            .apply {
                if (!artwork.isNullOrBlank()) setArtworkUri(Uri.parse(artwork))
            }
            .build()

        val item = MediaItem.Builder()
            .setMediaId(id.ifBlank { stream })
            .setUri(stream)
            .setMediaMetadata(metadata)
            .build()

        RadioPlaybackState.setStation(id, name)
        RadioPlaybackState.setError(null)

        player.stop()
        player.clearMediaItems()
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    private fun stopRadio() {
        player.pause()
        player.clearMediaItems()
        RadioPlaybackState.setPlaying(false)
        RadioPlaybackState.setStation(null, null)
        stopSelf()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.isPlaying) stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        RadioPlaybackState.setPlaying(false)
        mediaSession?.release()
        mediaSession = null
        player.release()
        super.onDestroy()
    }
}
