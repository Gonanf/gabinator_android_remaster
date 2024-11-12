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
            var byo = ByteArrayOutputStream()
            while (permisos) {
                try {
                    var input_stream = socket?.getInputStream()
                    var output_stream = socket?.getOutputStream()
                    val by = ByteArray(1024)
                    var BytesRead = by.size
                    var pos_term = 0
                    if (input_stream != null) {
                        do{
                            BytesRead = input_stream.read(by, 0, by.size)
                            pos_term = by.indexOf('\n'.code.toByte())
                            if (pos_term != -1){
                                byo.write(by.take(pos_term-1).toByteArray(), 0, pos_term-1)
                                println(byo.size())
                                break
                            }
                            else{
                                byo.write(by, 0, BytesRead)
                            }
                        }while( (by.last().toInt().toChar() != '\n') )
                    }
                    print_log("ADATA: "+byo.size(),"DEBUG")
                    bitmap_data = BitmapFactory.decodeByteArray(
                        byo.toByteArray(),
                        0,
                        byo.size()
                    )
                    byo = ByteArrayOutputStream()
                    if (pos_term != -1){
                        byo.write(by.takeLast(by.size - pos_term-1).toByteArray(),0,by.size - pos_term-1)
                    }
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