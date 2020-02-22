package com.inyoung.giphy

import android.app.Application
import io.realm.Realm

class GiphyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}