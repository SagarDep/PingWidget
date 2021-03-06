package ve.com.abicelis.pingwidget.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.util.Pair;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.util.Patterns;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.util.Locale;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.preference.ThemePreference;
import ve.com.abicelis.pingwidget.enums.MaxPingsPreferenceType;
import ve.com.abicelis.pingwidget.enums.PingIntervalPreferenceType;
import ve.com.abicelis.pingwidget.enums.WidgetLayoutType;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.Constants;
import ve.com.abicelis.pingwidget.util.RemoteViewsUtil;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;
import ve.com.abicelis.pingwidget.util.Util;


/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetConfigureFragment extends PreferenceFragmentCompat {

    //DATA
    private static final String TAG = PingWidgetConfigureFragment.class.getSimpleName();
    private int mWidgetId;

    //UI
    private EditTextPreference mAddress;
    private ListPreference mInterval;
    private ListPreference mMaxPings;
    private SwitchPreference mShowChartLines;
    private SwitchPreference mUseDarkTheme;
    private ThemePreference mTheme;
    private Preference mAbout;
    private Preference mRate;
    private Preference mContact;



    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        Log.d(TAG, "onCreatePreferencesFix(). Action " + getArguments().getString(Constants.ACTION, "NONE"));

        addPreferencesFromResource(R.xml.ping_widget_configure_preferences);

        mAddress = (EditTextPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_address_key));
        mAddress.setSummary(mAddress.getText());                            //If a value was previously set for a previous widget, keep that for next widget
        mAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mAddress.setSummary((String) newValue);
                return true;
            }
        });


        mInterval = (ListPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_interval_key));
        mInterval.setEntries(PingIntervalPreferenceType.getEntries(getContext()));
        mInterval.setEntryValues(PingIntervalPreferenceType.getEntryValues());

        if(mInterval.getValue() == null || !PingIntervalPreferenceType.isValidEntry(mInterval.getValue())) {
            mInterval.setValue(PingIntervalPreferenceType.INTERVAL_1.getEntryValue());
            mInterval.setSummary(PingIntervalPreferenceType.INTERVAL_1.getEntry(getContext()));
        } else {
            mInterval.setSummary(PingIntervalPreferenceType.valueOf(mInterval.getValue()).getEntry(getContext()));
        }
        mInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mInterval.setSummary(PingIntervalPreferenceType.valueOf((String) newValue).getEntry(getContext()));
                return true;
            }
        });


        mMaxPings = (ListPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_max_pings_key));
        mMaxPings.setEntries(MaxPingsPreferenceType.getEntries(getContext()));
        mMaxPings.setEntryValues(MaxPingsPreferenceType.getEntryValues());

        if(mMaxPings.getValue() == null || !MaxPingsPreferenceType.isValidEntry(mMaxPings.getValue())) {
            mMaxPings.setValue(MaxPingsPreferenceType.MAX_PINGS_15.getEntryValue());
            mMaxPings.setSummary(MaxPingsPreferenceType.MAX_PINGS_15.getEntry(getContext()));
        } else {
            mMaxPings.setSummary(MaxPingsPreferenceType.valueOf(mMaxPings.getValue()).getEntry(getContext()));
        }
        mMaxPings.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mMaxPings.setSummary(MaxPingsPreferenceType.valueOf((String) newValue).getEntry(getContext()));
                return true;
            }
        });


        mShowChartLines = (SwitchPreference) findPreference(getResources().getString(R.string.fragment_widget_show_chart_lines_key));
        mUseDarkTheme = (SwitchPreference) findPreference(getResources().getString(R.string.fragment_widget_dark_theme_key));
        mTheme = (ThemePreference) findPreference(getResources().getString(R.string.fragment_widget_configure_theme_key));
        mAbout = findPreference(getResources().getString(R.string.fragment_widget_configure_about_key));
        mAbout.setSummary(String.format(Locale.getDefault(), getResources().getString(R.string.fragment_widget_configure_about_summary), getAppVersionAndBuild(getActivity()).first));
        mRate = findPreference(getResources().getString(R.string.fragment_widget_configure_rate_key));
        mRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                playStoreIntent.setData(Uri.parse(getResources().getString(R.string.url_market)));
                startActivity(playStoreIntent);
                return true;
            }
        });
        mContact = findPreference(getResources().getString(R.string.fragment_widget_configure_contact_key));
        mContact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",getResources().getString(R.string.address_email), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                return true;
            }
        });

        //Get the arguments (bundle) passed from PingWidgetConfigureActivity, which contains the widgetId
        mWidgetId = getArguments().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);


        //Check if reconfiguring widget, if so, populate preference data
        if(getArguments().getString(Constants.ACTION, "").equals(Constants.ACTION_WIDGET_RECONFIGURE)) {
            Log.d(TAG, "onCreatePreferencesFix(). Setting values from widget ID " + mWidgetId);

            PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(getContext(), mWidgetId);
            Log.d(TAG, "onCreatePreferencesFix(). Widget data:" + data.toString());

            mAddress.setText(data.getAddress());
            mAddress.setSummary(data.getAddress());

            mInterval.setValue(data.getPingInterval().getEntryValue());
            mInterval.setSummary(data.getPingInterval().getEntry(getContext()));

            mMaxPings.setValue(data.getMaxPings().getEntryValue());
            mMaxPings.setSummary(data.getMaxPings().getEntry(getContext()));

            mShowChartLines.setChecked(data.showChartLines());
            mUseDarkTheme.setChecked(data.useDarkTheme());

            mTheme.setTheme(data.getTheme());
            mTheme.setSummary(getResources().getString(data.getTheme().getSummaryRes()));
        }


    }

    private static Pair<String, Integer> getAppVersionAndBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return new Pair<>(pInfo.versionName, pInfo.versionCode);
        } catch (Exception e) {
            return new Pair<>("", 0);
        }
    }

    @SuppressLint("DefaultLocale")
    private static boolean launchWebBrowser(Context context, String url) {
        try {
            url = url.toLowerCase();
            if (!url.startsWith("http://") || !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (null == resolveInfo) {
                Toast.makeText(context, "Could not find a Browser to open link", Toast.LENGTH_SHORT).show();
                return false;
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Could not start web browser", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private boolean checkValues() {
        if(mAddress.getText() == null || !Patterns.WEB_URL.matcher(mAddress.getText()).matches()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_address), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mInterval.getValue() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_interval), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mMaxPings.getValue() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_max_pings), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private PingWidgetData savePingWidgetData(int widgetId, String address, PingIntervalPreferenceType pingInterval, MaxPingsPreferenceType maxPings, boolean showMaxMinAvgLines, boolean useDarkTheme, String themeName) {
        PingWidgetData data = new PingWidgetData(address, pingInterval, maxPings, showMaxMinAvgLines, useDarkTheme, WidgetTheme.valueOf(themeName));
        SharedPreferencesHelper.writePingWidgetData(getContext().getApplicationContext(), widgetId, data);
        return data;
    }




    //Methods called from parent activity
    public void handleWidgetCreation() {

        if(!checkValues()) {
            return;
        }

        Bundle extras = getArguments();
        if (extras != null) {

            //Get AppWidgetManager and RemoteViews
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
            RemoteViews views = RemoteViewsUtil.getRemoteViews(getContext(), WidgetLayoutType.TALL);  //Initially, widget layout is tall

            //Save PingWidgetData in SharedPreferences()
            PingWidgetData data = savePingWidgetData(mWidgetId, mAddress.getText(), PingIntervalPreferenceType.valueOf(mInterval.getValue()), MaxPingsPreferenceType.valueOf(mMaxPings.getValue()), mShowChartLines.isChecked(), mUseDarkTheme.isChecked(), mTheme.getSelectedTheme());


            //Init the widget's views
            RemoteViewsUtil.initWidgetViews(getContext(), views, data);


            //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
            //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
            Util.registerWidgetStartPauseOnClickListener(getActivity(), mWidgetId, views);
            Util.registerWidgetReconfigureClickListener(getActivity(), mWidgetId, views);


            //Finally, update the widget
            appWidgetManager.updateAppWidget(mWidgetId, views);

            //Send widget an intent with RESULT_OK so it know its been configured correctly
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
            getActivity().setResult(Activity.RESULT_OK, resultValue);
            getActivity().finish();

        } else {
            Toast.makeText(getActivity(), "Error! No WidgetID passed to Activity", Toast.LENGTH_SHORT).show();
        }

    }

    public void handleWidgetReconfigure() {

        if(!checkValues()) {
            return;
        }

        Bundle extras = getArguments();
        if (extras != null) {

            //Get PingWidgetData
            PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(getContext(), mWidgetId);

            if(data != null) {

                //Get AppWidgetManager and RemoteViews
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                RemoteViews views = RemoteViewsUtil.getRemoteViews(getContext(), data.getWidgetLayoutType());  //Initially, widget layout is tall


                data.setAddress(mAddress.getText());
                data.setPingInterval(PingIntervalPreferenceType.valueOf(mInterval.getValue()));
                data.setMaxPings(MaxPingsPreferenceType.valueOf(mMaxPings.getValue()));
                data.setShowChartLines(mShowChartLines.isChecked());
                data.setUseDarkTheme(mUseDarkTheme.isChecked());
                data.setTheme(WidgetTheme.valueOf(mTheme.getSelectedTheme()));
                SharedPreferencesHelper.writePingWidgetData(getContext(), mWidgetId, data);

                //Update the widget's views
                RemoteViewsUtil.initWidgetViews(getContext(), views, data);

                //Finally, update the widget
                appWidgetManager.updateAppWidget(mWidgetId, views);

                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Error: No data for widget ID" + mWidgetId, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "Error! No WidgetID passed to handleWidgetReconfigure()", Toast.LENGTH_SHORT).show();
        }

    }
}
