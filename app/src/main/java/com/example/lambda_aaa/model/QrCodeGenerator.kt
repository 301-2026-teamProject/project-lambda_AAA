package com.example.lambda_aaa.model

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object QrCodeGenerator {
    fun generateBitmap(content: String, width: Int = 512, height: Int = 512): Bitmap {
        val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }
    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val data = if (base64Str.contains(",")) base64Str.substringAfter(",") else base64Str
            val decoded = Base64.decode(data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
        } catch (_: Exception) {
            null
        }
    }
    fun save(context: Context, bitmap: Bitmap, campsiteName: String) {
        val filename = "Campsite_${campsiteName.replace(Regex("[^a-zA-Z0-9]"), "_")}_QR.jpg"
        var fos: OutputStream? = null
        var success = false
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                success = true
            }
        } catch (_: Exception) {
            success = false
        }

        val msg = if (success) "Downloaded '$filename' to Downloads folder!" else "Failed to download QR code."
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
    fun upload(campsiteId: String, bitmap: Bitmap, onResult: (Boolean, String?) -> Unit) {
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val base64String = "data:image/jpeg;base64," + Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

            Firebase.firestore.collection("campsites").document(campsiteId)
                .update("imageUrl", base64String)
                .addOnSuccessListener { onResult(true, base64String) }
                .addOnFailureListener { e -> onResult(false, e.message) }
        } catch (e: Exception) {
            onResult(false, e.message)
        }
    }
}
