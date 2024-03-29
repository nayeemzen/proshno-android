package com.proshnotechnologies.proshno

import android.app.Application
import android.graphics.Typeface
import com.jakewharton.threetenabp.AndroidThreeTen
import com.proshnotechnologies.proshno.R.attr
import com.proshnotechnologies.proshno.di.AppModule
import com.proshnotechnologies.proshno.di.DaggerSingletonComponent
import com.proshnotechnologies.proshno.di.SingletonComponent
import es.dmoral.toasty.Toasty
import es.dmoral.toasty.Toasty.Config
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import timber.log.Timber
import timber.log.Timber.DebugTree
import uk.co.chrisjenx.calligraphy.CalligraphyConfig.Builder

class ProshnoApp : Application() {
    private lateinit var singletonComponent: SingletonComponent

    override fun onCreate() {
        super.onCreate()
        initToasty()
        initCalligraphy()
        initTimber()
        init310Abp()
        initDagger()
    }

    private fun initToasty() {
        Config.getInstance()
            .setToastTypeface(Typeface.createFromAsset(assets, "fonts/Raleway-SemiBold.ttf"))
            .apply()
    }

    private fun init310Abp() {
        AndroidThreeTen.init(this)
    }

    fun singletonComponent() = singletonComponent

    private fun initDagger() {
        singletonComponent = DaggerSingletonComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initCalligraphy() {
        CalligraphyConfig.initDefault(Builder()
            .setDefaultFontPath(DEFAULT_FONT)
            .setFontAttrId(attr.fontPath)
            .build())
    }

    companion object {
        const val DEFAULT_FONT = "fonts/Raleway-Regular.ttf"
    }
}