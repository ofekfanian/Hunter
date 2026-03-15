package com.ofek.hunter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.adapters.InterviewQuestionAdapter
import com.ofek.hunter.interfaces.QuestionCallback
import com.ofek.hunter.models.InterviewQuestion
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.SampleDataGenerator

/**
 * Fragment that loads and displays community questions from Firestore.
 */
class CommunityQuestionListFragment : Fragment() {

    companion object {
        private const val ARG_COMPANY = "company"

        fun newInstance(
            company: String? = null
        ): CommunityQuestionListFragment {
            return CommunityQuestionListFragment().apply {
                arguments = Bundle().apply {
                    company?.let { putString(ARG_COMPANY, it) }
                }
            }
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var progressBar: ProgressBar
    private var filterCompany: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filterCompany = arguments?.getString(ARG_COMPANY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community_question_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.questions_recycler)
        emptyState = view.findViewById(R.id.empty_state)
        progressBar = view.findViewById(R.id.questions_progress_bar)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    // Pulls questions from Firestore, falls back to sample data if empty or on error
    private fun loadQuestions() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.QUESTIONS)
            .get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                val all = result.mapNotNull { doc ->
                    doc.toObject(InterviewQuestion::class.java)?.also { it.id = doc.id }
                }
                val filtered = when {
                    filterCompany != null -> all.filter { it.companyName.equals(filterCompany, ignoreCase = true) }
                    else -> all
                }
                val questions = filtered.ifEmpty { SampleDataGenerator.getSampleQuestions() }
                showQuestions(questions)
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                progressBar.visibility = View.GONE
                showQuestions(SampleDataGenerator.getSampleQuestions())
            }
    }

    // Binds the question list to the adapter or shows empty state
    private fun showQuestions(questions: List<InterviewQuestion>) {
        progressBar.visibility = View.GONE

        if (questions.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE

            val adapter = InterviewQuestionAdapter(questions)
            adapter.questionCallback = object : QuestionCallback {
                override fun itemClicked(question: InterviewQuestion, position: Int) {
                    // Question detail view
                }

                override fun upvoteClicked(question: InterviewQuestion, position: Int) {
                    handleVote(question)
                }
            }
            recyclerView.adapter = adapter
        }
    }

    // Increments the upvote count in Firestore and reloads
    private fun handleVote(question: InterviewQuestion) {
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.QUESTIONS)
            .document(question.id)
            .update("upvotes", question.upvotes + 1)
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener
                loadQuestions()
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
            }
    }

    override fun onResume() {
        super.onResume()
        loadQuestions()
    }

    // Called externally to force a data reload
    fun refresh() {
        loadQuestions()
    }
}
