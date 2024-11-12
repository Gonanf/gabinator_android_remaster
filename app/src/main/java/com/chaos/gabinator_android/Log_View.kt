package com.chaos.gabinator_android

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.chaos.gabinator_android.databinding.ActivityLogViewBinding

class Log_View : AppCompatActivity() {

    private lateinit var binding: ActivityLogViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_view)
        findViewById<TextView>(R.id.log_text).text = LOG1
    }
}