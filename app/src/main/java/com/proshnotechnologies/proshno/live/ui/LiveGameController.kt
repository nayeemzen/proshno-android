package com.proshnotechnologies.proshno.live.ui

import android.content.res.Resources
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
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ConnectToGame
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ReceivedAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ReceivedQuestion
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ReceivedStreamStats
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ReceivedUserEliminated
import com.proshnotechnologies.proshno.live.repository.LocalDataStore
import com.proshnotechnologies.proshno.mvi.MviView
import com.proshnotechnologies.proshno.utils.extensions.dp
import es.dmoral.toasty.Toasty
import io.github.krtkush.lineartimer.LinearTimer
import io.github.krtkush.lineartimer.LinearTimer.Builder
import io.github.krtkush.lineartimer.LinearTimer.TimerListener
import io.github.krtkush.lineartimer.LinearTimerStates.INITIALIZED
import io.github.krtkush.lineartimer.LinearTimerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.live_game_container.view.linear_timer
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
import timber.log.Timber
import javax.inject.Inject

class LiveGameController : Controller(), MviView<LiveGameIntent, LiveGameViewState> {
    @Inject lateinit var localDataStore: LocalDataStore
    @Inject lateinit var viewModel: LiveGameViewModel

    private lateinit var player: GiraffePlayer
    private lateinit var linearTimer: LinearTimer
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
                    state.choice == 0 -> view?.option_1
                    state.choice == 1 -> view?.option_2
                    state.choice == 2 -> view?.option_3
                    else -> throw IllegalArgumentException("Invalid option")
                }

                if (state.inFlight) {
                    option?.setBackgroundResource(R.drawable.rounded_rectangle_yellow)
                }

                if (state.error != null) {
                    option?.setBackgroundResource(R.drawable.rounded_rectangle_transparent)
                    Toast.makeText(activity, "Error: please try again", Toast.LENGTH_SHORT).show()
                }
            }

            is ReceivedQuestion -> {
                if (linearTimer.state == INITIALIZED) {
                    linearTimer.startTimer()
                } else {
                    linearTimer.restartTimer()
                }

                view?.let {
                    it.linear_timer.visibility = VISIBLE
                    it.tv_question.text = state.question.text
                    it.tv_question.tag = state.question.id
                    it.option_1.setBackgroundResource(R.drawable.rounded_rectangle_transparent)
                    it.option_2.setBackgroundResource(R.drawable.rounded_rectangle_transparent)
                    it.option_3.setBackgroundResource(R.drawable.rounded_rectangle_transparent)
                    it.option_1.tv_option.text = state.question.choices[0]
                    it.option_2.tv_option.text = state.question.choices[1]
                    it.option_3.tv_option.text = state.question.choices[2]
                    it.option_1.tv_num_answered.visibility = INVISIBLE
                    it.option_2.tv_num_answered.visibility = INVISIBLE
                    it.option_3.tv_num_answered.visibility = INVISIBLE
                }
            }

            is ReceivedAnswer -> {
                view?.let {
                    it.option_1.tv_num_answered.text = state.answer.numResponses[0].toString()
                    it.option_2.tv_num_answered.text = state.answer.numResponses[1].toString()
                    it.option_3.tv_num_answered.text = state.answer.numResponses[2].toString()
                    it.option_1.tv_num_answered.visibility = VISIBLE
                    it.option_2.tv_num_answered.visibility = VISIBLE
                    it.option_3.tv_num_answered.visibility = VISIBLE

                    val option = when (state.answer.answer) {
                        0 -> it.option_1
                        1 -> it.option_2
                        2 -> it.option_3
                        else -> {
                            throw IllegalArgumentException("Invalid option: ${state.answer}")
                        }
                    }
                    option?.setBackgroundResource(R.drawable.rounded_rectangle_green)
                    activity?.let {
                        if (state.answer.isCorrect()) {
                            Toasty.success(it, "Correct!").show()
                        } else {
                            Toasty.error(it, "Wrong!").show()
                        }
                    }

                }
            }

            is ReceivedStreamStats -> {
                TODO("Implement stats")
            }

            is ConnectToGame -> {
                activity?.let {
                    if (state.error != null) {
                        Timber.e(state.error)
                        Toasty.error(it, "Error connecting, please restart the app.").show()
                    } else {
                        Toasty.info(it, "Connected.").show()
                    }
                }

            }

            is ReceivedUserEliminated -> {
                activity?.let {
                    Toasty.info(it, "You are eliminated").show()
                }
            }
        }
    }

    private fun bindIntents(view: View) {
        viewModel.states()
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .subscribe(this::render)
            .addTo(disposables)
        viewModel.processIntents(intents(view))
    }

    private fun chooseAnswerIntent(view: View): Observable<LiveGameIntent> {
        return Observable.merge(
            view.option_1.clicks().map { 0 },
            view.option_2.clicks().map { 1 },
            view.option_3.clicks().map { 2 })
            .filter { !localDataStore.isEliminated() }
            .map { ChooseAnswerIntent(view.tv_question.tag as String, it) }
            .distinctUntilChanged { it -> it.questionId }
            .cast(LiveGameIntent::class.java)
    }

    private fun initialIntent() = Observable.just(LiveGameIntent.InitialIntent)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val layout = inflater.inflate(R.layout.live_game, container, false)
        retainViewMode = RETAIN_DETACH
        initLinearTimer(layout.linear_timer)
        DaggerLiveGameComponent.builder()
            .singletonComponent((activity as MainActivity).singletonComponent())
            .build()
            .inject(this)
        //initVideoPlayer(layout)
        bindIntents(layout)
        return layout
    }

    private fun initLinearTimer(linearTimerView: LinearTimerView) {
        linearTimerView.circleRadiusInDp = linearTimerRadius
        linearTimer = Builder()
            .linearTimerView(linearTimerView)
            .duration(5 * 1000)
            .timerListener(object : TimerListener {
                override fun onTimerReset() {}
                override fun animationComplete() {}
                override fun timerTick(tickUpdateInMillis: Long) {}
            })
            .build()
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
            it.linear_timer.visibility = GONE
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

    /**
     * @implNote: This is pretty much a trial-and-error hack.
     */
    private val linearTimerRadius: Int by lazy {
        (Resources.getSystem().displayMetrics.widthPixels.dp / 9) + 1
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