package com.chaos.gabinator_android

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileInputStream
import kotlin.concurrent.thread
import kotlin.system.exitProcess

var LOG1: String = ""
var show_log = true

fun print_log(message: String, type: String) {
    if (show_log == false){
        return
    }
    LOG1 += "$type: $message\n"
    Log.d(type, message)
}
var input_stream: FileInputStream? = null
var parcel_file : ParcelFileDescriptor? = null
var file_descriptor: FileDescriptor? = null
var usb_manager: UsbManager? = null
var permisos = false


class Main : AppCompatActivity() {
    fun print_alert(message: String) {
        Snackbar.make(
            findViewById(R.id.menu),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }


    var accesorio: UsbAccessory? = null
    var abierto = false
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
                            permisos = true
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                accesorio = intent.getParcelableExtra<UsbAccessory?>(
                                    UsbManager.EXTRA_ACCESSORY,
                                    UsbAccessory::class.java
                                )
                            } else {
                                accesorio =
                                    intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY) as UsbAccessory?
                            }
                            print_log("Permisos obtenidos", "Main/Reciver/USB_Permission")
                            print_alert("Permisos obtenidos")

                            try{parcel_file = usb_manager?.openAccessory(accesorio)}
                            catch(e: Exception){print_alert("No se pudo abrir " + e.message)}

                            if (parcel_file == null){
                                print_log("Cannot open Accesory","USB_Transfer_Manager")
                                print_alert("Cannot open Accesory")
                                return
                            }
                            file_descriptor = parcel_file!!.fileDescriptor
                            print_log("Accesory opened correctly","USB_Transfer_Manager")
                            input_stream = FileInputStream(file_descriptor)
                            var USB_ACT = Intent(getApplicationContext(), com.chaos.gabinator_android.ImageView::class.java)
                            startActivity(USB_ACT)
                        } else {
                            print_alert("Permisos denegados")
                            print_log("Permisos denegados", "Main/Reciver/USB_Permission")
                        }

                    }


                    "android.hardware.usb.action.USB_ACCESSORY_DETACHED" -> {
                        //USB Detached
                        print_log("USB Desconectado", "Main/Reciver/Detached")
                        print_alert("USB Desconectado")
                        permisos = false
                        accesorio = null
                    }

                    "android.hardware.usb.action.USB_ACCESSORY_ATTACHED" -> {
                        print_log(
                            "USB Conectando, pidiendo permisos",
                            "Main/Reciver/Attached"
                        )
                        print_alert("USB Conectando")
                    }

                }
            }
        }
    }


    private fun handle_back_button() {
        onBackPressedDispatcher.addCallback {
            exitProcess(0)
        }
    }

    private fun set_listeners(){
        val USB = findViewById<Button>(R.id.USB)
        val TCP = findViewById<Button>(R.id.TCP)
        val LOG = findViewById<Button>(R.id.LOG)


        val permiso_usb = PendingIntent.getBroadcast(
            this, 0, Intent("com.android.example.USB_PERMISSION"),
            PendingIntent.FLAG_MUTABLE
        )

        //Accion al apretar el boton de USB

        USB.setOnClickListener {
            if (permisos) {
                //USB(accesorio)
                var USB_ACT = Intent(getApplicationContext(), com.chaos.gabinator_android.ImageView::class.java)
                startActivity(USB_ACT)
            }
            print_log("Clickeado USB", "Main/OnCreate")
            if (accesorio == null) {
                val accessoryList: Array<out UsbAccessory>? = usb_manager?.accessoryList
                if (accessoryList == null) {
                    print_log("No hay accesorios disponibles", "Main/OnCreate")
                    print_alert("No hay accesorios disponibles")
                    return@setOnClickListener
                }
                accesorio = accessoryList[0]
            }
            usb_manager?.requestPermission(accesorio, permiso_usb)
        }
        //Accion al apretar el boton de TCP
        TCP.setOnClickListener {
            print_log("Clickeado TCP", "Main/OnCreate")
            println("TCP")
            startActivity(Intent(getApplicationContext(), com.chaos.gabinator_android.Tcp_settings::class.java))
        }
        //Accion al apretar el boton de LOG
        LOG.setOnClickListener {
            print_log("Clickeado LOG", "Main/OnCreate")
            println("LOG")
            startActivity(Intent(getApplicationContext(), com.chaos.gabinator_android.Log_View::class.java))

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        usb_manager = getSystemService(Context.USB_SERVICE) as UsbManager
        @SuppressLint("RestrictedApi")
        var pm = getActivity(this)?.packageManager
        var isUSBAccessory = pm?.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY)

        if (!isUSBAccessory!!){
            print_alert("This device is not compatible with the USB Accesory mode")
        }else {
            print_alert("This device is compatible with the USB Accesory mode")
        }

        handle_back_button()

        print_log("Reciver Creandose", "Main/OnCreate")
        val filter = IntentFilter()
        filter.addAction("com.android.example.USB_PERMISSION")
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
        ContextCompat.registerReceiver(this,Reciver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)

        print_log("Reciver creado", "Main/OnCreate")


        set_listeners()







    }

}