package id.yukelola.core.designsystem

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

/**
 * Semantic Badge Definitions mapping domain states to visual tokens.
 */
enum class YukelolaBadgeType(
    @ColorRes val textColorRes: Int,
    @ColorRes val backgroundColorRes: Int,
    @DrawableRes val backgroundDrawableRes: Int
) {
    SALES(
        textColorRes = R.color.yukelola_semantic_sales_green_dark,
        backgroundColorRes = R.color.yukelola_semantic_sales_green_light,
        backgroundDrawableRes = R.drawable.bg_badge_sales
    ),
    DIGITAL(
        textColorRes = R.color.yukelola_semantic_digital_blue_dark,
        backgroundColorRes = R.color.yukelola_semantic_digital_blue_light,
        backgroundDrawableRes = R.drawable.bg_badge_digital
    ),
    WARNING(
        textColorRes = R.color.yukelola_semantic_error_red_dark,
        backgroundColorRes = R.color.yukelola_semantic_error_red_light,
        backgroundDrawableRes = R.drawable.bg_badge_warning
    ),
    INVENTORY(
        textColorRes = R.color.yukelola_semantic_inventory_orange_dark,
        backgroundColorRes = R.color.yukelola_semantic_inventory_orange_light,
        backgroundDrawableRes = R.drawable.bg_badge_inventory
    ),
    REPORTS(
        textColorRes = R.color.yukelola_semantic_reports_purple_dark,
        backgroundColorRes = R.color.yukelola_semantic_reports_purple_light,
        backgroundDrawableRes = R.drawable.bg_badge_reports
    )
}
