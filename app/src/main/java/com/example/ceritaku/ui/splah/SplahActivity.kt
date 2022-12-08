package com.example.ceritaku.ui.splah

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.ceritaku.R
import com.example.ceritaku.model.LoginResultModel
import com.example.ceritaku.preference.PreferenceLogin
import com.example.ceritaku.ui.home.MainActivity
import com.example.ceritaku.ui.login.LoginActivity
import com.example.ceritaku.util.Constanta

@SuppressLint("CustomSplashScreen")

class SplahActivity : AppCompatActivity() {
    private lateinit var preferenceLogin: PreferenceLogin
    private lateinit var loginResultModel: LoginResultModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splah)

        preferenceLogin = PreferenceLogin(this)
        loginResultModel = preferenceLogin.getUser()
        isLogin()
        setActionBar()
    }

    private fun setActionBar(){
        supportActionBar?.hide()
    }

    private fun process(intent: Intent) {
        val splashTimer: Long = Constanta.TIME_SPLASH
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, splashTimer)
    }

    private fun isLogin() {
        if (loginResultModel.name != null && loginResultModel.token != null && loginResultModel.userId != null) {
            val intent = Intent(this, MainActivity::class.java)
            process(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            process(intent)
        }
    }
}