package com.example.testip


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testip.databinding.ActivityMainBinding
import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.text.method.ScrollingMovementMethod
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception
import java.net.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)


        val ip1 = binding.etiquetteIp
        //ip1.setText("192.168.6.1")

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        ip1.setText("IP ADDRESS " + ipAddress)



        binding.buttonScanner.setOnClickListener {
            binding.editTextTextMultiLineIp.setText(" ")
            binding.buttonScanner.setText("SEARCHING")
            try {
                testIpRange(ipAddress,wifiManager)
            }catch (e:Exception){
                println ("ERROR = "+e.toString())
            }

        }

    }


    fun testIpRange(ip_: String,wifiManager: WifiManager) {


        var existe: Boolean = false
        var ListIp = binding.editTextTextMultiLineIp

        var ip = ip_ //Recupera ip en dispositivo Android

        //Cut last value  in ip  ip[3]
        ip = ip.subSequence(0, ip.lastIndexOf(".")).toString()

        for (elem in 0..254) { //Recorre ips de 0 .. 255

            val ip_actual = ip + "." + elem.toString() //Ip actual


                    GlobalScope.launch(Dispatchers.Main) {

                        withContext(Dispatchers.IO) {


                                existe = ipExist(ip_actual)

                            ListIp.setText( //Actualiza view ip actual
                                ListIp.getText().toString() + '\n' + ip_actual+"..."+existe.toString())

                            /*
                                if (existe) {

                                    ListIp.setText( //Actualiza view ip actual
                                        ListIp.getText().toString() + '\n' + ip_actual

                                    )


                                }
                            */
                        }

                    }



        }

    }


    fun ipExist(host: String): Boolean {

        val runtime = Runtime.getRuntime()
        try {

            val ipProcess = runtime.exec("/system/bin/ping -c 1 $host")

            val exitValue = ipProcess.waitFor()

            ipProcess.destroy()
            return exitValue == 0

        } catch (e: UnknownHostException) {
            e.printStackTrace()

        } catch (e: IOException) {
            e.printStackTrace()

        } catch (e: InterruptedException) {
            e.printStackTrace()

        }

        return false

    }


}

