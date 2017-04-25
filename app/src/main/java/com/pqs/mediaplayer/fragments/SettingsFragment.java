package com.pqs.mediaplayer.fragments;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.utils.Utils;

/**
 * Created by truongpq on 19/04/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String KEY_SOUND_EFFECTS = "pre_sound_effects";
    public static final String KEY_SLEEP_TIMER = "pre_sleep_timer";
    public static final String KEY_PAUSE_ON_DISCONNECT = "pre_pause_on_disconnect";
    public static final String KEY_RESUME_ON_CONNECT = "pre_resume_on_connect";

    public static SettingsFragment newInstance() {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);


        Preference pre_sound_effects = findPreference(KEY_SOUND_EFFECTS);
        pre_sound_effects.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (Utils.hasEffectsPanel(getActivity())) {
                    try {
                        // The google MusicFX apps need to be started using startActivityForResult
                        startActivityForResult(Utils.createEffectsIntent(), 111);
                    } catch (final ActivityNotFoundException notFound) {
                        Toast.makeText(getActivity(), "Equalizer not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Equalizer not found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        Preference pre_sleep_timer = findPreference(KEY_SLEEP_TIMER);
        pre_sleep_timer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utils.showSleepTimerDialog(getActivity());
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
