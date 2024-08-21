package com.chaos.gabinator_android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import kotlin.concurrent.thread

class TCP_ImageView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permisos = true
        setContentView(R.layout.activity_tcp_image_view)
        var input_stream = BufferedInputStream(socket?.getInputStream())
        println(input_stream)
        thread {
            var bitmap_data: Bitmap?
            while (permisos) {
                try {
                    var byo = ByteArrayOutputStream()
                    val by = ByteArray(1024)
                    var BytesRead = by.size
                    while (input_stream.available() != 0) {
                        BytesRead = input_stream.read(by, 0, by.size)
                        byo.write(by, 0, BytesRead)
                    }
                    println(byo.size())
                    bitmap_data = BitmapFactory.decodeByteArray(
                        byo.toByteArray(),
                        0,
                        byo.size()
                    )
                    print_log("Saving data", "USB_Transfer_Manager/")
                    runOnUiThread {
                        val image = findViewById<ImageView>(R.id.Image_tcp)
                        if (bitmap_data == null) {
                            print_log("Reciver Image is null", "USB_Transfer_Manager/RUNONUI/")
                            return@runOnUiThread
                        }
                        if (image == null) {
                            print_log("Image is null", "USB_Transfer_Manager/RUNONUI/")
                            return@runOnUiThread
                        }
                        image.setImageBitmap(bitmap_data)
                    }
                } catch (io: Exception) {
                    io.message?.let { print_log(it, "USB_Transfer_Manager/Exeption") }
                }

            }
        }
    }
}