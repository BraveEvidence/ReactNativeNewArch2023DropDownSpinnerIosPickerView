package com.rtnmynotification

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.rtnmynotification.NativeMyNotificationSpec


class MyNotificationModule(val context: ReactApplicationContext): NativeMyNotificationSpec(context) {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.POST_NOTIFICATIONS).toTypedArray()

    override fun getName(): String {
        return NAME
    }

    override fun showNotification(title: String?, promise: Promise?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context,context.packageName).setContentText("Some text")
            .setContentTitle(title).setSmallIcon(R.mipmap.ic_launcher).build()
        notificationManager.notify(1,notification)


    }

    override fun getPermissionForAndroid(promise: Promise?) {
        ActivityCompat.requestPermissions(
            context?.currentActivity!!,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
        promise!!.resolve(true)
    }

    override fun checkIfAndroidTOrAbove(promise: Promise?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            promise!!.resolve(true)
        } else {
            promise!!.resolve(false)
        }
    }




    companion object {
        const val NAME = "RTNMyNotification"
    }


}