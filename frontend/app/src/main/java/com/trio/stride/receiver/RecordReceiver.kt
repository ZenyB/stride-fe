package com.trio.stride.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trio.stride.data.service.RecordService

class RecordReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_TYPE_START = "START_RECORD"
        const val ACTION_TYPE_STOP = "STOP_RECORD"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val intentAction = intent.action
        when (intentAction) {
            ACTION_TYPE_START -> {
                val startIntent = Intent(context, RecordService::class.java).apply {
                    action = RecordService.RESUME_RECORDING
                }
                context.startService(startIntent)
            }

            ACTION_TYPE_STOP -> {
                val startIntent = Intent(context, RecordService::class.java).apply {
                    action = RecordService.PAUSE_RECORDING
                }
                context.startService(startIntent)
            }
        }
    }
}