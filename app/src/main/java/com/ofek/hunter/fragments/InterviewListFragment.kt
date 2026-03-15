package com.ofek.hunter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.adapters.InterviewAdapter
import com.ofek.hunter.interfaces.InterviewCallback
import com.ofek.hunter.models.Interview
import com.ofek.hunter.utilities.Constants
import android.widget.Toast

/**
 * Fragment that shows a filtered list of upcoming or past interviews.
 */
class InterviewListFragment : Fragment() {

    companion object {
        private const val ARG_TYPE = "type"
        const val TYPE_UPCOMING = 0
        const val TYPE_PAST = 1

        fun newInstance(type: Int): InterviewListFragment {
            return InterviewListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TYPE, type)
                }
            }
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var emptyStateText: TextView
    private var listType: Int = TYPE_UPCOMING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listType = arguments?.getInt(ARG_TYPE) ?: TYPE_UPCOMING
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interview_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.interviews_recycler)
        emptyState = view.findViewById(R.id.empty_state)
        emptyStateText = emptyState.findViewById(R.id.empty_text)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    // Fetches interviews from Firestore and filters by upcoming or past
    private fun loadInterviews() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.INTERVIEWS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                val all = result.mapNotNull { doc ->
                    doc.toObject(Interview::class.java)?.also { it.id = doc.id }
                }
                val filtered = when (listType) {
                    TYPE_UPCOMING -> all.filter { it.isUpcoming }
                    TYPE_PAST -> all.filter { it.isPast }
                    else -> all
                }
                showInterviews(filtered)
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                showInterviews(emptyList())
            }
    }

    // Updates the RecyclerView or shows empty state
    private fun showInterviews(interviews: List<Interview>) {
        if (interviews.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
            emptyStateText.text = when (listType) {
                TYPE_UPCOMING -> getString(R.string.no_upcoming_interviews)
                TYPE_PAST -> getString(R.string.no_past_interviews)
                else -> ""
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE

            val adapter = InterviewAdapter(interviews)
            adapter.interviewCallback = object : InterviewCallback {
                override fun itemClicked(interview: Interview, position: Int) {
                    Toast.makeText(requireContext(), interview.companyName, Toast.LENGTH_SHORT).show()
                }
            }
            recyclerView.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        loadInterviews()
    }
}
