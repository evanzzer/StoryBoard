package com.leafy.storyboard.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.leafy.storyboard.R
import com.leafy.storyboard.databinding.ActivityRegisterBinding
import com.leafy.storyboard.utils.ObserverGenerator
import com.leafy.storyboard.utils.TextUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModel<RegisterViewModel>()
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        triggerButtonEnable()
        playAnimation()

        binding.etName.doOnTextChanged { text, _, _, _ ->
            binding.layoutName.error = if (text.toString().isBlank())
                resources.getString(R.string.emptyMsg) else ""
            triggerButtonEnable()
        }

        binding.etEmail.invokeError = { e ->
            binding.layoutEmail.error = e
            triggerButtonEnable()
        }

        binding.etPassword.invokeError = { e ->
            binding.layoutPassword.error = e
            triggerButtonEnable()
        }

        binding.btnRegister.setOnClickListener {
            viewModel.postRegister(
                binding.etName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            ).observe(this, object : ObserverGenerator<String>() {
                override fun getSuccessUI(data: String?) {
                    Toast.makeText(
                        this@RegisterActivity,
                        R.string.successRegisterMsg,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                }

                override fun getEmptyUI() {}

                override fun getErrorUI(message: String?) {
                    setLoadingState(false)
                    if (!message.isNullOrBlank() && message.contains("400"))
                        binding.layoutEmail.error =
                            resources.getString(R.string.emailAlreadyExistMsg)
                    else AlertDialog.Builder(this@RegisterActivity)
                        .setTitle(R.string.errorTitle)
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }

                override fun getLoadingUI() {
                    binding.layoutEmail.error = ""
                    setLoadingState(true)
                }
            }.asObserver())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun triggerButtonEnable() {
        binding.btnRegister.isEnabled = binding.etName.text.toString().isNotBlank()
                && TextUtils.emailMatcher(binding.etEmail.text.toString())
                && TextUtils.passwordMatcher(binding.etPassword.text.toString())
    }

    private fun playAnimation() {
        val nameAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.layoutName, View.TRANSLATION_Y, 10f, 0f)
                    .setDuration(200),
                ObjectAnimator.ofFloat(binding.layoutName, View.ALPHA, 1f).setDuration(200)
            )
        }
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
        val registerAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.btnRegister, View.TRANSLATION_Y, 10f, 0f)
                    .setDuration(200),
                ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)
            )
        }
        val loginAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.btnLoginNow, View.TRANSLATION_Y, 10f, 0f)
                    .setDuration(200),
                ObjectAnimator.ofFloat(binding.btnLoginNow, View.ALPHA, 1f).setDuration(200)
            )
        }

        AnimatorSet().apply {
            playSequentially(nameAnim, emailAnim, passAnim, registerAnim, loginAnim)
            start()
        }
    }

    private fun setLoadingState(state: Boolean) {
        binding.apply {
            etName.isEnabled = !state
            etEmail.isEnabled = !state
            etPassword.isEnabled = !state
            btnRegister.isEnabled = !state
            btnLoginNow.isEnabled = !state
            progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }
}