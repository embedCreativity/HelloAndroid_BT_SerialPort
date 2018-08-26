package com.embedcreativity.bluetoothserial

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.IOException
import java.util.*

class BluetoothService : Service() {

    private val myBinder = MyLocalBinder()

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        var m_connectFinished: Boolean = false
    }

    override fun onBind(intent: Intent): IBinder {
        return myBinder
    }

    inner class MyLocalBinder : Binder() {
        fun getService() : BluetoothService {
            return this@BluetoothService
        }
    }

    // Connect to a bluetooth device given an address
    fun connectToDevice(address: String) {
        m_address = address
        ConnectToDevice(this).execute()
    }

    fun getConnectStatus() : Boolean {
        return m_connectFinished
    }

    fun getConnected() : Boolean {
        return m_isConnected
    }

    fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        m_connectFinished = false
        m_isConnected = false
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            // TODO: remove this override?
            m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        }

        override fun doInBackground(vararg p0: Void?): String? {
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
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true // flag that we were successful
                Log.i("data", "Connected!")
            }

            m_connectFinished = true // flag that we're done
        }
    }



}
