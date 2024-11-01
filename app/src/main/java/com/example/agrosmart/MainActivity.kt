package com.example.agrosmart

import android.os.Bundle
import android.os.AsyncTask
import android.util.Log
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

    // Declaración de variables de la UI
    private lateinit var textViewTemperature: TextView
    private lateinit var textViewHumidity: TextView
    private lateinit var textViewLight: TextView
    private lateinit var btnToggleLights: Button
    private lateinit var btnToggleAir: Button
    private lateinit var btnToggleWater: Button
    private lateinit var btnGetData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de las variables de la UI
        textViewTemperature = findViewById(R.id.textViewTemperature)
        textViewHumidity = findViewById(R.id.textViewHumidity)
        textViewLight = findViewById(R.id.textViewLight)
        btnToggleLights = findViewById(R.id.btnToggleLights)
        btnToggleAir = findViewById(R.id.btnToggleAir)
        btnToggleWater = findViewById(R.id.btnToggleWater)
        btnGetData = findViewById(R.id.btnGetData)

        // Cargar los datos al hacer clic en el botón "Obtener Datos"
        btnGetData.setOnClickListener {
            getDataFromArduino()
        }

        // Configurar los botones para enviar comandos al Arduino
        btnToggleLights.setOnClickListener {
            sendCommandToArduino("/toggle_light")
        }

        btnToggleAir.setOnClickListener {
            sendCommandToArduino("/toggle_ventilator")
        }

        btnToggleWater.setOnClickListener {
            sendCommandToArduino("/toggle_water")
        }
    }

    // Función para obtener los datos del Arduino mediante una solicitud GET
    private fun getDataFromArduino() {
        val url = "http://192.168.4.1/getData"  // Dirección IP del Arduino
        AsyncTask.execute {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readLine()
                reader.close()

                // Parsear la respuesta JSON
                val jsonObject = JSONObject(response)
                val temperature = jsonObject.getInt("temperature")
                val humidity = jsonObject.getInt("humidity")
                val light = jsonObject.getInt("light")

                // Actualizar la UI en el hilo principal
                runOnUiThread {
                    textViewTemperature.text = "Temperatura: $temperature°C"
                    textViewHumidity.text = "Humedad: $humidity%"
                    textViewLight.text = "Luz: $light"
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al obtener datos: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // Función para enviar comandos al Arduino
    private fun sendCommandToArduino(endpoint: String) {
        val url = "http://192.168.4.1$endpoint"  // Dirección IP del Arduino + endpoint
        AsyncTask.execute {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                // Verificar la respuesta del servidor
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    runOnUiThread {
                        Toast.makeText(this, "Comando enviado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al enviar comando", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al enviar comando: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
