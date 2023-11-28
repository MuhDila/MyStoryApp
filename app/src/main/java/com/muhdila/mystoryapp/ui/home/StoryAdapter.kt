package com.muhdila.mystoryapp.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.muhdila.mystoryapp.data.remote.response.ListStory
import com.muhdila.mystoryapp.databinding.ItemRowStoryBinding
import com.muhdila.mystoryapp.ui.detail.DetailActivity
import com.muhdila.mystoryapp.data.helper.loadImage
import com.muhdila.mystoryapp.data.pref.StoryModel

// Create a RecyclerView adapter for displaying a list of stories using paging
class StoryAdapter : PagingDataAdapter<ListStory, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    // Define the view holder class
    class MyViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        // Bind data to the view holder
        fun bind(data: ListStory) {
            // Load the story image using Glide
            with(binding) {
                image.loadImage(data.photoUrl) // Load and display the image
                binding.name.text = data.name // Set the story name
                binding.description.text = data.description // Set the story description
            }

            // Handle item click to view details of the story
            binding.root.setOnClickListener {
                val story = StoryModel(
                    data.name,
                    data.photoUrl,
                    data.description
                )

                // Start the DetailActivity with the selected story data and transition animation
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.DETAIL_STORY, story)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the layout for a single row of the story list
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Bind data to the view holder if it exists
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        // Define a DiffUtil.ItemCallback for efficient RecyclerView updates
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                // Check if the items are the same
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                // Check if the contents of the items are the same
                return oldItem == newItem
            }
        }

        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(
                oldStory: ListStory,
                newStory: ListStory
            ): Boolean {
                return oldStory == newStory
            }

            override fun areContentsTheSame(
                oldStory: ListStory,
                newStory: ListStory
            ): Boolean {
                return oldStory.name == newStory.name &&
                        oldStory.description == newStory.description &&
                        oldStory.photoUrl == newStory.photoUrl
            }
        }
    }
}