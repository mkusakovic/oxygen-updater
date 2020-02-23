package com.arjanvlek.oxygenupdater.activities

import android.annotation.TargetApi
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.arjanvlek.oxygenupdater.ActivityLauncher
import com.arjanvlek.oxygenupdater.OxygenUpdater
import com.arjanvlek.oxygenupdater.R
import com.arjanvlek.oxygenupdater.internal.settings.SettingsManager

class SplashActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var activityLauncher: ActivityLauncher

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsManager = SettingsManager(this)
        activityLauncher = ActivityLauncher(this)

        // Support functions for Android 8.0 "Oreo" and up.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createPushNotificationChannel()
            createProgressNotificationChannel()
        }

        migrateOldSettings()

        // chooseActivityToLaunch()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val storageManager = getSystemService<StorageManager>()!!
            val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
            startActivityForResult(intent, MainActivity.PERMISSION_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val treeUri = data!!.data!!
            val takeFlags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            Log.e("TAG", "takePersistableUriPermission: $treeUri")
            contentResolver.takePersistableUriPermission(treeUri, takeFlags)
        }
    }

    /**
     * Create a notification channel for push notifications (update data, news, etc.)
     *
     * Only supported in Oreo (26) and above
     */
    @TargetApi(26)
    private fun createPushNotificationChannel() {
        // The id of the channel.
        val id = OxygenUpdater.PUSH_NOTIFICATION_CHANNEL_ID

        // The user-visible name of the channel.
        val name: CharSequence = getString(R.string.push_notification_channel_name)

        // The user-visible description of the channel.
        NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
            // Configure the notification channel.
            description = getString(R.string.push_notification_channel_description)
            enableLights(true)

            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(this)
        }
    }

    /**
     * Create a notification channel for download progress notifications
     *
     * Only supported in Oreo (26) and above
     */
    @TargetApi(26)
    private fun createProgressNotificationChannel() {
        // The id of the channel.
        val id = OxygenUpdater.PROGRESS_NOTIFICATION_CHANNEL_ID

        // The user-visible name of the channel.
        val name = getString(R.string.progress_notification_channel_name)

        // The user-visible description of the channel.
        NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW).apply {
            // Configure the notification channel.
            description = getString(R.string.progress_notification_channel_description)

            enableLights(false)
            enableVibration(false)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(this)
        }
    }

    /**
     * Migrate settings from old versions of the app, if any
     */
    private fun migrateOldSettings() {
        // App version 2.4.6: Migrated old setting Show if system is up to date (default: ON) to Advanced mode (default: OFF).
        if (settingsManager.containsPreference(SettingsManager.PROPERTY_SHOW_IF_SYSTEM_IS_UP_TO_DATE)) {
            settingsManager.savePreference(SettingsManager.PROPERTY_ADVANCED_MODE, !settingsManager.getPreference(SettingsManager.PROPERTY_SHOW_IF_SYSTEM_IS_UP_TO_DATE, true))
            settingsManager.deletePreference(SettingsManager.PROPERTY_SHOW_IF_SYSTEM_IS_UP_TO_DATE)
        }
    }

    private fun chooseActivityToLaunch() {
        // Mark the welcome tutorial as finished if the user is moving from older app version.
        // This is checked by either having stored update information for offline viewing,
        // or if the last update checked date is set (if user always had up to date system and never viewed update information before)
        if (!settingsManager.getPreference(SettingsManager.PROPERTY_SETUP_DONE, false)
            && (settingsManager.checkIfOfflineUpdateDataIsAvailable() || settingsManager.containsPreference(SettingsManager.PROPERTY_UPDATE_CHECKED_DATE))
        ) {
            settingsManager.savePreference(SettingsManager.PROPERTY_SETUP_DONE, true)
        }

        if (!settingsManager.getPreference(SettingsManager.PROPERTY_SETUP_DONE, false)) {
            // launch OnboardingActivity since the app hasn't been setup yet
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            // setup is complete, launch MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }
}