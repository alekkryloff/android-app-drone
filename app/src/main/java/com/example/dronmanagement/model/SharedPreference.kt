package com.example.dronmanagement.model

import android.content.Context

class SharedPreference(ctx: Context) {
    private val sharedPreference =  ctx.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
    private val editor = sharedPreference.edit()

    private fun setString(variable: String, string: String){
        editor.putString(variable, string)
        editor.commit()
    }

    private fun setInt(variable: String, int: Int){
        editor.putInt(variable, int)
        editor.commit()
    }

    private fun getString(variable: String): String {
        return sharedPreference.getString(variable, "")!!
    }

    private fun getInt(variable: String): Int {
        return sharedPreference.getInt(variable, 0)
    }

    fun setIpPort(ip: String){ setString("IP:Port", ip) }
    fun getIpPort(): String { return getString("IP:Port") }

    fun setMode(mode: Int){ setInt("MODE", mode) }
    fun getMode(): Int { return getInt("MODE") }
}