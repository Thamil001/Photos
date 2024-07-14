package com.example.myapplication.util

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

infix fun Any.loge(message: Any?) {
    val trace = Thread.currentThread().stackTrace[3]

    Log.e(
        if (this is String) this else this::class.java.simpleName,
        "$message (${trace.fileName}:${trace.lineNumber})"
    )
}
