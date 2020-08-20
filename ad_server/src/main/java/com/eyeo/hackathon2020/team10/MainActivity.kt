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
package com.eyeo.hackathon2020.team10

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.eyeo.hackathon2020.team10.service.AdService
import com.eyeo.hackathon2020.team10.service.IAdService

class MainActivity : AppCompatActivity() {

    private val TAG = "Activity"
    private lateinit var button: Button
    private lateinit var url: TextView

    private var adService: IAdService? = null
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.i(TAG, "Service connected")
            adService = IAdService.Stub.asInterface(service)  as IAdService
        }
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            adService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun initControls() {
        button.setOnClickListener { requestAd() }
    }

    private fun requestAd() {
        url.text = adService?.adUrl
    }

    private fun bindControls() {
        button = findViewById(R.id.MainActivity_button)
        url = findViewById(R.id.MainActivity_url)
    }
}