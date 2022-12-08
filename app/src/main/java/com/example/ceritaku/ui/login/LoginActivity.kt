package com.example.ceritaku.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.ceritaku.data.Result
import com.example.ceritaku.databinding.ActivityLoginBinding
import com.example.ceritaku.factory.ViewModelFactory
import com.example.ceritaku.model.LoginResultModel
import com.example.ceritaku.model.ResponseLogin
import com.example.ceritaku.preference.PreferenceLogin
import com.example.ceritaku.ui.home.MainActivity
import com.example.ceritaku.ui.sign_up.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private  val loginViewModel: LoginViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        toSignUpActivity()
        buttonLogin()
        playAnimation()
        setActionBar()

    }

    private fun toSignUpActivity(){
        binding.tvRegsiter.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
    }

    private fun setViewModel(){
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun setActionBar(){
        supportActionBar?.title = "Login"
    }

    private fun buttonLogin(){
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                processLogin(email, password)
            } else {
                Toast.makeText(this, "Mohon Lengkapi Form", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processLogin(email: String, password:String){
        loginViewModel.authLogin(email, password).observe(this){
            if (it != null){
                when(it){
                    is Result.Loading ->{
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Gagal Login Periksa Email atau Password Anda atau Sinyal", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        succesLogin(it.data)
                        Toast.makeText(this, "Selamat Datang ${it.data.loginResult?.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun intentToHome(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun succesLogin(responseLogin: ResponseLogin){
        saveDataUser(responseLogin)
        intentToHome()
    }

    private fun saveDataUser(responseLogin: ResponseLogin){
        val preferenceLogin = PreferenceLogin(this)
        val resultLogin = responseLogin.loginResult
        val loginResultModel = LoginResultModel(
            name = resultLogin?.name, userId =  resultLogin?.userId, token = resultLogin?.token
        )
        preferenceLogin.setAuthLogin(loginResultModel)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivAnimationRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val edtEmail =
            ObjectAnimator.ofFloat(binding.textInputEmail, View.ALPHA, 1f).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(binding.textPw, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val belumPunyaAkun =
            ObjectAnimator.ofFloat(binding.tvBelumPunyaAkun, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.tvRegsiter, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                edtEmail,
                edtPassword,
                btnLogin,
                belumPunyaAkun,
                login
            )
            start()
        }
    }
}