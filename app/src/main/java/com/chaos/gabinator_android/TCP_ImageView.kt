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
        thread {
            var bitmap_data: Bitmap?
            while (permisos) {
                try {
                    var input_stream = socket?.getInputStream()
                    var output_stream = socket?.getOutputStream()
                    val size = input_stream?.available()
                    val buffer = size?.let { ByteArray(it) } // Buffer size
                    var bytesRead: Int = 1
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    // Read the input stream into the byte array output stream
                    if (input_stream != null) {
                        while (true) {
                            bytesRead = input_stream.read(buffer)
                            if (bytesRead == -1 || bytesRead == 0){
                                break
                            }
                            println("$bytesRead/"+input_stream.available())
                            byteArrayOutputStream.write(buffer, 0, bytesRead)
                            if (input_stream.available() == 0){
                                break
                            }
                        }
                    }
                    println("getting image")
                    // Convert the buffered data to a byte array
                    val imageData = byteArrayOutputStream.toByteArray()
                    bitmap_data = BitmapFactory.decodeByteArray(imageData,0,imageData.size)
                    println("H "+ bitmap_data.height + " W " + bitmap_data.width)
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