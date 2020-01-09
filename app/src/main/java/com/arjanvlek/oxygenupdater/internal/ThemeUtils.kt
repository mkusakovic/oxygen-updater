package com.arjanvlek.oxygenupdater.internal

import android.content.Context
import android.content.res.Configuration
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import com.arjanvlek.oxygenupdater.R
import com.arjanvlek.oxygenupdater.internal.Theme.AUTO
import com.arjanvlek.oxygenupdater.internal.Theme.DARK
import com.arjanvlek.oxygenupdater.internal.Theme.LIGHT
import com.arjanvlek.oxygenupdater.internal.Theme.SYSTEM
import com.arjanvlek.oxygenupdater.settings.SettingsManager
import java.util.*

/**
 * @author Adhiraj Singh Chauhan (github.com/adhirajsinghchauhan)
 */
object ThemeUtils {

    const val OEM_BLACK_MODE = "oem_black_mode"

    /**
     * Translates the theme chosen by the user to the corresponding [NightMode].
     *
     * Since [androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_TIME] is deprecated, we're considering night to last from 19:00 to 06:00
     * <br></br>
     * Cases:
     *
     *  * Light/Dark: `MODE_NIGHT_NO/MODE_NIGHT_YES`
     *  * Auto: `MODE_NIGHT_YES` if it's night-time. Otherwise, `MODE_NIGHT_AUTO_BATTERY`
     *  * System: `MODE_NIGHT_FOLLOW_SYSTEM` above Android Pie (9.0). Otherwise, MODE_NIGHT_AUTO_BATTERY
     *
     *
     * @param context the context
     *
     * @return the [NightMode] to apply using [androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode]
     */
    @NightMode
    fun translateThemeToNightMode(context: Context): Int {
        val theme = SettingsManager(context).getPreference(context.getString(R.string.key_theme_id), context.resources.getInteger(R.integer.theme_system_id))
        return translateThemeToNightMode(context, Theme.Companion[theme])
    }

    private fun translateThemeToNightMode(context: Context, theme: Theme): Int {
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        return when (theme) {
            LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AUTO -> {
                val hour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]

                if (hour in 19..23 || hour in 0..6) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            SYSTEM -> {
                val translatedTheme = translateOnePlusTheme(context)

                // if the user has chosen Dark theme for their OnePlus device, honor it.
                // otherwise resort to Android-provided modes
                if (translatedTheme == DARK) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else if (translatedTheme == LIGHT) {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    // Android Pie (9.0) introduced a night mode system flag that could be set in developer options
                    if (VERSION.SDK_INT >= VERSION_CODES.P && translatedTheme != null) {
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    } else {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    }
                }
            }
            else -> {
                val translatedTheme = translateOnePlusTheme(context)

                if (translatedTheme == DARK) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else if (translatedTheme == LIGHT) {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    if (VERSION.SDK_INT >= VERSION_CODES.P && translatedTheme != null) {
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    } else {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    }
                }
            }
        }
    }

    /**
     * Checks night mode flags and returns true if night mode is active
     *
     * @param context the context
     *
     * @return true if night mode is active, else false
     */
    fun isNightModeActive(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    /**
     * OxygenOS stores the system theme at `content://settings/system/oem_black_mode`.
     *
     * If the device has an option to change the theme (Settings -> Display -> Theme), this method will return the corresponding [Theme].
     *
     * This is how I've mapped OnePlus themes to [Theme]:
     *
     *  * OnePlusLight(0) and OnePlusColorful(2) are both [Theme.LIGHT] themes
     *  * OnePlusDark is a [Theme.DARK] theme
     *  * (haha ezpz)
     *
     * @param context the context
     *
     * @return 0 for light, 1 for dark, 2 for colorful/default theme
     */
    private fun translateOnePlusTheme(context: Context): Theme? {
        val resolver = context.contentResolver
        try {
            return when (Settings.System.getInt(resolver, OEM_BLACK_MODE)) {
                0 -> {
                    // OnePlusLight is a light theme
                    LIGHT
                }
                1 -> {
                    // OnePlusDark is a dark theme
                    DARK
                }
                2 -> {
                    // OnePlusColorful is a light theme
                    LIGHT
                }
                else -> null
            }
        } catch (e: SettingNotFoundException) {
            // no-op
        }

        // system doesn't support OnePlus themes yet (it was introduced in later OxygenOS versions),
        // so old devices will probably not have this setting
        return null
    }
}