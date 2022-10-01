package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ListItemBinding

class MainAdapter(val clickListener: MainClickListener) : ListAdapter<Asteroid, MainViewHolder>(MainDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.updateUI(getItem(position), clickListener)
    }
}

class MainViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun updateUI(item: Asteroid, clickListener: MainClickListener) {
        binding.asteroid = item
        binding.executePendingBindings()
        binding.clickListener = clickListener
    }

    companion object {
        fun inflate(parent: ViewGroup) : MainViewHolder{
            val inflater = LayoutInflater.from(parent.context)
            val view = ListItemBinding.inflate(inflater, parent, false)
            return MainViewHolder(view)
        }
    }
}

class MainDiffUtil : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }
}

class MainClickListener(val clickListener: (item: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}