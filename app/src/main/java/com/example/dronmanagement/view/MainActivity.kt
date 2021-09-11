package com.example.dronmanagement.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dronmanagement.R
import com.example.dronmanagement.model.SharedPreference


class MainActivity : AppCompatActivity() {
    lateinit var etIpPort: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etIpPort = findViewById(R.id.etIpPort)

        findViewById<Button>(R.id.btnJoystick).setOnClickListener {
            startActivity(Intent(this, JoystickActivity::class.java))
        }
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            handleIpPort()
        }
    }

    private fun initProcesses(){
        val pref = SharedPreference(this)
        pref.setMode(200)
        Log.i("debug", pref.getMode().toString())
    }

    private fun handleIpPort(){
        if (etIpPort.text.matches(Regex(getString(R.string.ip_port_regex)))){
            connectRaspberry()
        } else {
            Log.i("debug", getString(R.string.wrong_ip))
            Toast.makeText(this, getString(R.string.wrong_ip), Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectRaspberry(){
        Log.i("debug", getString(R.string.connecting) + " " + etIpPort.text)
        Toast.makeText(this, getString(R.string.connecting), Toast.LENGTH_SHORT).show()
    }
}