package com.tensorprojects.mirrormoves

import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btnSound: MediaPlayer
    private lateinit var menuMusic: MediaPlayer
    private var soundOnSetting: Boolean = true
    private lateinit var pressAnimation: Animation
    private lateinit var releaseAnimation: Animation
    private lateinit var pressScaleAnimation: Animation
    private lateinit var releaseScaleAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        menuMusic = MediaPlayer.create(this, R.raw.bgm_menu)

        val normalModeBtn = findViewById<Button>(R.id.startBtn)
        val randomModeBtn = findViewById<Button>(R.id.randomModeBtn)
        val aboutBtn = findViewById<ImageButton>(R.id.aboutBtn)
        val soundToggle = findViewById<ImageButton>(R.id.soundToggle)
        btnSound = MediaPlayer.create(this, R.raw.click)

        setButtonScaleAnimation(normalModeBtn)
        setButtonScaleAnimation(randomModeBtn)
        setButtonAnimation(soundToggle)
        setButtonAnimation(aboutBtn)
        pressAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press)
        releaseAnimation = AnimationUtils.loadAnimation(this, R.anim.button_release)
        pressScaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_press)
        releaseScaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_release)

        val settings = getSharedPreferences("Settings", MODE_PRIVATE)
        soundOnSetting = settings.getBoolean("Sound", true)
        val editor = settings.edit()
        if (soundOnSetting) {
            soundToggle.setBackgroundResource(R.drawable.sound_on_toggle)
        } else {
            soundToggle.setBackgroundResource(R.drawable.sound_off_toggle)
        }

        val toGameField = Intent(this, GameField::class.java)
        val toAboutUs = Intent(this, AboutUs::class.java)

        soundToggle.setOnClickListener {
            setButtonAnimation(soundToggle)
            soundOnSetting = settings.getBoolean("Sound", true)
            if (soundOnSetting) {
                editor.putBoolean("Sound", false)
                editor.apply()
                menuMusic.pause()
                soundToggle.setBackgroundResource(R.drawable.sound_off_toggle)
            } else {
                editor.putBoolean("Sound", true)
                editor.apply()
                soundToggle.setBackgroundResource(R.drawable.sound_on_toggle)
                menuMusic.start()
                btnSound.start()
            }
        }

        normalModeBtn.setOnClickListener {
            setButtonScaleAnimation(normalModeBtn)
            playBtnSound()
            menuMusic.stop()
            toGameField.putExtra("mode", 1)
            Handler(Looper.getMainLooper()).postDelayed({startActivity(toGameField)}, 70)
        }

        randomModeBtn.setOnClickListener {
            setButtonScaleAnimation(randomModeBtn)
            playBtnSound()
            menuMusic.stop()
            toGameField.putExtra("mode", 2)
            Handler(Looper.getMainLooper()).postDelayed({startActivity(toGameField)}, 70)
        }

        aboutBtn.setOnClickListener {
            setButtonAnimation(aboutBtn)
            playBtnSound()
            menuMusic.stop()
            Handler(Looper.getMainLooper()).postDelayed({startActivity(toAboutUs)}, 70)
        }
    }

    override fun onStart() {
        super.onStart()
        if (soundOnSetting) {
            menuMusic.start()
        }
    }

    override fun onPause() {
        super.onPause()
        menuMusic.pause()
    }

    override fun onRestart() {
        super.onRestart()
        menuMusic.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        menuMusic.stop()
    }

    private fun playBtnSound() {
        val settings = getSharedPreferences("Settings", MODE_PRIVATE)
        val onSetting = settings.getBoolean("Sound", true)
        if (onSetting) {
            btnSound.start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonAnimation(button: View) {
        button.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.startAnimation(pressAnimation)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.startAnimation(releaseAnimation)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonScaleAnimation(button: View) {
        button.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.startAnimation(pressScaleAnimation)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.startAnimation(releaseScaleAnimation)
                }
            }
            false
        }
    }
}