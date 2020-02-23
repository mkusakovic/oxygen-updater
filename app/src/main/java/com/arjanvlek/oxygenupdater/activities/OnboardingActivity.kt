package com.arjanvlek.oxygenupdater.activities

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.arjanvlek.oxygenupdater.ActivityLauncher
import com.arjanvlek.oxygenupdater.OxygenUpdater
import com.arjanvlek.oxygenupdater.R
import com.arjanvlek.oxygenupdater.activities.OnboardingActivity.SimpleOnboardingFragment.Companion.ARG_PAGE_NUMBER
import com.arjanvlek.oxygenupdater.dialogs.Dialogs
import com.arjanvlek.oxygenupdater.extensions.enableEdgeToEdgeUiSupport
import com.arjanvlek.oxygenupdater.fragments.ChooserOnboardingFragment
import com.arjanvlek.oxygenupdater.fragments.DeviceChooserOnboardingFragment
import com.arjanvlek.oxygenupdater.fragments.UpdateMethodChooserOnboardingFragment
import com.arjanvlek.oxygenupdater.internal.KotlinCallback
import com.arjanvlek.oxygenupdater.internal.settings.SettingsManager
import com.arjanvlek.oxygenupdater.models.DeviceOsSpec
import com.arjanvlek.oxygenupdater.models.DeviceRequestFilter
import com.arjanvlek.oxygenupdater.utils.ContributorUtils
import com.arjanvlek.oxygenupdater.utils.Logger
import com.arjanvlek.oxygenupdater.utils.SetupUtils
import com.arjanvlek.oxygenupdater.utils.Utils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.fragment_onboarding_complete.*
import kotlin.math.abs

class OnboardingActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var activityLauncher: ActivityLauncher

    private var deviceChooserOnboardingFragment: ChooserOnboardingFragment? = null
    private var updateMethodChooserOnboardingFragment: ChooserOnboardingFragment? = null

    private var permissionCallback: KotlinCallback<Boolean>? = null

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            handlePageChangeCallback(position + 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        enableEdgeToEdgeUiSupport()

        settingsManager = SettingsManager(this)
        activityLauncher = ActivityLauncher(this)

        if (!settingsManager.getPreference(SettingsManager.PROPERTY_IGNORE_UNSUPPORTED_DEVICE_WARNINGS, false)) {
            val applicationData = application as OxygenUpdater
            applicationData.serverConnector!!.getDevices(DeviceRequestFilter.ALL) {
                val deviceOsSpec = Utils.checkDeviceOsSpec(applicationData.systemVersionProperties!!, it)

                if (!deviceOsSpec.isDeviceOsSpecSupported) {
                    displayUnsupportedDeviceOsSpecMessage(deviceOsSpec)
                }
            }
        }

        setupViewPager()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager?.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private fun setupViewPager() {
        viewPager.apply {
            // create the adapter that will return a fragment for each of the pages of the activity.
            adapter = OnboardingPagerAdapter()

            // attach TabLayout to ViewPager2
            TabLayoutMediator(tabLayout, this) { _, _ -> }.attach()

            registerOnPageChangeCallback(pageChangeCallback)
        }

        appBar.post {
            // adjust bottom margin on first load
            viewPagerContainer.updateLayoutParams<CoordinatorLayout.LayoutParams> { bottomMargin = appBar.totalScrollRange }

            // adjust bottom margin on scroll
            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                viewPagerContainer.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    bottomMargin = appBar.totalScrollRange - abs(verticalOffset)
                }
            })
        }
    }

    private fun handlePageChangeCallback(pageNumber: Int) {
        when (pageNumber) {
            1 -> {
                collapsingToolbarLayout.title = getString(R.string.onboarding_page_1_title)
                collapsingToolbarImage.setImageResource(R.drawable.logo_outline)
            }
            2 -> {
                deviceChooserOnboardingFragment?.fetchData()

                collapsingToolbarLayout.title = getString(R.string.onboarding_page_2_title)
                collapsingToolbarImage.setImageResource(R.drawable.smartphone)
            }
            3 -> {
                updateMethodChooserOnboardingFragment?.fetchData()

                collapsingToolbarLayout.title = getString(R.string.onboarding_page_3_title)
                collapsingToolbarImage.setImageResource(R.drawable.cloud_download)
            }
            4 -> {
                collapsingToolbarLayout.title = getString(R.string.onboarding_page_4_title)
                collapsingToolbarImage.setImageResource(R.drawable.done_all)
            }
            else -> {
                // no-op
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMoreInfoButtonClicked(view: View) = Dialogs.showContributorExplanation(this)

    @Suppress("UNUSED_PARAMETER")
    fun onStartAppButtonClicked(view: View) {
        if (settingsManager.checkIfSetupScreenIsFilledIn()) {
            if (onboardingPage4ContributeCheckbox.isChecked) {
                requestContributorStoragePermissions { granted: Boolean ->
                    if (granted) {
                        // 1st time, will save setting to true.
                        ContributorUtils(this).flushSettings(true)
                        settingsManager.savePreference(SettingsManager.PROPERTY_SETUP_DONE, true)
                        activityLauncher.Main()
                    } else {
                        Toast.makeText(this, R.string.contribute_allow_storage, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // not signed up, saving this setting will prevent contribute popups which belong to app updates.
                settingsManager.savePreference(SettingsManager.PROPERTY_SETUP_DONE, true)
                settingsManager.savePreference(SettingsManager.PROPERTY_CONTRIBUTE, false)
                activityLauncher.Main()
            }
        } else {
            val deviceId = settingsManager.getPreference(SettingsManager.PROPERTY_DEVICE_ID, -1L)
            val updateMethodId = settingsManager.getPreference(SettingsManager.PROPERTY_UPDATE_METHOD_ID, -1L)
            Logger.logWarning(TAG, SetupUtils.getAsError("Setup wizard", deviceId, updateMethodId))
            Toast.makeText(this, getString(R.string.settings_entered_incorrectly), Toast.LENGTH_LONG).show()
        }
    }

    private fun displayUnsupportedDeviceOsSpecMessage(deviceOsSpec: DeviceOsSpec) {
        // Do not show dialog if app was already exited upon receiving of devices from the server.
        if (isFinishing) {
            return
        }

        val resourceId: Int = when (deviceOsSpec) {
            DeviceOsSpec.CARRIER_EXCLUSIVE_OXYGEN_OS -> R.string.carrier_exclusive_device_warning_message
            DeviceOsSpec.UNSUPPORTED_OXYGEN_OS -> R.string.unsupported_device_warning_message
            DeviceOsSpec.UNSUPPORTED_OS -> R.string.unsupported_os_warning_message
            else -> R.string.unsupported_os_warning_message
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.unsupported_device_warning_title))
            .setPositiveButton(getString(R.string.download_error_close)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setMessage(getString(resourceId))
            .show()
    }

    private fun requestContributorStoragePermissions(permissionCallback: KotlinCallback<Boolean>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.permissionCallback = permissionCallback
            requestPermissions(arrayOf(MainActivity.VERIFY_FILE_PERMISSION), MainActivity.PERMISSION_REQUEST_CODE)
        } else {
            permissionCallback.invoke(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && requestCode == MainActivity.PERMISSION_REQUEST_CODE) {
            permissionCallback?.invoke(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        }
    }

    /**
     * Contains the basic / non interactive onboarding fragments.
     */
    class SimpleOnboardingFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            super.onCreateView(inflater, container, savedInstanceState)

            return when (arguments?.getInt(ARG_PAGE_NUMBER, 0)) {
                1 -> inflater.inflate(R.layout.fragment_onboarding_welcome, container, false)
                4 -> inflater.inflate(R.layout.fragment_onboarding_complete, container, false)
                else -> null
            }
        }

        companion object {
            /**
             * The fragment argument representing the page number for this fragment.
             */
            const val ARG_PAGE_NUMBER = "page_number"
        }
    }

    /**
     * A [FragmentStateAdapter] that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    inner class OnboardingPagerAdapter : FragmentStateAdapter(this) {

        /**
         * This is called to instantiate the fragment for the given page.
         * Return a [SimpleOnboardingFragment] (defined as a static inner class below).
         */
        override fun createFragment(position: Int) = (position + 1).let { pageNumber ->
            when (pageNumber) {
                2 -> DeviceChooserOnboardingFragment().apply { deviceChooserOnboardingFragment = this }
                3 -> UpdateMethodChooserOnboardingFragment().apply { updateMethodChooserOnboardingFragment = this }
                else -> SimpleOnboardingFragment().apply { arguments = bundleOf(ARG_PAGE_NUMBER to pageNumber) }
            }
        }

        /**
         * Show 4 total pages: Welcome screen, device selection, update method selection, and completion screen
         */
        override fun getItemCount() = 4
    }

    companion object {
        private const val TAG = "OnboardingActivity"
    }
}