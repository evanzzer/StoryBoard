package com.leafy.storyboard.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import com.google.android.material.textfield.TextInputEditText
import com.leafy.storyboard.R

class PasswordEditTest : TextInputEditText {
    var invokeError: ((String) -> Unit)? = null

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) :
            super(ctx, attrs)

    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle)

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val t = p0.toString()
                invokeError?.invoke(when {
                    t.isBlank() -> resources.getString(R.string.emptyMsg)
                    !TextUtils.passwordMatcher(t) -> resources.getString(R.string.passwordErrorMsg)
                    else -> ""
                })
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            this.clearFocus()

        return super.onKeyPreIme(keyCode, event)
    }
}