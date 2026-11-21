package com.example.movieapp.presentation.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubeTrailerPlayer(
    videoKey: String,
    modifier: Modifier = Modifier,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    key(videoKey) {
        AndroidView(
            modifier = modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium),
            factory = { context ->
                YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false
                    lifecycle.addObserver(this)
                    val options =
                        IFramePlayerOptions
                            .Builder(context)
                            .controls(1)
                            .rel(0)
                            .build()
                    initialize(
                        object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.cueVideo(videoKey, 0f)
                            }
                        },
                        options,
                    )
                }
            },
            onRelease = { playerView ->
                lifecycle.removeObserver(playerView)
                playerView.release()
            },
        )
    }
}
