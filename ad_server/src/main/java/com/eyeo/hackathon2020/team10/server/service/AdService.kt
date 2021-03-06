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
package com.eyeo.hackathon2020.team10.server.service

import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.os.IBinder
import android.util.Log
import com.eyeo.hackathon2020.team10.server.R
import java.util.*
import kotlin.math.abs

class Binder(val resources: Resources) : IAdService.Stub() {
    private val random = Random()

    override fun getAdUrl(): String {
        val url = "[banner_url_${abs(random.nextInt())}]"
        Log.w("AdService", "$url generated")
        return url
    }

    override fun getAdBitmap(): ByteArray {
        return resources.openRawResource(R.raw.ad).readBytes()
    }
}

class AdService : Service() {
    private lateinit var binder: Binder

    override fun onCreate() {
        super.onCreate()
        binder = Binder(resources)
    }
    override fun onBind(intent: Intent?): IBinder? = binder
}