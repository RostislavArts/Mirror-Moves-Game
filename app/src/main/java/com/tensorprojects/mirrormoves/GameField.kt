package com.tensorprojects.mirrormoves

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameField : AppCompatActivity() {
    private var mode: Int = 0
    private var btnSound = MediaPlayer()
    private var errorSound = MediaPlayer()
    private var successSound = MediaPlayer()
    private var bgm = MediaPlayer()
    private val prevMoves = mutableListOf<ImageButton>()
    private var playerMove = false
    private var currentIndexCheck = 0
    private var score = 0
    private var savedScores = "HighScore"

    private lateinit var b1: ImageButton
    private lateinit var b2: ImageButton
    private lateinit var b3: ImageButton
    private lateinit var b4: ImageButton
    private lateinit var textOfCurrentScore: TextView
    private lateinit var turnOverlay: TextView
    private lateinit var turnOverlayBackground: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.game_field)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val bundle: Bundle? = intent.extras
        mode = bundle?.getInt("mode").toString().toInt()

        b1 = findViewById(R.id.upBtn)
        b2 = findViewById(R.id.rightBtn)
        b3 = findViewById(R.id.downBtn)
        b4 = findViewById(R.id.leftBtn)

        btnSound = MediaPlayer.create(this, R.raw.click)
        errorSound = MediaPlayer.create(this, R.raw.error)
        successSound = MediaPlayer.create(this, R.raw.success)

        if (mode == 1) {
            bgm = MediaPlayer.create(this, R.raw.bgm)
        } else if (mode == 2){
            bgm = MediaPlayer.create(this, R.raw.bgm_random)
        }
        val settings = getSharedPreferences("Settings", MODE_PRIVATE)
        val musicOnSetting = settings.getBoolean("Sound", true)
        if (musicOnSetting) {
            bgm.start()
            bgm.isLooping = true
        }

        val textOfHighScore = findViewById<TextView>(R.id.highScoreText)
        textOfCurrentScore = findViewById(R.id.currentScoreText)
        textOfCurrentScore.text = resources.getString(R.string.score, 0)

        if (mode == 2) {
            savedScores = "RandomHighScore"
            val mode = findViewById<TextView>(R.id.mode)
            mode.text = resources.getString(R.string.random_mode)
        }
        val highScores = getSharedPreferences(savedScores, MODE_PRIVATE)
        val highestScore = highScores.getInt(savedScores, 0)
        textOfHighScore.text = resources.getString(R.string.high_score, highestScore)

        turnOverlay = findViewById<TextView>(R.id.turnText)
        turnOverlayBackground = findViewById<ImageView>(R.id.turnTextBackground)

        showTurnOverlay(R.string.comp_turn)
        Handler(Looper.getMainLooper()).postDelayed({selectMoves()}, 2300)
    }

    override fun onPause() {
        super.onPause()
        bgm.pause()
    }

    override fun onRestart() {
        super.onRestart()
        bgm.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        bgm.stop()
    }

    private fun selectMoves() {
        val listOfButtons = listOf(b1, b2, b3, b4)

        if (mode == 1) {
            val selection = listOfButtons.random()
            prevMoves.add(selection)
            for ((n, i) in prevMoves.withIndex()) {
                Handler(Looper.getMainLooper()).postDelayed({ lightUp(i) }, n * 1200L)
            }
        } else if (mode == 2) {
            var n = 0
            while (n <= score) {
                val selection = listOfButtons.random()
                prevMoves.add(selection)
                Handler(Looper.getMainLooper()).postDelayed({lightUp(selection)},  n * 1200L)
                n++
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({showTurnOverlay(R.string.player_turn)}, prevMoves.size * 1200L)
        Handler(Looper.getMainLooper()).postDelayed({playerMove = true}, (prevMoves.size * 1200L) + 500L)
    }

    private fun lightUp(btn: ImageButton) {
        when (btn) {
            b1 -> {
                btn.setBackgroundResource(R.drawable.up_pressed)
                Handler(Looper.getMainLooper()).postDelayed(
                    { btn.setBackgroundResource(R.drawable.up) },
                    1000)
            }
            b2 -> {
                btn.setBackgroundResource(R.drawable.right_pressed)
                Handler(Looper.getMainLooper()).postDelayed(
                    { btn.setBackgroundResource(R.drawable.right) },
                    1000)
            }
            b3 -> {
                btn.setBackgroundResource(R.drawable.down_pressed)
                Handler(Looper.getMainLooper()).postDelayed(
                    { btn.setBackgroundResource(R.drawable.down) },
                    1000)
            }
            b4 -> {
                btn.setBackgroundResource(R.drawable.left_pressed)
                Handler(Looper.getMainLooper()).postDelayed(
                    { btn.setBackgroundResource(R.drawable.left) },
                    1000)
            }
        }
    }

    fun buttonClicked(view: View) {
        val clickedButton = view as ImageButton
        val btnID = view.id
        val settings = getSharedPreferences("Settings", MODE_PRIVATE)
        val soundOnSetting = settings.getBoolean("Sound", true)

        if (playerMove) {
            if (soundOnSetting) {
                btnSound.start()
            }

            when (findViewById<ImageButton>(btnID)) {
                b1 -> {
                    view.setBackgroundResource(R.drawable.up_pressed)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { view.setBackgroundResource(R.drawable.up) },
                        100)
                }
                b2 -> {
                    view.setBackgroundResource(R.drawable.right_pressed)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { view.setBackgroundResource(R.drawable.right) },
                        100)
                }
                b3 -> {
                    view.setBackgroundResource(R.drawable.down_pressed)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { view.setBackgroundResource(R.drawable.down) },
                        100)
                }
                b4 -> {
                    view.setBackgroundResource(R.drawable.left_pressed)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { view.setBackgroundResource(R.drawable.left) },
                        100)
                }
            }

            if (clickedButton == prevMoves[currentIndexCheck]) {
                currentIndexCheck++
                if (currentIndexCheck == prevMoves.size){
                    score++
                    textOfCurrentScore.text = resources.getString(R.string.score, score)
                    currentIndexCheck = 0
                    playerMove = false
                    if (mode == 2) {
                        prevMoves.clear()
                    }

                    if (soundOnSetting) {
                        Handler(Looper.getMainLooper()).postDelayed({successSound.start()}, 200)
                    }
                    Handler(Looper.getMainLooper()).postDelayed({showTurnOverlay(R.string.comp_turn)}, 800)
                    Handler(Looper.getMainLooper()).postDelayed({selectMoves()}, 3100)
                }
            } else {
                playerMove = false

                if (soundOnSetting) {
                    errorSound.start()
                }

                if (mode == 2) {
                    savedScores = "RandomHighScore"
                }

                val highScores = getSharedPreferences(savedScores, MODE_PRIVATE)
                var highestScore = highScores.getInt(savedScores, 0)

                val toGameOver = Intent(this, GameOver::class.java)
                toGameOver.putExtra("score", score)

                if (score > highestScore) {
                    val editor = highScores.edit()
                    editor.putInt(savedScores, score)
                    editor.apply()
                    toGameOver.putExtra("newhighscore", true)
                }

                highestScore = highScores.getInt(savedScores, 0)
                toGameOver.putExtra("highscore", highestScore)
                toGameOver.putExtra("mode", mode)
                Handler(Looper.getMainLooper()).postDelayed({startActivity(toGameOver)}, 500)
            }
        }
    }

    private fun showTurnOverlay(message: Int) {
        turnOverlay.text = getString(message)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        turnOverlayBackground.visibility = View.VISIBLE
        turnOverlayBackground.startAnimation(fadeIn)
        turnOverlay.visibility = View.VISIBLE
        turnOverlay.startAnimation(fadeIn)

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                turnOverlayBackground.startAnimation(fadeOut)
                turnOverlay.startAnimation(fadeOut)
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                turnOverlayBackground.visibility = View.GONE
                turnOverlay.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}