package com.proshnotechnologies.proshno.live

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.proshnotechnologies.proshno.R
import kotlinx.android.synthetic.main.live_game.view.video_overlay_layout
import kotlinx.android.synthetic.main.live_game.view.video_view
import tcking.github.com.giraffeplayer2.GiraffePlayer
import tcking.github.com.giraffeplayer2.VideoInfo
import tcking.github.com.giraffeplayer2.VideoInfo.AR_ASPECT_FILL_PARENT

class LiveGameController : Controller() {
    private lateinit var player: GiraffePlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.live_game, container, false)
    }

    override fun onAttach(view: View) {
        player = view.video_view.videoInfo(videoInfo()).player
        player.start()
        view.video_overlay_layout.bringToFront()
    }

    private fun videoInfo(): VideoInfo? {
        return VideoInfo(LIVE_VIDEO_URI)
            .setPortraitWhenFullScreen(false)
            .setAspectRatio(AR_ASPECT_FILL_PARENT)
            .setShowTopBar(false)
    }

    companion object {
        val LIVE_VIDEO_URI: Uri = Uri.parse("rtmp://10.88.111.6/live/test")
    }
}