package com.ofek.hunter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ofek.hunter.databinding.ItemCvFileBinding
import com.ofek.hunter.interfaces.CVCallback
import com.ofek.hunter.models.CVFile
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * RecyclerView adapter for displaying uploaded CV files.
 */
class CVAdapter(var cvFiles: List<CVFile> = listOf()) :
    RecyclerView.Adapter<CVAdapter.CVViewHolder>() {

    var cvCallback: CVCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CVViewHolder {
        val binding = ItemCvFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CVViewHolder(binding)
    }

    // Bind CV file info to the list item
    override fun onBindViewHolder(holder: CVViewHolder, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.cvFileName.text = fileName
                binding.cvFileSize.text = formatFileSize(fileSize)
                binding.cvUploadDate.text = formatDate(uploadedAt)
            }
        }
    }

    override fun getItemCount(): Int = cvFiles.size
    fun getItem(position: Int): CVFile = cvFiles[position]

    // Convert byte count to human-readable size string
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }

    // Format upload timestamp to dd/MM/yyyy HH:mm
    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    inner class CVViewHolder(val binding: ItemCvFileBinding) : RecyclerView.ViewHolder(binding.root) {
        // Set up click listeners for open, delete, and share actions
        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                cvCallback?.itemClicked(getItem(pos), pos)
            }
            binding.cvBTNDelete.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                cvCallback?.deleteClicked(getItem(pos), pos)
            }
            binding.cvBTNShare.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                cvCallback?.shareClicked(getItem(pos), pos)
            }
        }
    }
}
