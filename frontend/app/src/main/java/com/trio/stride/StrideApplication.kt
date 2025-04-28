package com.trio.stride

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.trio.stride.data.service.GpsService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StrideApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupServiceLifecycle()
    }

    private fun setupServiceLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            private var startedActivities = 0

            override fun onActivityStarted(activity: Activity) {
                if (startedActivities == 0) {

                    startService(Intent(this@StrideApplication, GpsService::class.java))
                }
                startedActivities++
            }

            override fun onActivityStopped(activity: Activity) {
                startedActivities--
                if (startedActivities == 0) {
                    // Last activity stopped - app is background
                    // Service continues running unless system kills it
                }
            }

            // Other overrides can be empty
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}

