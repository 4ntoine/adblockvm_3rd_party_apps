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
package com.eyeo.hackathon2020.team10.server

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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.eyeo.hackathon2020.team10.server.service.AdService
import com.eyeo.hackathon2020.team10.server.service.IAdService

class MainActivity : AppCompatActivity() {
    private val TAG = "ServerActivity"
    private lateinit var button: Button
    private lateinit var url: TextView
    private lateinit var image: ImageView

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
        setTitle("ServerActivity")
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
        val intent = Intent(this, AdService::class.java)
        intent.action = IAdService::class.java.name
        val message = (if (bindService(intent, mConnection, Context.BIND_AUTO_CREATE))
            "connected" else "failed to connect") + " to ad server (same process)"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun initControls() {
        button.setOnClickListener { requestAd() }
    }

    private fun requestAd() {
        adService?.let {
            url.text = it.adUrl
            showImage(it.adBitmap)
        }
    }

    private fun showImage(bitmapBytes: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
        image.setImageDrawable(BitmapDrawable(resources, bitmap))
    }

    private fun bindControls() {
        button = findViewById(R.id.MainActivity_button)
        url = findViewById(R.id.MainActivity_url)
        image = findViewById(R.id.MainActivity_image)
    }
}