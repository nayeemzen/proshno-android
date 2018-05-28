package com.proshnotechnologies.proshno.utils.extensions

import android.content.res.Resources
import android.util.DisplayMetrics

val Int.dp: Int
    get()  {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
        return (this / (densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }