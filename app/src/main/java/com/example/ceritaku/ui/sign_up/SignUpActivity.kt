package com.example.ceritaku.ui.sign_up

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
import com.example.ceritaku.databinding.ActivitySignUpBinding
import com.example.ceritaku.factory.ViewModelFactory
import com.example.ceritaku.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val signUpViewModel: SignUpViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        toLoginActivity()
        buttonRegister()
        playAnimation()
        setActionBar()

    }

    private fun toLoginActivity() {
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setViewModel() {
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun setActionBar(){
        supportActionBar?.title = "Sign Up"
    }

    private fun buttonRegister() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edtNameRegister.text.toString().trim()
            val email = binding.edtEmailRegister.text.toString().trim()
            val password = binding.edtPasswordRegister.text.toString().trim()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                processSignUp(name, email, password)
            } else {
                Toast.makeText(this, "Lengkapi Form", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formSignUp() {
        binding.edtNameRegister.text?.clear()
        binding.edtEmailRegister.text?.clear()
        binding.edtPasswordRegister.text?.clear()
    }

    private fun processSignUp(name: String, email: String, password: String) {
        signUpViewModel.register(name, email, password).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading ->{
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Ada yang tidak beres", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        formSignUp()
                        startActivity(Intent(this, LoginActivity::class.java))
                        Toast.makeText(this, "Berhasil Daftar Akun ${it.data.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.tvTitleRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val edtName =
            ObjectAnimator.ofFloat(binding?.textInputName, View.ALPHA, 1f).setDuration(500)
        val edtEmail =
            ObjectAnimator.ofFloat(binding?.textInputEmail, View.ALPHA, 1f).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(binding?.textPw, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).setDuration(500)
        val sudahPunyaAkun =
            ObjectAnimator.ofFloat(binding?.tvBelumPunyaAkun, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding?.tvLogin, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playSequentially(
                edtName,
                edtEmail,
                edtPassword,
                btnRegister,
                sudahPunyaAkun,
                login
            )
            start()
        }
    }
}