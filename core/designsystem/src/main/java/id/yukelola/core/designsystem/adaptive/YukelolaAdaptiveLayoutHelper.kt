package id.yukelola.core.designsystem.adaptive

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.DimenRes

/**
 * Adaptive layout constraints and geometry helper.
 * Provides unified logic for constraining card widths, form columns, and split-pane layout weights.
 */
object YukelolaAdaptiveLayoutHelper {

    /**
     * Calculates the optimal form column count (1 on phones, 2 on tablets).
     */
    fun getFormColumnCount(context: Context): Int {
        return if (YukelolaWindowSizeHelper.isTablet(context)) 2 else 1
    }

    /**
     * Determines whether POS screen should render as a unified two-pane (Catalog + Live Cart).
     */
    fun shouldShowPosTwoPane(context: Context): Boolean {
        return YukelolaWindowSizeHelper.isTwoPaneSupported(context)
    }

    /**
     * Determines whether Digital Transaction screen should render as two-pane (Input + Live Inquiry/Summary).
     */
    fun shouldShowDigitalTwoPane(context: Context): Boolean {
        return YukelolaWindowSizeHelper.isTwoPaneSupported(context)
    }

    /**
     * Applies maximum readable content width constraint to prevent unreadable ultra-wide text/forms on 12"+ tablets.
     */
    fun applyMaxContentWidth(viewGroup: ViewGroup, maxWidthPx: Int) {
        val layoutParams = viewGroup.layoutParams
        if (layoutParams != null && maxWidthPx > 0) {
            val screenWidth = viewGroup.resources.displayMetrics.widthPixels
            if (screenWidth > maxWidthPx) {
                val horizontalMargin = (screenWidth - maxWidthPx) / 2
                viewGroup.setPadding(horizontalMargin, viewGroup.paddingTop, horizontalMargin, viewGroup.paddingBottom)
            }
        }
    }
}
