package com.example.agrosmart

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    // Declaración de las variables de la UI
    private lateinit var textViewTemperature: TextView  // TextView para mostrar la temperatura
    private lateinit var textViewHumidity: TextView     // TextView para mostrar la humedad
    private lateinit var textViewLight: TextView        // TextView para mostrar el estado de la luz
    private lateinit var btnToggleLights: Button        // Botón para encender/apagar las luces
    private lateinit var btnToggleAir: Button           // Botón para encender/apagar el aire
    private lateinit var btnToggleWater: Button         // Botón para encender/apagar el riego
    private lateinit var btnGetData: Button             // Botón para obtener los datos de los sensores

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos las variables con los componentes de la UI
        textViewTemperature = findViewById(R.id.textViewTemperature)  // Conectamos el TextView de la temperatura
        textViewHumidity = findViewById(R.id.textViewHumidity)        // Conectamos el TextView de la humedad
        textViewLight = findViewById(R.id.textViewLight)              // Conectamos el TextView del estado de la luz
        btnToggleLights = findViewById(R.id.btnToggleLights)         // Conectamos el botón de las luces
        btnToggleAir = findViewById(R.id.btnToggleAir)               // Conectamos el botón del aire
        btnToggleWater = findViewById(R.id.btnToggleWater)           // Conectamos el botón del riego
        btnGetData = findViewById(R.id.btnGetData)                   // Conectamos el botón para obtener datos

        // Configuramos los listeners de los botones para enviar comandos al Arduino
        btnToggleLights.setOnClickListener {
            sendCommandToArduino("L")  // Enviar comando 'L' para activar/desactivar la luz
        }

        btnToggleAir.setOnClickListener {
            sendCommandToArduino("V")  // Enviar comando 'V' para activar/desactivar el ventilador
        }

        btnToggleWater.setOnClickListener {
            sendCommandToArduino("R")  // Enviar comando 'R' para activar/desactivar el riego
        }

        btnGetData.setOnClickListener {
            getDataFromArduino()  // Obtener los datos de los sensores desde el Arduino
        }
    }

    // Método para enviar comandos HTTP al Arduino
    private fun sendCommandToArduino(command: String) {
        val url = "http://192.168.4.1/$command"  // URL del Arduino (hotspot) con el comando a enviar
        AsyncTask.execute {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection  // Abrimos la conexión HTTP
                connection.requestMethod = "GET"  // Establecemos el método de la petición como GET
                connection.connect()  // Conectamos al servidor
                val responseCode = connection.responseCode  // Obtenemos el código de respuesta
                if (responseCode == HttpURLConnection.HTTP_OK) {  // Si la respuesta es exitosa
                    runOnUiThread {
                        Toast.makeText(this, "Comando enviado: $command", Toast.LENGTH_SHORT).show()  // Mostramos un mensaje de confirmación
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al enviar el comando", Toast.LENGTH_SHORT).show()  // Mostramos un mensaje de error
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()  // Imprimimos el error en el log
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()  // Mostramos un mensaje de error de conexión
                }
            }
        }
    }

    // Método para obtener datos de los sensores desde el Arduino
    private fun getDataFromArduino() {
        val url = "http://192.168.4.1/getData"  // URL del endpoint que devuelve los datos de los sensores
        AsyncTask.execute {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection  // Abrimos la conexión HTTP
                connection.requestMethod = "GET"  // Establecemos el método de la petición como GET
                connection.connect()  // Conectamos al servidor

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {  // Si la respuesta es exitosa
                    val inputStream = connection.inputStream  // Obtenemos el stream de datos
                    val reader = BufferedReader(InputStreamReader(inputStream))  // Creamos un lector para leer los datos
                    val response = reader.readLine()  // Leemos la línea de respuesta

                    // Parsear la respuesta recibida (por ejemplo, L:100,T:25,H:45)
                    val data = response.split(",")  // Dividimos la respuesta en partes usando la coma como delimitador
                    val luz = data[0].split(":")[1].toInt()  // Obtenemos el valor de luz
                    val temperatura = data[1].split(":")[1].toInt()  // Obtenemos el valor de la temperatura
                    val humedad = data[2].split(":")[1].toInt()  // Obtenemos el valor de la humedad

                    // Actualizamos la UI con los datos
                    runOnUiThread {
                        textViewTemperature.text = "$temperatura°C"  // Mostramos la temperatura en el TextView
                        textViewHumidity.text = "$humedad%"  // Mostramos la humedad en el TextView
                        textViewLight.text = if (luz > 0) "Encendida" else "Apagada"  // Mostramos el estado de la luz en el TextView
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al obtener datos del Arduino", Toast.LENGTH_SHORT).show()  // Mostramos un mensaje de error
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()  // Imprimimos el error en el log
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()  // Mostramos un mensaje de error de conexión
                }
            }
        }
    }
}
