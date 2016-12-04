/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.tempus.R;

import static android.app.Activity.RESULT_OK;

public class NewAlarmItems extends PreferenceFragment {

    int PLACE_PICKER_REQUEST = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_new_alarm);
        setHasOptionsMenu(true);

        bindPreferenceSummaryToValue(findPreference("alarm_name"));
        bindPreferenceSummaryToValue(findPreference("alarm_ringtone"));
        bindPreferenceSummaryToValue(findPreference("alarm_type"));

        Preference myPref = findPreference("event_loc");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            return true;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                NewAlarmActivity.locationPicked = place.getLatLng().toString();
            }
        }
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                if(listPreference.getKey().equals("alarm_type") && stringValue.equals("1")){
                    Preference prefCat = findPreference("prefCatTraffic");
                    prefCat.setEnabled(true);
                }
                else if(listPreference.getKey().equals("alarm_type") && stringValue.equals("0")){
                    Preference prefCat = findPreference("prefCatTraffic");
                    prefCat.setEnabled(false);
                }
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            }
            else {
                if(preference instanceof DialogPreference){
                    DialogPreference dialogPreference = (DialogPreference) preference;
                    Log.e("DialogPreference", dialogPreference.getKey());
                }
                preference.setSummary(stringValue);
            }

            return true;
        }
    };
}