package com.tensorprojects.mirrormoves

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AboutUs : AppCompatActivity() {
    private lateinit var pressAnimation: Animation
    private lateinit var releaseAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.about_us)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backBtn = findViewById<ImageButton>(R.id.backBtn)

        val settings = getSharedPreferences("Settings", MODE_PRIVATE)
        val soundOnSetting = settings.getBoolean("Sound", true)
        val btnSound = MediaPlayer.create(this, R.raw.click)

        setButtonAnimation(backBtn)
        pressAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press)
        releaseAnimation = AnimationUtils.loadAnimation(this, R.anim.button_release)

        val toMain = Intent(this, MainActivity::class.java)

        backBtn.setOnClickListener {
            setButtonAnimation(backBtn)
            if (soundOnSetting) {
                btnSound.start()
            }
            Handler(Looper.getMainLooper()).postDelayed({startActivity(toMain)}, 50)
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
}