package com.example.dronmanagement.connection

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import kotlin.concurrent.thread

class SocketConnection(private val ip: String, private val port: Int) {
    private val logTag = "debug"
    var iMsg = ""

    private fun sendMsg(msg: String) {
        Thread {
            try {
                val socket = Socket(ip, port)
                //Log.i(logTag, "$socket")
                val output = DataOutputStream(socket.getOutputStream())
                val input = DataInputStream(socket.getInputStream())
                iMsg = input.readUTF().toString()
                //Log.i(logTag, "A: $msg")
                //Log.i(logTag, "R: $iMsg")
                output.writeUTF(msg)
                output.flush()
                output.close()
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }.start()
    }

    fun connect() { sendMsg("Connect") }
    fun getLogs() { sendMsg("Logs") }

    private var finish = false
    private var threadSteps = 0
    private fun handleResponse(){
        if (threadSteps < 50) {
            if (!finish) {
                Thread.sleep(10)
                threadSteps += 1
                if (iMsg != "") {
                    finish = true
                    Log.i(logTag, "R: $iMsg")
                    iMsg = ""
                } else {
                    handleResponse()
                }
            }
        }
    }

    fun runHandleResponse(){
        finish = false
        threadSteps = 0
        handleResponse()
    }
}