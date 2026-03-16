package com.ofek.hunter.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityStatisticsBinding
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Statistics dashboard with Vico bar chart and status counts.
 */
class StatisticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticsBinding
    private val chartModelProducer = CartesianChartModelProducer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
        setupChart()
        loadStatistics()
    }

    // Set up header, bottom nav, and back button
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.statistics_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.stat_section_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_statistics)
        NavigationHelper.setupBottomNavigation(this, "statistics")
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
    }

    // Build the Vico bar chart with per-bar colors and rounded corners
    private fun setupChart() {
        val barShape = Shape.rounded(allPercent = 12)
        val defaultColumn = LineComponent(color = getColor(R.color.stat_accent_violet), thicknessDp = 40f, shape = barShape)
        val columnProvider = object : ColumnCartesianLayer.ColumnProvider {
            override fun getColumn(
                entry: ColumnCartesianLayerModel.Entry,
                seriesIndex: Int,
                extraStore: ExtraStore
            ): LineComponent {
                val color = when (entry.x.toInt()) {
                    0 -> getColor(R.color.stat_accent_violet)
                    1 -> getColor(R.color.stat_accent_blue)
                    2 -> getColor(R.color.stat_accent_teal)
                    else -> getColor(R.color.stat_accent_rose)
                }
                return LineComponent(color = color, thicknessDp = 40f, shape = barShape)
            }

            override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore): LineComponent {
                return defaultColumn
            }
        }
        binding.statisticsChart.chart = CartesianChart(
            ColumnCartesianLayer(columnProvider = columnProvider)
        )
        binding.statisticsChart.modelProducer = chartModelProducer
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Load job counts from Firestore and feed data to the chart
    private fun loadStatistics() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user?.email == "ofekfanian689@gmail.com") {
            loadDemoStatistics()
            return
        }

        val userId = user?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val jobs = result.mapNotNull { doc -> doc.toObject(JobApplication::class.java) }
                val applied   = jobs.count { it.jobType == "Applied" }
                val interview = jobs.count { it.jobType == "Interview" }
                val offer     = jobs.count { it.jobType == "Offer" }
                val rejected  = jobs.count { it.jobType == "Rejected" }

                showStatistics(applied, interview, offer, rejected)
            }
            .addOnFailureListener { }
    }

    private fun loadDemoStatistics() {
        showStatistics(applied = 4, interview = 2, offer = 1, rejected = 1)
    }

    private fun showStatistics(applied: Int, interview: Int, offer: Int, rejected: Int) {
        findViewById<TextView>(R.id.stat_value_applied)?.text = applied.toString()
        findViewById<TextView>(R.id.stat_value_interview)?.text = interview.toString()
        findViewById<TextView>(R.id.stat_value_offer)?.text = offer.toString()
        findViewById<TextView>(R.id.stat_value_rejected)?.text = rejected.toString()

        val total = applied + interview + offer + rejected
        val hasData = total > 0
        if (hasData) {
            binding.statisticsChartTitle.visibility = View.VISIBLE
            binding.statisticsChartCard.visibility = View.VISIBLE
            binding.statisticsEmptyCard.visibility = View.GONE

            // Show insights row
            binding.statisticsInsightsTitle.visibility = View.VISIBLE
            binding.statisticsInsightsRow.visibility = View.VISIBLE
            setupInsightCards(offer, total)

            // Show summary
            binding.statisticsSummaryTitle.visibility = View.VISIBLE
            binding.statisticsSummaryCard.visibility = View.VISIBLE
            setupSummary(interview, offer, total)

            lifecycleScope.launch(Dispatchers.Main) {
                chartModelProducer.runTransaction {
                    columnSeries { series(applied, interview, offer, rejected) }
                }
            }
        } else {
            binding.statisticsChartTitle.visibility = View.GONE
            binding.statisticsChartCard.visibility = View.GONE
            binding.statisticsInsightsTitle.visibility = View.GONE
            binding.statisticsInsightsRow.visibility = View.GONE
            binding.statisticsSummaryTitle.visibility = View.GONE
            binding.statisticsSummaryCard.visibility = View.GONE
            binding.statisticsEmptyCard.visibility = View.VISIBLE
        }
    }

    private fun setupInsightCards(offer: Int, total: Int) {
        val successRate = if (total > 0) (offer * 100) / total else 0
        binding.statValueSuccessRate.text = getString(R.string.percent_format, successRate)

        // Avg response in days (demo: 4d, real: estimate based on data)
        val avgDays = if (total >= 5) (total / 5).coerceIn(1, 14) else 4
        binding.statValueAvgResponse.text = getString(R.string.days_format, avgDays)

        // Apps per week (estimate: total / 4 weeks)
        val appsPerWeek = if (total > 0) String.format(java.util.Locale.US, "%.1f", total / 4.0) else "0"
        binding.statValueAppsWeek.text = appsPerWeek
    }

    private fun setupSummary(interview: Int, offer: Int, total: Int) {
        val interviewRate = if (total > 0) (interview * 100) / total else 0
        val successRate = if (total > 0) (offer * 100) / total else 0

        binding.statInsight1.text = if (interviewRate >= 30)
            getString(R.string.statistics_insight_above_avg, interviewRate)
        else
            getString(R.string.statistics_insight_below_avg, interviewRate)

        binding.statInsight2.text = getString(R.string.statistics_insight_keep_applying)

        binding.statInsight3.text = if (successRate >= 10)
            getString(R.string.statistics_insight_strong_quality)
        else
            getString(R.string.statistics_insight_keep_going)
    }
}
