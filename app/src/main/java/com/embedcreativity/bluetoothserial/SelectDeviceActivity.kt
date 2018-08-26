package com.embedcreativity.bluetoothserial

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*

class SelectDeviceActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private var isBound = false

    companion object {
        var myService: BluetoothService? = null
        // Try getting a UUID from: https://www.uuidgenerator.net/version4
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

        // Make sure bluetooth is enabled before loading the rest of the UI
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter == null ) {
            toast("this device doesn't support bluetooth")
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        // Bind to the bluetooth service
        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE)

        // Set up UI onClickListener for the SelectDeviceActivity::Refresh button
        select_device_refresh.setOnClickListener{ pairedDeviceList() }
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

    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()
        val nameList : ArrayList<String> = ArrayList()


        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                nameList.add(device.name)
                Log.i("device", ""+device)
                Log.i("-->name: ", ""+device.name)
            }
        } else {
            toast("no paired bluetooth devices found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val device: BluetoothDevice = list[position]
                m_address = device.address
                // Tell service to go connect to device
                myService?.connectToDevice(m_address)
                // Start AsyncTask to wait for service to complete
                WaitForConnect(this).execute()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    toast( "Bluetooth has been enabled")
                } else {
                    toast("Bluetooth has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluettoh enabling has been canceled")
            }
        }
    }

    private class WaitForConnect(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Bluetooth", "Connecting...")
        }

        override fun doInBackground(vararg p0: Void?): String? {

            while ( true ) {
                if (false == myService?.getConnectStatus()) {
                    Thread.sleep(250)
                } else {
                    return null
                }
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            m_progress.dismiss()

            if ( false == myService?.getConnected()) {
                Log.i("data", "couldn't connect")
                Toast.makeText(this.context, "Failed to connect!", Toast.LENGTH_LONG).show()
            } else {
                m_isConnected = true
                Log.i("data", "Connected!")
                Toast.makeText(this.context, "Connected!", Toast.LENGTH_LONG).show()
                context.startActivity(Intent(context, ControlActivity::class.java))
            }
        }
    }
}
