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
import io.ktor.selectors.*
import io.ktor.sockets.*
import io.ktor.utils.io.*
import io.ktor.coroutines.*
import kotlinx.coroutines.*

class TCP_ImageView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permisos = true
        setContentView(R.layout.activity_tcp_image_view)
	val read_channel = socket.openReadChannel()
	launch(Dispatchers.IO){
		print_log(read_channel.readUTF8Line(),"DEBUG")

	}
}
}
