package com.example.delta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // You can use a notification or a sound to alert the user here.
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show()
        // You can also start a service or activity here to handle the alarm.
    }
}
