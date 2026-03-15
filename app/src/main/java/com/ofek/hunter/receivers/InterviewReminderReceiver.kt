package com.ofek.hunter.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ofek.hunter.App
import com.ofek.hunter.R
import com.ofek.hunter.activities.InterviewsActivity

/**
 * Broadcast receiver that fires interview reminder notifications.
 */
class InterviewReminderReceiver : BroadcastReceiver() {

    // Build and show the reminder notification when the alarm fires
    override fun onReceive(context: Context, intent: Intent) {
        val company = intent.getStringExtra(EXTRA_COMPANY) ?: return
        val position = intent.getStringExtra(EXTRA_POSITION) ?: ""
        val interviewId = intent.getStringExtra(EXTRA_INTERVIEW_ID) ?: ""

        val tapIntent = Intent(context, InterviewsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            interviewId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(R.string.reminder_notification_title)
        val body = context.getString(R.string.reminder_notification_body, company, position)

        val notification = NotificationCompat.Builder(context, App.CHANNEL_INTERVIEW_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(interviewId.hashCode(), notification)
        } catch (_: SecurityException) {
            // Permission not granted — ignore silently
        }
    }

    companion object {
        const val EXTRA_COMPANY = "extra_company"
        const val EXTRA_POSITION = "extra_position"
        const val EXTRA_INTERVIEW_ID = "extra_interview_id"
    }
}
