package com.example.happyplacesapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.databinding.HappyPlaceRecyclerViewItemBinding
import com.example.happyplacesapp.happy_place_database.HappyPlaceEntity

class HappyPlaceAdapter(private val items: ArrayList<HappyPlaceEntity>) : RecyclerView.Adapter<HappyPlaceAdapter.HappyPlaceViewHolder>() {

    inner class HappyPlaceViewHolder(binding : HappyPlaceRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val civLocation = binding.civLocation
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HappyPlaceViewHolder {
        return HappyPlaceViewHolder(HappyPlaceRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size

    }

    override fun onBindViewHolder(holder: HappyPlaceViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        //binding
        holder.civLocation.setImageURI(Uri.parse(item.image))
        holder.tvTitle.text = item.name
        holder.tvDescription.text = item.description

    }
}