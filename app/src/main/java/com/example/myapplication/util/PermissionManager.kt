package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
object PermissionManager {

    private const val PERMISSION_RESULT_CODE = 11

    fun requestStoragePermission(activity: Activity) {
        activity.requestPermissions(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                )
            } else arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_RESULT_CODE
        )
    }

    fun checkStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
}