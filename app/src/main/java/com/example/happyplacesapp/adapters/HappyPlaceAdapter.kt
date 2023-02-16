package com.example.happyplacesapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.utils.Constants
import com.example.happyplacesapp.activities.AddHappyPlaceActivity
import com.example.happyplacesapp.activities.MainActivity
import com.example.happyplacesapp.databinding.HappyPlaceRecyclerViewItemBinding
import com.example.happyplacesapp.happy_place_database.HappyPlaceEntity
import kotlinx.coroutines.launch

class HappyPlaceAdapter(private val context: Context, private val items: ArrayList<HappyPlaceEntity>) : RecyclerView.Adapter<HappyPlaceAdapter.HappyPlaceViewHolder>() {

    private var onClickListener : OnClickListener? = null


    fun setOnClickListener( onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
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
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
               onClickListener!!.onClick(position, item)
            }
        }
    }

    fun notifyEditItem (activity: Activity, position: Int, requestCode : Int) {
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(Constants.RV_HAPPY_PLACE_ITEM, items[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }
    fun notifyDeleteItem (position : Int) : HappyPlaceEntity {
        notifyItemRemoved(position)
        return items[position]
    }

    interface OnClickListener {
        fun onClick(position: Int, entity: HappyPlaceEntity)
    }
}