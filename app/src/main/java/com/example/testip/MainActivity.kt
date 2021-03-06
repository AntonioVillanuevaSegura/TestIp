/*
 *Scanner IPs Android Kotlin
 * Antonio Villanueva Segura
 */
package com.example.testip

import android.annotation.SuppressLint
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
    var lineasCreadas=0 //Lineas de IPs creadas ,analizadas
    var tempo=0 //Un contador de tiempo , para evitar bloqueos en el ScrollBar de la vista de IPs

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val ip1 = binding.etiquetteIp //Binding Ip local del telefono

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        ip1.setText("IP ADDRESS $ipAddress") //Direccion IP telefono


        //Barra desplazamiento en la lista IPs ...bastante problematico
        binding.ipListe.movementMethod = ScrollingMovementMethod.getInstance()

        val button=binding.buttonScanner //Binding boton

        button.setOnClickListener {
            binding.ipListe.text = " .... searching ..."
            //button.setText("SEARCHING")

            testIpRange(ipAddress) //Analiza ips de 0 .. 255

            /*Segundo scope Evita crash TextView && scrollBar && corutina
            * Cuando no se crean nuevas lineas se copia el fichero temporal
            * con las ip temp en el TextView
            */
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {

                    while (tempo<10) { //Un tiempo antes de escribir en TextView no hay nuevas lineas
                        delay(10) //Retardo tonto para evitar crash en scroll Bar

                        if (lineasCreadas < cuentaLineasCreadas(tmp)) {
                            lineasCreadas = cuentaLineasCreadas(tmp)
                            tempo = 0 //Reset nueva linea
                        } else {
                            tempo++
                        }
                    }
                    //Escribe la lista de IPs en el TextView (tmp)
                    binding.ipListe.setText(tmp)
                }
            }
        }

    }

    //Analiza un rango de IPs desde la IP base en mascara 255.255.255.0..255
    fun testIpRange(ip_: String) {
        tmp=""

        var ip = ip_ //Recupera ip en dispositivo Android

        //Cut last value  in ip  ip[3]
        ip = ip.subSequence(0, ip.lastIndexOf(".")).toString()

        for (elem in 0..255) { //Recorre ips de 0 .. 255

            val ip_actual = ip + "." + elem.toString() //Ip actual

            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {

                    //var existe: Boolean
                    //existe = ipExist(ip_actual)
                    /*Antes de escribir en el TextView se guarda en un txt temporal
                    *Una segunda corrutina se encarga de escribir en el TextView
                    * pasado un tiempo sin nuevas lineas , es para evitar crash
                     */

                    if (ipExist(ip_actual)) {//Solo IPs que existen
                       // tmp += ip_actual + "..." + existe.toString() + '\n'
                        tmp += ip_actual + '\n'
                    }

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

