package com.proshnotechnologies.proshno.live.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Controller.RetainViewMode.RETAIN_DETACH
import com.jakewharton.rxbinding2.view.clicks
import com.proshnotechnologies.proshno.BuildConfig
import com.proshnotechnologies.proshno.MainActivity
import com.proshnotechnologies.proshno.R
import com.proshnotechnologies.proshno.R.id
import com.proshnotechnologies.proshno.live.di.DaggerLiveGameComponent
import com.proshnotechnologies.proshno.live.mvi.LiveGameIntent
import com.proshnotechnologies.proshno.live.mvi.LiveGameIntent.ChooseAnswerIntent
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewModel
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ChooseAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ReceivedAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ReceivedQuestion
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ReceivedStreamStats
import com.proshnotechnologies.proshno.mvi.MviView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.live_game_container.view.live_game_container_layout
import kotlinx.android.synthetic.main.live_game_container.view.option_1
import kotlinx.android.synthetic.main.live_game_container.view.option_2
import kotlinx.android.synthetic.main.live_game_container.view.option_3
import kotlinx.android.synthetic.main.live_game_container.view.tv_question
import kotlinx.android.synthetic.main.live_game_container.view.video_view
import kotlinx.android.synthetic.main.live_game_container.view.video_view_container_card
import kotlinx.android.synthetic.main.live_game_header.view.img_logo
import kotlinx.android.synthetic.main.live_game_header.view.live_game_header_layout
import kotlinx.android.synthetic.main.live_game_question.view.tv_num_answered
import kotlinx.android.synthetic.main.live_game_question.view.tv_option
import tcking.github.com.giraffeplayer2.DefaultPlayerListener
import tcking.github.com.giraffeplayer2.GiraffePlayer
import tcking.github.com.giraffeplayer2.VideoInfo
import tcking.github.com.giraffeplayer2.VideoInfo.AR_ASPECT_FILL_PARENT
import tcking.github.com.giraffeplayer2.VideoView
import javax.inject.Inject

class LiveGameController : Controller(), MviView<LiveGameIntent, LiveGameViewState> {
    @Inject lateinit var viewModel: LiveGameViewModel
    private lateinit var player: GiraffePlayer
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var isExpanded = false

    override fun intents(view: View): Observable<LiveGameIntent> = Observable.merge(
        initialIntent(), chooseAnswerIntent(view))

    override fun render(state: LiveGameViewState) {
        if (state.isFullScreen && !isExpanded) {
            isExpanded = true
            expandVideo()
        } else if (!state.isFullScreen && isExpanded) {
            isExpanded = false
            shrinkVideo()
        }

        when (state) {
            is ChooseAnswer -> {
                val option = when {
                    state.choice == 1 -> view?.option_1
                    state.choice == 2 -> view?.option_2
                    else -> view?.option_3
                }

                if (state.inFlight) {
                    option?.setBackgroundResource(R.drawable.rounded_rectangle_yellow)
                } else if (state.error != null) {
                    option?.setBackgroundResource(R.drawable.rounded_rectangle)
                    Toast.makeText(activity, "Error: please try again", Toast.LENGTH_SHORT).show()
                }
            }

            is ReceivedQuestion -> {
                view?.let {
                    it.tv_question.text = state.question.question
                    it.tv_question.tag = state.question.questionId
                    it.option_1.tv_option.text = state.question.choices[0].text
                    it.option_2.tv_option.text = state.question.choices[1].text
                    it.option_3.tv_option.text = state.question.choices[2].text
                    it.option_1.tv_num_answered.visibility = INVISIBLE
                    it.option_2.tv_num_answered.visibility = INVISIBLE
                    it.option_3.tv_num_answered.visibility = INVISIBLE
                }
            }

            is ReceivedAnswer -> {
                view?.let {
                    it.option_1.tv_num_answered.text = state.question.choices[0].numAnswered.toString()
                    it.option_2.tv_num_answered.text = state.question.choices[1].numAnswered.toString()
                    it.option_3.tv_num_answered.text = state.question.choices[2].numAnswered.toString()
                    it.option_1.tv_num_answered.visibility = VISIBLE
                    it.option_2.tv_num_answered.visibility = VISIBLE
                    it.option_3.tv_num_answered.visibility = VISIBLE

                    val option = when (state.question.answer) {
                        0 -> it.option_1
                        1 -> it.option_2
                        2 -> it.option_3
                        else -> {
                            throw IllegalArgumentException("Invalid option: ${state.question.answer}")
                        }
                    }
                    option?.setBackgroundResource(R.drawable.rounded_rectangle_green)
                }
            }

            is ReceivedStreamStats -> {
                TODO("Implement stats")
            }
        }
    }

