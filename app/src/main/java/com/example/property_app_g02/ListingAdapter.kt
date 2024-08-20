package com.example.property_app_g02

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.property_app_g02.databinding.LandlordListingRvLayoutBinding

class ListingAdapter(private val clickDetector: ClickDetectorInterface) : ListAdapter<Listing, ListingAdapter.ListingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = LandlordListingRvLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position), clickDetector)
    }

    class ListingViewHolder(private val binding: LandlordListingRvLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listing: Listing, clickDetector: ClickDetectorInterface) {
            binding.tvPrice.text = "Price: ${listing.price}"
            binding.tvBedrooms.text = "Bedrooms: ${listing.bedrooms}"
            if (listing.available) {
                binding.tvAvailable.text = "Availablity: Available"
            } else {
                binding.tvAvailable.text = "Availablity: Not Available"
            }
            binding.tvAddress.text = "Address: ${listing.address}"

            binding.btnDelete.setOnClickListener {
                clickDetector.onDeleteClick(listing)
            }
            binding.btnEdit.setOnClickListener {
                clickDetector.onEditClick(listing)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Listing>() {
        override fun areItemsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem == newItem
        }
    }
}