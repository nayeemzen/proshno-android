package com.proshnotechnologies.proshno

import android.app.Application
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class ProshnoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
            .setDefaultFontPath(DEFAULT_FONT)
            .setFontAttrId(R.attr.fontPath)
            .build())
    }

    companion object {
        const val DEFAULT_FONT = "fonts/Raleway-Regular.ttf"
    }
}