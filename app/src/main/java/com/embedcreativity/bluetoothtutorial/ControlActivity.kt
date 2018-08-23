package com.embedcreativity.bluetoothtutorial

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.control_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*


class ControlActivity: AppCompatActivity() {

    companion object {
        // UUID from: https://www.uuidgenerator.net/version4
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        //var m_myUUID: UUID = UUID.fromString("3aba5d64-afc7-425d-9ebc-7afe76cc790d")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var connectSuccess: Boolean = true

        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)

        control_led_on.setOnClickListener{ sendCommand("On\r\n") }
        control_led_off.setOnClickListener{ sendCommand("Off\r\n") }
        control_led_disconnnect.setOnClickListener{ disconnect() }

        toast("Connecting ... Please wait")

        try {
            if ( m_bluetoothSocket == null || !m_isConnected) {
                m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                m_bluetoothSocket!!.connect()
            }
        } catch (e: IOException) {
            connectSuccess = false
            e.printStackTrace()
        }

        if (!connectSuccess) {
            Log.i("data", "couldn't connect")
            toast("Failed to connect!")
            finish()
        } else {
            m_isConnected = true
        }
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