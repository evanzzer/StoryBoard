package com.leafy.storyboard.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.leafy.storyboard.R
import com.leafy.storyboard.core.domain.model.Story
import com.leafy.storyboard.databinding.ItemStoryBinding
import com.leafy.storyboard.ui.main.details.DetailActivity
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    class ListViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Story) {
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .centerCrop()
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_baseline_image_24)
                        .error(R.drawable.ic_baseline_broken_image_24)
                )
                .into(binding.imgStory)

            binding.tvName.text = item.name

            // Android SDK 26+ TODO: Parse support for 25 and lower
            binding.tvDate.text =
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
                    .format(
                        ZonedDateTime.parse(item.createdAt)
                            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                    )

            binding.root.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imgStory, itemView.context.resources.getString(R.string.image))
                    )

                itemView.context.startActivity(
                    Intent(itemView.context, DetailActivity::class.java).also {
                        it.putExtra(DetailActivity.EXTRA_DATA, item)
                    }, optionsCompat.toBundle()
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id
        }
    }
}