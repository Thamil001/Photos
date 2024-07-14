package com.example.myapplication.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ImageResolver {

    suspend fun query(context: Context) = withContext(Dispatchers.IO) {
        val imgList = mutableListOf<Image>()
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns,
            null,
            null,
            orderBy
        )
        val count: Int = cursor!!.count

        for (i in 0 until count) {
            cursor.moveToPosition(i)
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val idColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val uri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cursor.getString(idColumnIndex)
            )
            imgList.add(0, Image(cursor.getString(dataColumnIndex), uri))
        }

        cursor.close()
        loge(imgList.size)
        imgList
    }

    suspend fun getThumbnail(context: Context, imageUri: Uri): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.loadThumbnail(imageUri, Size(200, 200), null)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}

data class Image(
    val data: String,
    val uri: Uri,
)

fun getImageUriFromPath(context: Context, imagePath: String): Uri? {
    val imageFile = File(imagePath)
    return try {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}


