package com.chaos.gabinator_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import java.net.Socket
import kotlin.concurrent.thread
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io*
import io.ktor.coroutines.*

var socket: Socket? = null
class Tcp_settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val ip = findViewById<EditText>(R.id.ip)
        val puerto = findViewById<EditText>(R.id.puerto)
        val conectar = findViewById<Button>(R.id.conectar)
        var IP_parsed = arrayOf<String>()
        conectar.setOnClickListener {
            println("CONECTANDO")
            println(ip.text.toString()+":"+puerto.text.toString().toInt())
            for (i in ip.text.split(".", limit = 4))
            {
                if (i.toInt() > 255 || i.toInt() < 0){
                    continue
                }
                IP_parsed = IP_parsed.plus(i)

            }
            if (IP_parsed.size == 0 || IP_parsed.size > 4){

                return@setOnClickListener
            }
		socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().connect(ip.text.toString(),puerto.text.toString().toInt())
            startActivity(Intent(getApplicationContext(), com.chaos.gabinator_android.TCP_ImageView::class.java))
    }

}
