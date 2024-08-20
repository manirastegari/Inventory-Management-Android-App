package com.example.property_app_g02

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.property_app_g02.databinding.WatchListRvLayoutBinding

class WatchListAdapter(
    private val listings: List<Listing>,
    private val onRemoveClickListener: OnRemoveClickListener
) : RecyclerView.Adapter<WatchListAdapter.ViewHolder>() {

    class ViewHolder(val binding: WatchListRvLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WatchListRvLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = listings[position]
        holder.binding.propertyPrice.text = "Price: ${listing.price}"
        holder.binding.propertyAddress.text = "Address: ${listing.address}"
        holder.binding.propertyBedrooms.text = "Bedrooms: ${listing.bedrooms}"
        Glide.with(holder.binding.root.context).load(listing.imageUrl).into(holder.binding.propertyImage)
        holder.binding.btnRemove.setOnClickListener {
            onRemoveClickListener.onRemoveClick(listing)
        }
    }

    override fun getItemCount() = listings.size
}