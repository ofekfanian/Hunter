package com.ofek.hunter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ofek.hunter.R
import com.ofek.hunter.databinding.JobItemBinding
import com.ofek.hunter.interfaces.JobCallback
import com.ofek.hunter.models.JobApplication

/**
 * RecyclerView adapter for displaying job application cards.
 */
class JobAdapter(var jobs: List<JobApplication> = listOf()) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    var jobCallback: JobCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = JobItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    // Bind job data to the card
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.jobTXTCompanyInitial.text = company.firstOrNull()?.uppercase() ?: "?"
                binding.jobFRAMELogoContainer.getChildAt(0)?.setBackgroundResource(getAvatarGradient(company))
                binding.jobTXTCompanyName.text = company
                binding.jobTXTPosition.text = title
                binding.jobTXTLocation.text = location
                binding.jobTXTStatus.text = jobType
                binding.jobVIEWStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, getStatusColor(jobType))
                )
                if (salary.isNotEmpty() && salary != "0") {
                    binding.jobTXTSalary.text = salary
                    binding.jobTXTSalary.visibility = android.view.View.VISIBLE
                } else {
                    binding.jobTXTSalary.visibility = android.view.View.GONE
                }
                // Show date if available
                if (dateApplied > 0) {
                    binding.jobTXTDate.text = android.text.format.DateFormat.format("dd/MM/yyyy", dateApplied).toString()
                } else {
                    binding.jobTXTDate.text = ""
                }
                // Show work model badge
                if (workModel.isNotEmpty()) {
                    binding.jobTXTWorkModel.text = workModel
                    binding.jobTXTWorkModel.visibility = android.view.View.VISIBLE
                } else {
                    binding.jobTXTWorkModel.visibility = android.view.View.GONE
                }
                // Show source badge
                if (source.isNotEmpty()) {
                    binding.jobTXTSource.text = source
                    binding.jobTXTSource.visibility = android.view.View.VISIBLE
                } else {
                    binding.jobTXTSource.visibility = android.view.View.GONE
                }
                // Show CV version
                if (requirements.isNotEmpty()) {
                    binding.jobTXTCvVersion.text = requirements
                    binding.cvVersionContainer.visibility = android.view.View.VISIBLE
                } else {
                    binding.cvVersionContainer.visibility = android.view.View.GONE
                }
                binding.jobBTNFavorite.setImageResource(
                    if (isSaved) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
            }
        }
    }

    override fun getItemCount(): Int = jobs.size
    fun getItem(position: Int): JobApplication = jobs[position]

    // Pick color based on job status
    private fun getStatusColor(jobType: String): Int = when (jobType) {
        "Saved" -> R.color.status_saved
        "Applied" -> R.color.status_applied
        "Interview" -> R.color.status_interview
        "Offer" -> R.color.status_offer
        "Rejected" -> R.color.status_rejected
        else -> R.color.white
    }

    // Pick a gradient avatar based on company name (deterministic per company)
    private val avatarGradients = intArrayOf(
        R.drawable.bg_avatar_indigo,
        R.drawable.bg_avatar_teal,
        R.drawable.bg_avatar_rose,
        R.drawable.bg_avatar_amber,
        R.drawable.bg_avatar_emerald,
        R.drawable.bg_avatar_sky
    )

    private fun getAvatarGradient(company: String): Int {
        val index = (company.hashCode() and Int.MAX_VALUE) % avatarGradients.size
        return avatarGradients[index]
    }

    inner class JobViewHolder(val binding: JobItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Set up click listeners for the card and favorite button
        init {
            binding.jobCARDRoot.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                jobCallback?.itemClicked(getItem(pos), pos)
            }
            binding.jobBTNFavorite.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                jobCallback?.favoriteClicked(getItem(pos), pos)
            }
        }
    }
}
