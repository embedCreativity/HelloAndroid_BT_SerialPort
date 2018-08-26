package com.embedcreativity.bluetoothserial

import android.bluetooth.BluetoothSocket
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException

class ControlActivity: AppCompatActivity() {
    private var isBound = false

    companion object {
        var myService: BluetoothService? = null
        var m_bluetoothSocket: BluetoothSocket? = null
        var m_isConnected: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)

        // Bind to the bluetooth service
        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE)

        control_led_on.setOnClickListener{ myService?.sendCommand("On\r\n") }
        control_led_off.setOnClickListener{ myService?.sendCommand("Off\r\n") }
        control_led_disconnnect.setOnClickListener{ disconnect() }
    }

    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothService.MyLocalBinder
            myService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    private fun disconnect() {
        myService?.disconnect()
        unbindService(myConnection)
        finish()
    }
}