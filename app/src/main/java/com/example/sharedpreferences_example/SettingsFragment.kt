package com.example.sharedpreferences_example

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import java.lang.NumberFormatException

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    androidx.preference.Preference.OnPreferenceChangeListener {
    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.timer_preferences, s)
        val sharedPreferences: SharedPreferences = preferenceScreen
            .sharedPreferences
        val preferenceScreen: PreferenceScreen? = preferenceScreen
        val count: Int = preferenceScreen!!.preferenceCount
        for (i in 0 until count) {
            val preference: androidx.preference.Preference? = preferenceScreen.getPreference(i)
            if (preference !is CheckBoxPreference) {
               val value: String? = sharedPreferences.getString(
                    preference?.key,
                    ""
                )
                setPreferenceLabel(preference, value)
            }
        }
        val preference: androidx.preference.Preference? = findPreference("default_interval")
        preference?.onPreferenceChangeListener = this
    }

    private fun setPreferenceLabel(preference: androidx.preference.Preference?, value: String?) {
        if (preference is androidx.preference.ListPreference) {
            val listPreference: androidx.preference.ListPreference = preference
            val index: Int = listPreference.findIndexOfValue(value)
            if (index >= 0) {
                listPreference.summary = listPreference.entries[index]
            }
        } else if (preference is EditTextPreference) {
            preference.setSummary(value)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference: androidx.preference.Preference? = findPreference(key)
        if (preference !is CheckBoxPreference) {
            val value: String? = sharedPreferences.getString(preference?.key, "")
            setPreferenceLabel(preference, value)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(
        preference: androidx.preference.Preference?,
        newValue: Any?
    ): Boolean {
        val toast: Toast =
            Toast.makeText(context, "Please enter an integer number", Toast.LENGTH_LONG)
        if (preference?.key.equals("default_interval")) {
            val defaultIntervalString = newValue as String
            try {
                defaultIntervalString.toInt()
            } catch (nef: NumberFormatException) {
                toast.show()
                return false
            }
        }
        return true
    }

}