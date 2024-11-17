package com.chaos.gabinator_android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.netty.buffer.ByteBuf
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import kotlin.concurrent.thread

class TCP_ImageView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permisos = true
        setContentView(R.layout.activity_tcp_image_view)
        thread{
            val input = socket!!.getInputStream()

            while (permisos){
                val usize_bytes = input.read()
                var packet_size: Long = 0
            if (usize_bytes == 64){
                var bytes_size = ByteArray(8)
                for (i in bytes_size.indices){
                    val temp = input.read()
                    if (temp == -1){
                        return@thread
                    }
                    bytes_size[i] = temp.toByte()
                    print_log(bytes_size[i].toString() +"/"+temp.toString(),"xd")
                }
                packet_size = ByteBuffer.wrap(bytes_size).getLong()
                print_log(packet_size.toString(),"DEBYTG")
            }
            var bytes_bitmap = ByteArray(packet_size.toInt())
            for(i in 0..<packet_size){
                val temp = input.read()
                if (temp == -1){
                    return@thread
                }
                bytes_bitmap[i.toInt()] = temp.toByte()
            }
            print_log(bytes_bitmap.size.toString(),"DEBug")
            val bitmap = BitmapFactory.decodeByteArray(bytes_bitmap,0, packet_size.toInt())
            runOnUiThread {
                val img = findViewById<ImageView>(R.id.Image_tcp)
                if (bitmap != null){
                    img.setImageBitmap(bitmap)
                }
            }
        }
            }
    }
}
