package com.ofek.hunter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ItemOnboardingPageBinding

/**
 * ViewPager2 adapter for the onboarding walkthrough pages.
 */
class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private val pages = listOf(
        OnboardingPage(
            titleRes = R.string.onboarding_title_1,
            descRes = R.string.onboarding_desc_1,
            iconRes = R.drawable.ic_work,
            circleRes = R.drawable.circle_glass_indigo,
            tintColorRes = R.color.accent_indigo
        ),
        OnboardingPage(
            titleRes = R.string.onboarding_title_2,
            descRes = R.string.onboarding_desc_2,
            iconRes = R.drawable.ic_cv_document,
            circleRes = R.drawable.circle_glass_teal,
            tintColorRes = R.color.accent_teal
        ),
        OnboardingPage(
            titleRes = R.string.onboarding_title_3,
            descRes = R.string.onboarding_desc_3,
            iconRes = R.drawable.ic_community,
            circleRes = R.drawable.circle_glass_rose,
            tintColorRes = R.color.accent_rose
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OnboardingViewHolder(binding)
    }

    // Populate the onboarding page with title, description, and icon
    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val page = pages[position]
        holder.binding.onboardingTitle.setText(page.titleRes)
        holder.binding.onboardingDescription.setText(page.descRes)
        holder.binding.onboardingIcon.setImageResource(page.iconRes)
        holder.binding.onboardingIcon.imageTintList =
            android.content.res.ColorStateList.valueOf(holder.itemView.context.getColor(page.tintColorRes))
        holder.binding.onboardingCircle.setBackgroundResource(page.circleRes)
    }

    override fun getItemCount(): Int = pages.size

    class OnboardingViewHolder(val binding: ItemOnboardingPageBinding) :
        RecyclerView.ViewHolder(binding.root)

    private data class OnboardingPage(
        val titleRes: Int,
        val descRes: Int,
        val iconRes: Int,
        val circleRes: Int,
        val tintColorRes: Int
    )
}
