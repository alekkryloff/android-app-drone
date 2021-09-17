package com.example.dronmanagement.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dronmanagement.R
import com.example.dronmanagement.connection.SocketConnection
import com.example.dronmanagement.model.SharedPreference
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private lateinit var etIP: EditText
    private lateinit var tvLogs: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnLayout: LinearLayout
    private lateinit var scrollView: ScrollView

    private lateinit var pref: SharedPreference
    private lateinit var conn: SocketConnection
    private var ip = ""
    private val logTag = "debug"
    private var connectionStarted = false
    private var logs = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etIP = findViewById(R.id.etIpPort)
        tvLogs = findViewById(R.id.tvLogs)
        btnConnect = findViewById(R.id.btnConnect)
        btnLayout = findViewById(R.id.btnLayout)
        scrollView = findViewById(R.id.scrollView)

        pref = SharedPreference(this)
        etIP.setText(pref.getIp())

        findViewById<Button>(R.id.btnJoystick).setOnClickListener {
            startActivity(Intent(this, JoystickActivity::class.java))
        }
        findViewById<Button>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        btnConnect.setOnClickListener {
            if (!connectionStarted){
                this.currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                handleIpPort()
            } else { disconnect() }
        }

        runUpdateLogsTextView()
        runAutoDisconnection()
    }

    private fun handleIpPort(){
        if (etIP.text.matches(Regex(getString(R.string.ip_regex)))){
            ip = etIP.text.toString()
            pref.setIp(ip)
            conn = SocketConnection(ip, pref.getPort())
            conn.connect()
            runCheckConnection()
        } else {
            Log.i(logTag, getString(R.string.wrong_ip))
            Toast.makeText(this, getString(R.string.wrong_ip), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun disconnect(){
        needDisconnect = false
        connectionStarted = false
        btnLayout.visibility = View.INVISIBLE
        etIP.visibility = View.VISIBLE
        btnConnect.text = "Connect"
        logs += "-- Disconnected --\n"
        Toast.makeText(this, getString(R.string.connectStop), Toast.LENGTH_SHORT).show()
    }


    private var finish = false
    private var threadSteps = 0
    @SuppressLint("SetTextI18n")
    private fun checkConnection(){
        if (threadSteps < 50) {
            if (!finish) {
                Thread.sleep(10)
                threadSteps += 1
                if (conn.iMsg != "") {
                    finish = true
                    conn.iMsg = ""
                    logs += "-- Connected --\n"
                    runUpdateLogs()

                    Toast.makeText(this, getString(R.string.connectSuccess), Toast.LENGTH_SHORT).show()
                    etIP.visibility = View.GONE
                    btnConnect.text = "Disconnect"
                    btnLayout.visibility = View.VISIBLE
                } else { checkConnection() }
            }
        } else {
            Toast.makeText(this, getString(R.string.connectIssue), Toast.LENGTH_SHORT).show()
        }
    }

    private fun runCheckConnection(){
        finish = false
        threadSteps = 0
        thread { runOnUiThread { checkConnection() } }
    }

    private val timeStep = 10L
    private val checkDelay = 1000L
    @SuppressLint("SetTextI18n")
    private fun updateLogs(){
        if (connectionStarted) {
            Thread.sleep(timeStep)
            if (conn.iMsg != "") {
                threadSteps = 0
                Log.i(logTag, conn.iMsg)
                logs += "${conn.iMsg}\n"
                Thread.sleep(checkDelay - timeStep)
                conn.iMsg = ""
                conn.getLogs()
            }
            threadSteps += 1
            if (threadSteps > 50){
                connectionStarted = false
            } else {
                updateLogs()
            }
        }
    }

    private fun runUpdateLogs(){
        connectionStarted = true
        thread {
            conn.getLogs()
            updateLogs()
        }
    }

    private fun runUpdateLogsTextView(){
        if (tvLogs.text.toString() != logs) {
            tvLogs.text = logs
            scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
        }
        Handler(Looper.getMainLooper()).postDelayed({runUpdateLogsTextView()}, 10)
    }

    private var needDisconnect = false
    private fun runAutoDisconnection(){
        if (!connectionStarted){
            if (needDisconnect){
                disconnect()
            }
        } else {
            needDisconnect = true
        }
        Handler(Looper.getMainLooper()).postDelayed({runAutoDisconnection()}, 10)
    }
}