    private fun bindIntents(view: View) {
        viewModel.states()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::render)
            .addTo(disposables)
        viewModel.processIntents(intents(view))
    }

    private fun chooseAnswerIntent(view: View): Observable<LiveGameIntent> {
        return Observable.merge(
            view.option_1.clicks().map { 1 },
            view.option_2.clicks().map { 2 },
            view.option_3.clicks().map { 3 })
            .map { ChooseAnswerIntent(view.tv_question.tag as String, it) }
            .distinctUntilChanged { it -> it.questionId }
            .cast(LiveGameIntent::class.java)
    }

    private fun initialIntent() = Observable.just(LiveGameIntent.InitialIntent)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val layout = inflater.inflate(R.layout.live_game, container, false)
        retainViewMode = RETAIN_DETACH
        DaggerLiveGameComponent.builder()
            .singletonComponent((activity as MainActivity).singletonComponent())
            .build()
            .inject(this)
        initVideoPlayer(layout)
        bindIntents(layout)
        return layout
    }

    override fun onAttach(view: View) {
        view.live_game_header_layout.bringToFront()
        if (BuildConfig.DEBUG) {
            view.live_game_header_layout.img_logo.setOnClickListener {
                if (isExpanded) {
                    isExpanded = false
                    shrinkVideo()
                } else {
                    isExpanded = true
                    expandVideo()
                }
            }
        }
    }

    override fun onDestroyView(view: View) {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }

        if (!player.isReleased) {
            player.release()
        }
    }

    private fun expandVideo() {
        view?.let {
            it.live_game_container_layout.setPadding(0, 0, 0, 0)
            it.live_game_container_layout.layoutParams = (it.live_game_container_layout
                .layoutParams as FrameLayout.LayoutParams)
                .apply {
                    height = MATCH_PARENT
                    setMargins(0, 0, 0, 0)
                }
            it.video_view_container_card.radius = 0.toFloat()
            it.video_view_container_card.layoutParams = (it.video_view_container_card
                .layoutParams as ViewGroup.MarginLayoutParams)
                .apply {
                    width = MATCH_PARENT
                    height = MATCH_PARENT
                    setMargins(0, 0, 0, 0)
                }
        }
    }

    private fun shrinkVideo() {
        view?.let { view ->
            view.video_view_container_card.radius = videoContainerRadius
            view.video_view_container_card.layoutParams = (view.video_view_container_card
                .layoutParams as ViewGroup.MarginLayoutParams)
                .apply {
                    width = videoContainerWidth
                    height = videoContainerHeight
                    topMargin = videoContainerTopMargin
                }

            view.live_game_container_layout.setPadding(0, 0, 0,
                gameContainerBottomPadding)
            view.live_game_container_layout.layoutParams = (view.live_game_container_layout
                .layoutParams as FrameLayout.LayoutParams)
                .apply {
                    leftMargin = gameContainerLeftMargin
                    rightMargin = gameContainerRightMargin
                    topMargin = gameContainerTopMargin
                    height = WRAP_CONTENT
                }
        }
    }

    private fun initVideoPlayer(layout: View) {
        hideMediaPlayerControls(layout.video_view)
        player = layout.video_view.videoInfo(videoInfo()).player
        player.start()
    }

    private fun hideMediaPlayerControls(view: VideoView) {
        view.playerListener = object : DefaultPlayerListener() {
            override fun onStart(giraffePlayer: GiraffePlayer?) {
                view.findViewById<SeekBar>(id.app_video_seekBar).visibility = GONE
                view.findViewById<ImageView>(id.app_video_fullscreen).visibility = GONE
                view.findViewById<ImageView>(
                    id.app_video_volume_icon).visibility = GONE
                view.findViewById<TextView>(id.app_video_currentTime).visibility = GONE
                view.findViewById<ImageView>(id.app_video_clarity).visibility = GONE
            }
        }
    }

    private fun videoInfo(): VideoInfo? {
        return VideoInfo(LIVE_VIDEO_URI)
            .setPortraitWhenFullScreen(false)
            .setAspectRatio(AR_ASPECT_FILL_PARENT)
            .setShowTopBar(false)
    }

    private val videoContainerWidth: Int by lazy {
        activity!!.resources.getDimension(R.dimen.video_view_container_card_width).toInt()
    }

    private val videoContainerHeight: Int by lazy {
        activity!!.resources.getDimension(R.dimen.video_view_container_card_height).toInt()
    }

    private val videoContainerTopMargin: Int by lazy {
        activity!!.resources.getDimension(R.dimen.video_view_container_card_margin_top).toInt()
    }

    private val videoContainerRadius: Float by lazy {
        activity!!.resources.getDimension(R.dimen.video_view_container_card_radius)
    }

    private val gameContainerLeftMargin: Int by lazy {
        activity!!.resources.getDimension(R.dimen.live_game_container_margin_left).toInt()
    }

    private val gameContainerRightMargin: Int by lazy {
        activity!!.resources.getDimension(R.dimen.live_game_container_margin_right).toInt()
    }

    private val gameContainerTopMargin: Int by lazy {
        activity!!.resources.getDimension(R.dimen.live_game_container_margin_top).toInt()
    }

    private val gameContainerBottomPadding: Int by lazy {
        activity!!.resources.getDimension(R.dimen.live_game_container_padding_bottom).toInt()
    }

    companion object {
        val LIVE_VIDEO_URI: Uri = Uri.parse("rtmp://10.88.111.6/live/test")
    }
}