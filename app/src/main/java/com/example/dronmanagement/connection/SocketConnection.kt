package com.example.dronmanagement.connection

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class SocketConnection(private val ip: String, private val port: Int) {
    private val logTag = "debug"
    var iMsg = ""

    private fun sendMsg(msg: String) {
        Thread {
            try {
                val soc = Socket(ip, port)
                val output = DataOutputStream(soc.getOutputStream())
                val input = DataInputStream(soc.getInputStream())
                iMsg = input.readUTF().toString()
                Log.i(logTag, "Android: $msg")
                Log.i(logTag, "Raspberry: $iMsg")
                output.writeUTF(msg)
                output.flush()
                output.close()
                soc.close()
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }.start()
    }

    fun connect() {
        sendMsg("Connect")
    }
}