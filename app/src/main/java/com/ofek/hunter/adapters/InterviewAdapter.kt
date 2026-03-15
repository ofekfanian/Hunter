package com.ofek.hunter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ofek.hunter.databinding.ItemInterviewBinding
import com.ofek.hunter.interfaces.InterviewCallback
import com.ofek.hunter.models.Interview

/**
 * RecyclerView adapter for displaying interview cards.
 */
class InterviewAdapter(var interviews: List<Interview> = listOf()) :
    RecyclerView.Adapter<InterviewAdapter.InterviewViewHolder>() {

    var interviewCallback: InterviewCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewViewHolder {
        val binding = ItemInterviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InterviewViewHolder(binding)
    }

    // Bind interview details to the card
    override fun onBindViewHolder(holder: InterviewViewHolder, position: Int) {
        val interview = getItem(position)
        holder.binding.interviewItemCompany.text = interview.companyName
        holder.binding.interviewItemPosition.text = interview.position
        holder.binding.interviewItemDate.text = interview.formattedDate
        holder.binding.interviewItemTime.text = interview.formattedTime
        holder.binding.interviewItemType.text = interview.type
        holder.binding.interviewItemLocation.text = interview.location
    }

    override fun getItemCount(): Int = interviews.size
    fun getItem(position: Int): Interview = interviews[position]

    inner class InterviewViewHolder(val binding: ItemInterviewBinding) : RecyclerView.ViewHolder(binding.root) {
        // Set up click listener on the interview card
        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                interviewCallback?.itemClicked(getItem(pos), pos)
            }
        }
    }
}
