package com.apkfood.wavesoffoodadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.apkfood.wavesoffoodadmin.R
import com.apkfood.wavesoffoodadmin.model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(
    private val onUserClick: (User) -> Unit,
    private val onUserAction: (User, String) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProfileImage: CircleImageView = itemView.findViewById(R.id.ivProfileImage)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        private val tvUserPhone: TextView = itemView.findViewById(R.id.tvUserPhone)
        private val tvTotalOrders: TextView = itemView.findViewById(R.id.tvTotalOrders)
        private val tvTotalSpent: TextView = itemView.findViewById(R.id.tvTotalSpent)
        private val tvJoinDate: TextView = itemView.findViewById(R.id.tvJoinDate)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        private val btnAction: MaterialButton = itemView.findViewById(R.id.btnAction)

        fun bind(user: User) {
            tvUserName.text = user.getDisplayName()
            tvUserEmail.text = user.email
            tvUserPhone.text = user.getPhoneDisplay()
            tvTotalOrders.text = user.getTotalOrdersDisplay().toString()
            
            // Format currency
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val formattedAmount = formatter.format(user.totalSpent).replace("IDR", "Rp")
            tvTotalSpent.text = formattedAmount
            
            // Format join date
            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            tvJoinDate.text = dateFormat.format(Date(user.getJoinDateLong()))
            
            // Load profile image
            if (user.profileImageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivProfileImage)
            }
            
            // Set status
            if (user.isActive) {
                chipStatus.text = "Active"
                chipStatus.setChipBackgroundColorResource(R.color.success_green)
                btnAction.text = "Ban"
                btnAction.setTextColor(itemView.context.getColor(R.color.error_red))
                btnAction.strokeColor = itemView.context.getColorStateList(R.color.error_red)
            } else {
                chipStatus.text = "Banned"
                chipStatus.setChipBackgroundColorResource(R.color.error_red)
                btnAction.text = "Unban"
                btnAction.setTextColor(itemView.context.getColor(R.color.success_green))
                btnAction.strokeColor = itemView.context.getColorStateList(R.color.success_green)
            }
            
            // Click listeners
            itemView.setOnClickListener {
                onUserClick(user)
            }
            
            btnAction.setOnClickListener {
                val action = if (user.isActive) "ban" else "unban"
                onUserAction(user, action)
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
