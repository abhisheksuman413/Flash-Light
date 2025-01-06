package com.fps69.flashlight

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fps69.flashlight.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private var isBlinking = false
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private lateinit var handler: Handler
    private var blinkDelay: Long = 500



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        handler = Handler(Looper.getMainLooper())
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager


        try {
            cameraId = cameraManager.cameraIdList.firstOrNull {
                cameraManager.getCameraCharacteristics(it)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }


        // SeekBar Listener
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blinkDelay = progress.toLong()
                binding.seekbarValue.text = "Blink Speed: ${blinkDelay}ms"


                // Update text color with gradient
                updateTextColorWithGradient(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        // Toggle Button Listener
        binding.btnToggle.setOnClickListener {
            if (isBlinking) {
                stopBlinking()
                binding.btnToggle.text = "Start Blinking"
            } else {
                startBlinking()
                binding.btnToggle.text = "Stop Blinking"
            }
        }


    }

    private fun startBlinking() {
        isBlinking = true
        handler.post(object : Runnable {
            override fun run() {
                if (!isBlinking) return
                toggleFlashlight(true)
                handler.postDelayed({
                    toggleFlashlight(false)
                    handler.postDelayed(this, blinkDelay)
                }, blinkDelay)
            }
        })
    }


    private fun stopBlinking() {
        isBlinking = false
        handler.removeCallbacksAndMessages(null)
        toggleFlashlight(false)
    }


    private fun toggleFlashlight(state: Boolean) {
        try {
            cameraManager.setTorchMode(cameraId ?: return, state)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // Function to update text color with a gradient based on SeekBar progress
    private fun updateTextColorWithGradient(progress: Int) {
        val maxProgress = 2000
        val startColor = interpolateColor(progress, 0, maxProgress, "#FF5722", "#FFC107") // From orange to yellow
        val endColor = interpolateColor(progress, 0, maxProgress, "#2196F3", "#4CAF50") // From blue to green

        val colors = intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))

        val shader = LinearGradient(
            0f, 0f, 0f, binding.seekbarValue.height.toFloat(),
            colors, null, Shader.TileMode.CLAMP
        )

        binding.seekbarValue.paint.shader = shader
    }

    // Function to interpolate between two colors based on the progress value
    private fun interpolateColor(progress: Int, minProgress: Int, maxProgress: Int, colorStart: String, colorEnd: String): String {
        val startColor = Color.parseColor(colorStart)
        val endColor = Color.parseColor(colorEnd)

        val red = interpolateValue(progress, minProgress, maxProgress, Color.red(startColor), Color.red(endColor))
        val green = interpolateValue(progress, minProgress, maxProgress, Color.green(startColor), Color.green(endColor))
        val blue = interpolateValue(progress, minProgress, maxProgress, Color.blue(startColor), Color.blue(endColor))

        return String.format("#%02X%02X%02X", red, green, blue)
    }

    // Helper function to interpolate color values
    private fun interpolateValue(progress: Int, minProgress: Int, maxProgress: Int, startValue: Int, endValue: Int): Int {
        val ratio = (progress - minProgress).toFloat() / (maxProgress - minProgress)
        return (startValue + ratio * (endValue - startValue)).toInt()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopBlinking()
    }
}