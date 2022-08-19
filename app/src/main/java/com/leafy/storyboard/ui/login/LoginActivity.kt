package com.leafy.storyboard.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.leafy.storyboard.R
import com.leafy.storyboard.core.domain.model.Login
import com.leafy.storyboard.databinding.ActivityLoginBinding
import com.leafy.storyboard.ui.main.MainActivity
import com.leafy.storyboard.ui.register.RegisterActivity
import com.leafy.storyboard.utils.ObserverGenerator
import com.leafy.storyboard.utils.TextUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModel<LoginViewModel>()
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = resources.getString(R.string.login)

        triggerButtonEnable()

        val sp =
            getSharedPreferences(resources.getString(R.string.appDirectory), Context.MODE_PRIVATE)

        if (!sp.getString(resources.getString(R.string.tokenKey), "").isNullOrBlank()) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
            return
        }

        binding.etEmail.setText(sp.getString(resources.getString(R.string.emailKey), ""))
        playAnimation()

        binding.etEmail.invokeError = { e ->
            binding.layoutEmail.error = e
            triggerButtonEnable()
        }

        binding.etPassword.invokeError = { e ->
            binding.layoutPassword.error = e
            triggerButtonEnable()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.postLogin(email, password).observe(this, object : ObserverGenerator<Login>() {
                override fun getSuccessUI(data: Login?) {
                    sp.edit {
                        putString(resources.getString(R.string.tokenKey), data?.token)
                        putString(resources.getString(R.string.emailKey), email)
                    }

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun getEmptyUI() {}

                override fun getErrorUI(message: String?) {
                    setLoadingState(false)
                    if (!message.isNullOrBlank() && message.contains("400"))
                        binding.layoutEmail.error = resources.getString(R.string.emailNotExistMsg)
                    else if (!message.isNullOrBlank() && message.contains("401"))
                        binding.layoutPassword.error =
                            resources.getString(R.string.invalidPasswordMsg)
                    else AlertDialog.Builder(this@LoginActivity)
                        .setTitle(R.string.errorTitle)
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }

                override fun getLoadingUI() {
                    binding.layoutEmail.error = ""
                    binding.layoutPassword.error = ""
                    setLoadingState(true)
                }
            }.asObserver())
        }

        binding.btnRegisterNow.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation() {
        val emailAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.layoutEmail, View.TRANSLATION_Y, 10f, 0f)
                    .setDuration(200),
                ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, 1f).setDuration(200)
            )
        }
        val passAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.layoutPassword, View.TRANSLATION_Y, 10f, 0f)
                    .setDuration(200),
                ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, 1f).setDuration(200)
            )
        }
        val loginAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.btnLogin, View.TRANSLATION_Y, 10f, 0f)
                    .setDuration(200),
                ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)
            )
        }
        val registerAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.btnRegisterNow, View.TRANSLATION_Y, 10f, 0f)
                    .setDuration(200),
                ObjectAnimator.ofFloat(binding.btnRegisterNow, View.ALPHA, 1f).setDuration(200)
            )
        }

        AnimatorSet().apply {
            playSequentially(emailAnim, passAnim, loginAnim, registerAnim)
            start()
        }
    }

    private fun triggerButtonEnable() {
        binding.btnLogin.isEnabled = TextUtils.emailMatcher(binding.etEmail.text.toString())
                && TextUtils.passwordMatcher(binding.etPassword.text.toString())
    }

    private fun setLoadingState(state: Boolean) {
        binding.apply {
            etEmail.isEnabled = !state
            etPassword.isEnabled = !state
            btnLogin.isEnabled = !state
            btnRegisterNow.isEnabled = !state
            progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }
}