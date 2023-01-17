package com.vshum.turbogum

import android.app.Application
import com.vshum.turbogum.services.ServicesLocator

class App: Application() {
    lateinit var servicesLocator: ServicesLocator

    override fun onCreate() {
        super.onCreate()
        servicesLocator = ServicesLocator()
    }
}