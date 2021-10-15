package com.example.sharedpreferences_example

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    private var seekBar: SeekBar? = null
    private var textView: TextView? = null
    private var isTimerOn = false
    private var button: Button? = null
    private var countDownTimer: CountDownTimer? = null
    private var defaultInterval = 0
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seekBar = findViewById(R.id.seekBar)
        textView = findViewById(R.id.textView)
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        seekBar?.max = 600
        isTimerOn = false
        setIntervalFromSharedPreferences(sharedPreferences)
        button = findViewById(R.id.button)
        seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val progressInMillis = (progress * 1000).toLong()
                updateTimer(progressInMillis)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    fun start(view: View) {
        if (!isTimerOn) {
            button!!.text = "Stop"
            seekBar!!.isEnabled = false
            isTimerOn = true
            countDownTimer = object : CountDownTimer((seekBar!!.progress * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    updateTimer(millisUntilFinished)
                }

                override fun onFinish() {
                    val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                        applicationContext
                    )
                    if (sharedPreferences.getBoolean("enable_sound", true)) {
                        when (sharedPreferences.getString("timer_melody", "bell")) {
                            "bell" -> {
                                val mediaPlayer: MediaPlayer = MediaPlayer.create(
                                    applicationContext,
                                    R.raw.bell_sound
                                )
                                mediaPlayer.start()
                            }
                            "alarm_siren" -> {
                                val mediaPlayer: MediaPlayer = MediaPlayer.create(
                                    applicationContext,
                                    R.raw.alarm_siren_sound
                                )
                                mediaPlayer.start()
                            }
                            "bip" -> {
                                val mediaPlayer: MediaPlayer = MediaPlayer.create(
                                    applicationContext,
                                    R.raw.bip_sound
                                )
                                mediaPlayer.start()
                            }
                        }
                    }
                    resetTimer()
                }
            }
            countDownTimer?.start()
        } else {
            resetTimer()
        }
    }

    private fun updateTimer(millisUntilFinished: Long) {
        val minutes = millisUntilFinished.toInt() / 1000 / 60
        val seconds = millisUntilFinished.toInt() / 1000 - minutes * 60
        var minutesString: String
        var secondsString: String
        minutesString = if (minutes < 10) {
            "0$minutes"
        } else {
            minutes.toString()
        }
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            seconds.toString()
        }
        textView!!.text = "$minutesString:$secondsString"
    }

    private fun resetTimer() {
        countDownTimer!!.cancel()
        button!!.text = "Start"
        seekBar!!.isEnabled = true
        isTimerOn = false
        setIntervalFromSharedPreferences(sharedPreferences)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.timer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val openSettings = Intent(this, SettingsActivity::class.java)
            startActivity(openSettings)
            return true
        } else if (id == R.id.action_about) {
            val openAbout = Intent(this, AboutActivity::class.java)
            startActivity(openAbout)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setIntervalFromSharedPreferences(sharedPreferences: SharedPreferences?) {
        defaultInterval = Integer.valueOf(sharedPreferences!!.getString("default_interval", "30"))
        val defaultIntervalInMillis = (defaultInterval * 1000).toLong()
        updateTimer(defaultIntervalInMillis)
        seekBar!!.progress = defaultInterval
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "default_interval") {
            setIntervalFromSharedPreferences(sharedPreferences)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }
}

