package com.ofek.hunter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ItemInterviewQuestionBinding
import com.ofek.hunter.interfaces.QuestionCallback
import com.ofek.hunter.models.InterviewQuestion

/**
 * RecyclerView adapter for community questions and tips.
 */
class InterviewQuestionAdapter(var questions: List<InterviewQuestion> = listOf()) :
    RecyclerView.Adapter<InterviewQuestionAdapter.QuestionViewHolder>() {

    var questionCallback: QuestionCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemInterviewQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    // Bind question/tip data and style the badge by type
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        with(holder) {
            with(getItem(position)) {
                val context = binding.root.context
                val displayName = if (isAnonymous || userName.isEmpty())
                    context.getString(R.string.anonymous) else userName
                binding.questionCompany.text = displayName
                binding.questionCompanyLogo.text = displayName.firstOrNull()?.uppercaseChar()?.toString()
                    ?: context.getString(R.string.initial_question_mark)
                binding.questionPosition.text = when {
                    companyName.isNotEmpty() && this.position.isNotEmpty() -> "${this.position}, $companyName"
                    companyName.isNotEmpty() -> companyName
                    this.position.isNotEmpty() -> this.position
                    else -> ""
                }
                binding.questionText.text = question
                val isTip = type == InterviewQuestion.TYPE_TIP
                binding.questionDifficulty.text = if (isTip)
                    context.getString(R.string.type_tip) else context.getString(R.string.type_question)
                binding.questionDifficulty.background = ContextCompat.getDrawable(
                    context, if (isTip) R.drawable.bg_badge_rose else R.drawable.bg_badge_blue
                )
                binding.questionDifficulty.setTextColor(ContextCompat.getColor(
                    context, if (isTip) R.color.stat_accent_rose else R.color.primary_blue
                ))
                binding.questionUpvotes.text = upvotes.toString()
                binding.questionUser.text = displayName
            }
        }
    }

    override fun getItemCount(): Int = questions.size
    fun getItem(position: Int): InterviewQuestion = questions[position]

    inner class QuestionViewHolder(val binding: ItemInterviewQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        // Set up click listeners for the card and upvote button
        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                questionCallback?.itemClicked(getItem(pos), pos)
            }
            binding.upvoteButton.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                questionCallback?.upvoteClicked(getItem(pos), pos)
            }
        }
    }
}
