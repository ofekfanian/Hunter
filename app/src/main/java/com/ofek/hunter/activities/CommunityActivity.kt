package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityCommunityBinding
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper
import com.ofek.hunter.fragments.CommunityQuestionListFragment

/**
 * Community screen hosting the question list fragment.
 */
class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding
    private var questionListFragment: CommunityQuestionListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews(savedInstanceState)
    }

    // Set up header, bottom nav, fragment, and FAB
    private fun initViews(savedInstanceState: Bundle?) {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.community_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.rose_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_community)
        NavigationHelper.setupBottomNavigation(this, "community")
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupFragment(savedInstanceState)
        setupFAB()
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Attach or restore the question list fragment
    private fun setupFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            questionListFragment = CommunityQuestionListFragment.newInstance()

            supportFragmentManager.beginTransaction()
                .replace(R.id.community_fragment_container, questionListFragment!!)
                .commit()
        } else {
            questionListFragment = supportFragmentManager
                .findFragmentById(R.id.community_fragment_container) as? CommunityQuestionListFragment
        }
    }

    // Navigate to AddQuestionActivity when the FAB is tapped
    private fun setupFAB() {
        binding.communityFABAdd.setOnClickListener {
            startActivity(Intent(this, AddQuestionActivity::class.java))
        }
        binding.communityFABAdd.post { AnimationHelper.bounce(binding.communityFABAdd) }
    }

    override fun onResume() {
        super.onResume()
        questionListFragment?.refresh()
    }
}
