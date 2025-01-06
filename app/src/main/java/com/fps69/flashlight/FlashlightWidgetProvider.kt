package com.fps69.flashlight

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FlashlightWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE = "com.fps69.flashlight.ACTION_TOGGLE"
        const val ACTION_INCREASE_SPEED = "com.fps69.flashlight.ACTION_INCREASE_SPEED"
        const val ACTION_DECREASE_SPEED = "com.fps69.flashlight.ACTION_DECREASE_SPEED"

        private var isFlashlightOn = false
        private var isBlinking = false
        private var blinkSpeed = 500 // Default blink speed in milliseconds
        private var blinkHandler: Handler? = null
        private var blinkRunnable: Runnable? = null

    }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_flashlight)

            // Intent for Toggle Button
            val toggleIntent = Intent(context, FlashlightWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_toggle_button, togglePendingIntent)


            // Increase Speed Button
            val increaseIntent = Intent(context, FlashlightWidgetProvider::class.java).apply {
                action = ACTION_INCREASE_SPEED
            }
            val increasePendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                increaseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_increase_speed, increasePendingIntent)

            // Decrease Speed Button
            val decreaseIntent = Intent(context, FlashlightWidgetProvider::class.java).apply {
                action = ACTION_DECREASE_SPEED
            }
            val decreasePendingIntent = PendingIntent.getBroadcast(
                context,
                2,
                decreaseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_decrease_speed, decreasePendingIntent)


            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TOGGLE -> toggleFlashlight(context)
            ACTION_INCREASE_SPEED -> adjustBlinkSpeed(context, -100)
            ACTION_DECREASE_SPEED -> adjustBlinkSpeed(context, 100)
        }

    }


    private fun toggleFlashlight(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }

            if (cameraId != null) {
                if (isBlinking) {
                    stopBlinking(cameraManager, cameraId)
                    Toast.makeText(context, "Blinking stopped", Toast.LENGTH_SHORT).show()
                } else if (isFlashlightOn) {
                    turnOffFlashlight(cameraManager, cameraId)
                    Toast.makeText(context, "Flashlight OFF", Toast.LENGTH_SHORT).show()
                } else {
                    startBlinking(cameraManager, cameraId)
                    Toast.makeText(context, "Blinking started", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No flashlight available on this device", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CameraAccessException) {
            Toast.makeText(context, "Error toggling flashlight: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startBlinking(cameraManager: CameraManager, cameraId: String) {
        isBlinking = true
        blinkHandler = Handler(Looper.getMainLooper())
        blinkRunnable = object : Runnable {
            override fun run() {
                try {
                    isFlashlightOn = !isFlashlightOn
                    cameraManager.setTorchMode(cameraId, isFlashlightOn)
                    blinkHandler?.postDelayed(this, blinkSpeed.toLong())
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
        blinkHandler?.post(blinkRunnable!!)
    }

    private fun stopBlinking(cameraManager: CameraManager, cameraId: String) {
        isBlinking = false
        blinkHandler?.removeCallbacks(blinkRunnable!!)
        isFlashlightOn = false
        try {
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun turnOffFlashlight(cameraManager: CameraManager, cameraId: String) {
        isFlashlightOn = false
        try {
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun adjustBlinkSpeed(context: Context, adjustment: Int) {
        blinkSpeed = (blinkSpeed + adjustment).coerceIn(100, 1000) // Limit between 100ms and 1000ms
        Toast.makeText(context, "Blink speed set to ${blinkSpeed}ms", Toast.LENGTH_SHORT).show()
    }







}