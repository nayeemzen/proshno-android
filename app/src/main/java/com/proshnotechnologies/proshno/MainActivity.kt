package com.proshnotechnologies.proshno

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.google.firebase.auth.FirebaseAuth
import com.proshnotechnologies.proshno.auth.ui.SignInController
import com.proshnotechnologies.proshno.home.ui.HomeController
import com.proshnotechnologies.proshno.live.LiveGameController
import kotlinx.android.synthetic.main.activity_main.controller_container
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var router: Router
    private val auth: FirebaseAuth by lazy {
        singletonComponent().firebaseAuth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        router = Conductor.attachRouter(this, controller_container, savedInstanceState)
        if (!router.hasRootController()) {
            val controller : Controller = if (auth.currentUser == null) {
                SignInController()
            } else {
                LiveGameController()
            }

            router.setRoot(RouterTransaction.with(controller)
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun singletonComponent() = (application as ProshnoApp).singletonComponent()
}
