package com.chaos.gabinator_android

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.OnKeyListener
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

var LOG: String = ""

fun print_log(message: String, type: String) {
    LOG += "$type: $message"
    Log.d(type, message)
}

class Main : AppCompatActivity() {
    private fun USB(accesorio : UsbAccessory?) {
        if (accesorio == null) {
            print_log("Accesorio sin obtener","USB")
            return
        }
        print_log("Accesorio obtenido con exito -> $accesorio, continuando...","USB")
        setContentView(R.layout.usb_mode)
    }

    private fun handle_back_button(){
        onBackPressedDispatcher.addCallback {
            setContentView(R.layout.menu)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        val USB = findViewById<Button>(R.id.USB)
        val TCP = findViewById<Button>(R.id.TCP)
        val LOG = findViewById<Button>(R.id.LOG)

        var accesorio: UsbAccessory? = null

        handle_back_button()

        print_log("Reciver Creandose", "Main/OnCreate")
        val Reciver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
                synchronized(this) {
                    when (intent.action) {
                        "com.android.example.USB_PERMISSION" -> {
                            print_log(
                                "Pidiendo permisos accesorio...",
                                "Main/Reciver/USB_Permission"
                            )
                            if (intent.getBooleanExtra(
                                    UsbManager.EXTRA_PERMISSION_GRANTED,
                                    false
                                )
                            ) {
                                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                    accesorio = intent.getParcelableExtra<UsbAccessory?>(UsbManager.EXTRA_ACCESSORY,
                                        UsbAccessory::class.java
                                    )
                                }
                                else{
                                    accesorio = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY) as UsbAccessory?
                                }
                                print_log("Permisos obtenidos", "Main/Reciver/USB_Permission")
                                USB(accesorio)
                            } else {
                                print_log("Permisos denegados", "Main/Reciver/USB_Permission")
                            }

                        }


                        "android.hardware.usb.action.USB_ACCESSORY_DETACHED" -> {
                            //USB Detached
                            print_log("USB Desconectado", "Main/Reciver/Detached")
                        }

                        "android.hardware.usb.action.USB_ACCESSORY_ATTACHED" -> {
                            print_log("USB Conectando, pidiendo permisos", "Main/Reciver/Attached")

                        }

                    }
                }
            }
        }

        print_log("Reciver creado","Main/OnCreate")
        val manager: UsbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        val permiso_usb = PendingIntent.getBroadcast(
            this, 0, Intent("com.android.example.USB_PERMISSION"),
            PendingIntent.FLAG_IMMUTABLE
        )
        val filter = IntentFilter()
        filter.addAction("com.android.example.USB_PERMISSION")
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
        if (Build.VERSION.SDK_INT > 33) {
            registerReceiver(Reciver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(Reciver, filter)
        }

        //Accion al apretar el boton de USB
        USB.setOnClickListener {
            print_log("Clickeado USB", "Main/OnCreate")
            if (accesorio == null){
                val accessoryList: Array<out UsbAccessory>? = manager.accessoryList
                if (accessoryList == null){
                    print_log("No hay accesorios disponibles","Main/OnCreate")
                    Snackbar.make(
                        findViewById(R.id.menu),
                        "No hay accesorios disponibles",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }
                accesorio = accessoryList[0]
            }
            manager.requestPermission(accesorio, permiso_usb)
        }
        //Accion al apretar el boton de TCP
        TCP.setOnClickListener {
            print_log("Clickeado TCP", "Main/OnCreate")
            println("TCP")
        }
        //Accion al apretar el boton de LOG
        LOG.setOnClickListener {
            print_log("Clickeado LOG", "Main/OnCreate")
            println("LOG")
        }


    }

}