package com.proshnotechnologies.proshno.login

import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.proshnotechnologies.proshno.R
import com.proshnotechnologies.proshno.home.HomeController
import kotlinx.android.synthetic.main.login.view.btn_login
import kotlinx.android.synthetic.main.login.view.et_password

class LoginController : Controller() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val layout = inflater.inflate(R.layout.login, container, false)
        layout.et_password.transformationMethod = PasswordTransformationMethod()
        layout.btn_login.setOnClickListener {
            router.pushController(RouterTransaction.with(HomeController())
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
        }

        return layout
    }
}