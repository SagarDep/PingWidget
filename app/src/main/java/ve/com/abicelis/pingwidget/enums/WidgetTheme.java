package ve.com.abicelis.pingwidget.enums;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import ve.com.abicelis.pingwidget.R;

/**
 * Created by abice on 13/2/2017.
 */

public enum WidgetTheme {
    BLUE_YELLOW(R.id.theme_blue_yellow, R.string.widget_theme_summary_blue_yellow, R.drawable.widget_background_top_blue, R.color.widget_theme_blue_yellow_background_top, R.color.widget_theme_blue_yellow_chart),
    GREEN_GREEN(R.id.theme_green_green, R.string.widget_theme_summary_green_green, R.drawable.widget_background_top_green, R.color.widget_theme_green_background_top, R.color.widget_theme_green_chart),
    ORANGE_AMBER(R.id.theme_orange_amber, R.string.widget_theme_summary_orange_amber, R.drawable.widget_background_top_orange, R.color.widget_theme_orange_amber_background_top, R.color.widget_theme_orange_amber_chart),
    RED_TEAL(R.id.theme_red_teal, R.string.widget_theme_summary_red_teal, R.drawable.widget_background_top_red, R.color.widget_theme_red_teal_background_top, R.color.widget_theme_red_teal_chart),
    TEAL_ORANGE(R.id.theme_teal_orange, R.string.widget_theme_summary_teal_orange, R.drawable.widget_background_top_teal, R.color.widget_theme_teal_orange_background_top, R.color.widget_theme_teal_orange_chart),
    PURPLE_ORANGE(R.id.theme_purple_orange, R.string.widget_theme_summary_purple_orange, R.drawable.widget_background_top_purple, R.color.widget_theme_purple_orange_background_top, R.color.widget_theme_purple_orange_chart)
    ;

    private @IdRes int mThemeViewId;
    private @StringRes int mSummary;
    private @DrawableRes int mDrawableBackgroundContainerTop;
    private @ColorRes int mColorBackgroundContainerTop;
    private @ColorRes int mColorChart;

    WidgetTheme(@IdRes int themeViewId, @StringRes int summary, @DrawableRes int drawableBackgroundContainerTop, @ColorRes int colorBackgroundContainerTop, @ColorRes int colorChart) {
        mThemeViewId = themeViewId;
        mSummary = summary;
        mDrawableBackgroundContainerTop = drawableBackgroundContainerTop;
        mColorBackgroundContainerTop = colorBackgroundContainerTop;
        mColorChart = colorChart;
    }

    public int getSummaryRes() {
        return mSummary;
    }

    public int getThemeViewId() {
        return mThemeViewId;
    }

    public int getDrawableBackgroundContainerTop() {
        return mDrawableBackgroundContainerTop;
    }

    public int getColorBackgroundContainerTop() {
        return mColorBackgroundContainerTop;
    }

    public int getColorChart() {
        return mColorChart;
    }

    public static WidgetTheme getByThemeViewId(@IdRes int themeViewId) {

        for(WidgetTheme widgetTheme : WidgetTheme.values()) {
            if(widgetTheme.getThemeViewId() == themeViewId)
                return widgetTheme;
        }
        throw new IllegalArgumentException(String.valueOf(themeViewId));
    }
}