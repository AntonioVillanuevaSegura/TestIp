package com.example.testip


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testip.databinding.ActivityMainBinding

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view =binding.root

        setContentView(view)

        var ip1= binding.etiquetteIp
        ip1.setText("192.168.6.1")

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        ip1.setText("IP ADDRESS $ipAddress")



    }

}