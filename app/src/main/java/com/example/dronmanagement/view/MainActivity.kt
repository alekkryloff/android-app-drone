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
import com.example.dronmanagement.connection.SocketConnection
import com.example.dronmanagement.model.SharedPreference
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private lateinit var etIP: EditText
    private lateinit var pref: SharedPreference
    private lateinit var conn: SocketConnection
    private var ip = ""
    private val logTag = "debug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etIP = findViewById(R.id.etIpPort)
        pref = SharedPreference(this)
        etIP.setText(pref.getIp())

        findViewById<Button>(R.id.btnJoystick).setOnClickListener {
            startActivity(Intent(this, JoystickActivity::class.java))
        }
        findViewById<Button>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            handleIpPort()
        }
    }

    private fun handleIpPort(){
        if (etIP.text.matches(Regex(getString(R.string.ip_regex)))){
            ip = etIP.text.toString()
            pref.setIp(ip)
            conn = SocketConnection(ip, pref.getPort())
            conn.connect()
            runCheckThread()
        } else {
            Log.i(logTag, getString(R.string.wrong_ip))
            Toast.makeText(this, getString(R.string.wrong_ip), Toast.LENGTH_SHORT).show()
        }
    }


    private var finish = false
    private var threadSteps = 0
    private fun checkResponse(toastSuccess: String, toastIssue: String){
        if (threadSteps < 10) {
            if (!finish) {
                Thread.sleep(50)
                threadSteps += 1
                if (conn.iMsg != "") {
                    initCheckTread()
                    Toast.makeText(this, toastSuccess, Toast.LENGTH_SHORT).show()
                    conn.iMsg = ""
                } else {
                    Toast.makeText(this, toastIssue, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, toastIssue, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCheckTread(){
        finish = false
        threadSteps = 0
    }

    private fun runCheckThread(){
        initCheckTread()
        thread {
            runOnUiThread {
                checkResponse(getString(R.string.connectSuccess), getString(R.string.connectIssue))
            }
        }
    }
}