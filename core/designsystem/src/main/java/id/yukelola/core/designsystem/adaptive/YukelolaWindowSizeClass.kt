package id.yukelola.core.designsystem.adaptive

import android.content.Context
import android.content.res.Configuration

/**
 * Window Width Size Classes conforming to Material 3 Adaptive Guidelines.
 * - COMPACT: Typical phone in portrait (< 600dp)
 * - MEDIUM: Tablet 7"-8" portrait or foldable unfolded (600dp - 839dp)
 * - EXPANDED: Tablet 10"-12"+ or landscape desktop/tablet (>= 840dp)
 */
enum class WindowWidthSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

/**
 * High-level device layout posture classification.
 */
enum class DevicePosture {
    PHONE_PORTRAIT,
    PHONE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE
}

object YukelolaWindowSizeHelper {

    fun getWindowWidthSizeClass(context: Context): WindowWidthSizeClass {
        val widthDp = context.resources.configuration.screenWidthDp
        return when {
            widthDp < 600 -> WindowWidthSizeClass.COMPACT
            widthDp < 840 -> WindowWidthSizeClass.MEDIUM
            else -> WindowWidthSizeClass.EXPANDED
        }
    }

    fun getDevicePosture(context: Context): DevicePosture {
        val config = context.resources.configuration
        val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE
        val isTablet = config.smallestScreenWidthDp >= 600

        return when {
            isTablet && isLandscape -> DevicePosture.TABLET_LANDSCAPE
            isTablet && !isLandscape -> DevicePosture.TABLET_PORTRAIT
            !isTablet && isLandscape -> DevicePosture.PHONE_LANDSCAPE
            else -> DevicePosture.PHONE_PORTRAIT
        }
    }

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.smallestScreenWidthDp >= 600
    }

    fun isTwoPaneSupported(context: Context): Boolean {
        val widthSizeClass = getWindowWidthSizeClass(context)
        val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        return widthSizeClass == WindowWidthSizeClass.EXPANDED || (widthSizeClass == WindowWidthSizeClass.MEDIUM && isLandscape)
    }

    fun getOptimalGridSpanCount(
        context: Context,
        defaultPhoneSpan: Int = 2,
        tabletPortraitSpan: Int = 3,
        tabletLandscapeSpan: Int = 4
    ): Int {
        return when (getDevicePosture(context)) {
            DevicePosture.PHONE_PORTRAIT -> defaultPhoneSpan
            DevicePosture.PHONE_LANDSCAPE -> defaultPhoneSpan + 1
            DevicePosture.TABLET_PORTRAIT -> tabletPortraitSpan
            DevicePosture.TABLET_LANDSCAPE -> tabletLandscapeSpan
        }
    }
}
