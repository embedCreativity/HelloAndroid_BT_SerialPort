package com.embedcreativity.bluetoothtutorial

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException

class ControlActivity: AppCompatActivity() {

    companion object {
        var m_bluetoothSocket: BluetoothSocket? = null
        var m_isConnected: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)

        control_led_on.setOnClickListener{ sendCommand("On\r\n") }
        control_led_off.setOnClickListener{ sendCommand("Off\r\n") }
        control_led_disconnnect.setOnClickListener{ disconnect() }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }
}