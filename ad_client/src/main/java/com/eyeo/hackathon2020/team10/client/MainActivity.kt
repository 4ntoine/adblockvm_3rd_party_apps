/*
 * This file is part of Adblock Plus <https://adblockplus.org/>,
 * Copyright (C) 2006-present eyeo GmbH
 *
 * Adblock Plus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * Adblock Plus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Adblock Plus.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.eyeo.hackathon2020.team10.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.*
import com.eyeo.hackathon2020.team10.server.service.IAdService

class MainActivity : AppCompatActivity() {

    private val TAG = "ClientActivity"
    private lateinit var image: ImageView
    private lateinit var button: Button
    private lateinit var lines: EditText

    private var adService: IAdService? = null
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.i(TAG, "Service connected")
            adService = IAdService.Stub.asInterface(service)  as IAdService
            requestAd()
        }
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            adService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("ClientActivity")
        bindControls()
        initControls()
        connectService()
    }

    override fun onDestroy() {
        disconnectService()
        super.onDestroy()
    }

    private fun disconnectService() {
        unbindService(mConnection)
    }

    private fun connectService() {
        val intent = Intent("AdService")
        intent.setPackage("com.eyeo.hackathon2020.team10.server")
        val message = (if (bindService(intent, mConnection, Context.BIND_AUTO_CREATE))
            "connected" else "failed to connect") + " to ad server (inter-process)"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun initControls() {
        button.setOnClickListener { increment() }
    }

    private fun requestAd() {
        adService?.let {
            showImage(it.adBitmap)
        }
    }

    private var counter = 0

    private fun increment() {
        lines.text.append("\n${++counter}")
    }

    private fun showImage(bitmapBytes: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
        image.setImageDrawable(BitmapDrawable(resources, bitmap))
    }

    private fun bindControls() {
        image = findViewById(R.id.MainActivity_image)
        button = findViewById(R.id.MainActivity_button)
        lines = findViewById(R.id.MainActivity_lines)
    }
}