package com.leafy.storyboard.utils

import androidx.lifecycle.Observer
import com.leafy.storyboard.core.data.utils.Status

abstract class ObserverGenerator<T> {
    private val observer = Observer<Status<T>> { item ->
        if (item != null) {
            when (item) {
                is Status.Success -> getSuccessUI(item.data)
                is Status.Empty -> getEmptyUI()
                is Status.Error -> getErrorUI(item.message)
                is Status.Loading -> getLoadingUI()
            }
        }
    }

    abstract fun getSuccessUI(data: T?)

    abstract fun getEmptyUI()

    abstract fun getErrorUI(message: String?)

    abstract fun getLoadingUI()

    fun asObserver() = observer
}