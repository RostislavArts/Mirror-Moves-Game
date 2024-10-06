package com.tensorprojects.mirrormoves

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameOver : AppCompatActivity() {
    private lateinit var btnSound: MediaPlayer
    private lateinit var pressScaleAnimation: Animation
    private lateinit var releaseScaleAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.game_over)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val bundle: Bundle? = intent.extras
        val intScore = bundle?.getInt("score")
        val highScore = bundle?.getInt("highscore")
        val mode = bundle?.getInt("mode")
        val isNewHighScore = bundle?.getBoolean("newhighscore").toString().toBoolean()
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.text_pulse)

        btnSound = MediaPlayer.create(this, R.raw.click)

        val score = findViewById<TextView>(R.id.score)
        val highscore = findViewById<TextView>(R.id.highscore)
        val restartBtn = findViewById<Button>(R.id.restartBtn)
        val toMenuBtn = findViewById<Button>(R.id.toMenuBtn)
        val newHighScoreTxt = findViewById<TextView>(R.id.newHighScoreTxt)

        setButtonScaleAnimation(restartBtn)
        setButtonScaleAnimation(toMenuBtn)
        pressScaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_press)
        releaseScaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_release)

        score.text = resources.getString(R.string.score, intScore)

        highscore.text = resources.getString(R.string.high_score, highScore)

        if (isNewHighScore) {
            newHighScoreTxt.visibility = TextView.VISIBLE
            newHighScoreTxt.startAnimation(pulseAnimation)
        }

        toMenuBtn.setOnClickListener {
            setButtonScaleAnimation(toMenuBtn)
            playBtnSound()
            val toMenu = Intent(this, MainActivity::class.java)
            Handler(Looper.getMainLooper()).postDelayed({startActivity(toMenu)}, 70)
        }

        restartBtn.setOnClickListener {
            setButtonScaleAnimation(restartBtn)
            playBtnSound()
            val toGameField = Intent(this, GameField::class.java)
            toGameField.putExtra("mode", mode)
            Handler(Looper.getMainLooper()).postDelayed({startActivity(toGameField)}, 70)
        }
    }

    private fun playBtnSound() {
        val settings = getSharedPreferences("Settings", MODE_PRIVATE)
        val onSetting = settings.getBoolean("Sound", true)
        if (onSetting) {
            btnSound.start()
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