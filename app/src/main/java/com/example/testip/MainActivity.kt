package com.example.testip


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testip.databinding.ActivityMainBinding
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.text.format.Formatter
import android.text.method.ScrollingMovementMethod
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception
import java.net.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var tmp="" //Texto temporal contiene las IPs
    var lineasCreadas=0
    var tempo=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val ip1 = binding.etiquetteIp //Ip aparato

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        ip1.setText("IP ADDRESS " + ipAddress)


        binding.ipListe.movementMethod = ScrollingMovementMethod.getInstance()

        var button=binding.buttonScanner

       // binding.ipListe.movementMethod=ScrollingMovementMethod ()
        button.setOnClickListener {
            binding.ipListe.setText(" .... searching ...")
            //button.setText("SEARCHING")

            testIpRange(ipAddress) //Analiza ips de 0 .. 255


            /*Segundo scope Evita crash TextView && scrollBar && corutina
            * Cuando no se crean nuevas lineas se copia el fichero temporal
            * con las ip temp en el TextView
            */
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {

                    while (tempo<10) { //Un tiempo antes de escribir en TextView no hay nuevas lineas
                        delay(10) //Retardo tonto

                        if (lineasCreadas < cuentaLineasCreadas(tmp)) {
                            lineasCreadas = cuentaLineasCreadas(tmp)
                            tempo = 0 //Reset nueva linea
                        } else {
                            tempo++
                            println ("Tempo "+tempo.toString())
                        }

                    }


                    //TextView (tmp)
                    binding.ipListe.setText(tmp)

                    //delay (1000)
                    //Restaura boton
                    //button.setText("scanner")
                }

            }


        }


    }
    //Analiza un rango de IPs desde la IP base en mascara 255.255.255.0..255
fun testIpRange(ip_: String) {

    var ListIp = binding.ipListe

    var ip = ip_ //Recupera ip en dispositivo Android

    //Cut last value  in ip  ip[3]
    ip = ip.subSequence(0, ip.lastIndexOf(".")).toString()

    for (elem in 0..255) { //Recorre ips de 0 .. 255

        val ip_actual = ip + "." + elem.toString() //Ip actual

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {

                var existe: Boolean
                existe = ipExist(ip_actual)
                /*Antes de escribir en el TextView se guarda en un txt temporal
                *Una segunda corrutina se encarga de escribir en el TextView
                * pasado un tiempo sin nuevas lineas
                 */
                tmp += ip_actual + "..." + existe.toString() + '\n'

            }
        }
    }

    }

    //Cuenta la cantidad de lineas creadas en txt
    fun cuentaLineasCreadas (txt:String):Int{
        var count=0
        for (elem in txt.lines()){
            count++
        }
        return count
    }

    //Existe la ip ..ping -c 1 host ?
    fun ipExist(host: String): Boolean {

        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1  $host")
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

