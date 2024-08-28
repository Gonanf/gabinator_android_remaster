package com.chaos.gabinator_android

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread


class ImageView : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp_image_view)
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        thread {
            var bitmap_data: Bitmap?
            while (permisos) {
                try {
                    var byo = ByteArrayOutputStream()
                    val by = ByteArray(16384)
                    var BytesRead = by.size
                    while (BytesRead != -1 && BytesRead == by.size) {
                        BytesRead = input_stream?.read(by, 0, by.size)!!
                        byo.write(by, 0, BytesRead)
                    }
                    bitmap_data = BitmapFactory.decodeByteArray(
                        byo.toByteArray(),
                        0,
                        byo.size()
                    )
                    print_log("Saving data","USB_Transfer_Manager/")
                        runOnUiThread { val image = findViewById<ImageView>(R.id.Image_tcp)
                            if (bitmap_data == null){
                                print_log("Reciver Image is null","USB_Transfer_Manager/RUNONUI/")
                                return@runOnUiThread
                            }
                            if (image == null) {
                                print_log("Image is null","USB_Transfer_Manager/RUNONUI/")
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