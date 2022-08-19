package com.leafy.storyboard.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.leafy.storyboard.databinding.ItemLoadingBinding

class LoadingAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingAdapter.ListViewHolder>() {
    class ListViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.layoutError.btnRefresh.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.layoutError.tvError.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.layoutError.root.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ListViewHolder =
        ListViewHolder(ItemLoadingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ), retry)
}