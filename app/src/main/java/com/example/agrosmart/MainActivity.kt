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

    private lateinit var textViewTemperature: TextView
    private lateinit var textViewHumidity: TextView
    private lateinit var btnGetData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewTemperature = findViewById(R.id.textViewTemperature)
        textViewHumidity = findViewById(R.id.textViewHumidity)
        btnGetData = findViewById(R.id.btnGetData)

        // Configurar el botón para obtener los datos del ESP8266
        btnGetData.setOnClickListener {
            fetchSensorData()
        }
    }

    private fun fetchSensorData() {
        val url = "http://192.168.4.1/datos"
        SensorDataTask().execute(url)
    }

    inner class SensorDataTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String?): String? {
            return try {
                val url = URL(urls[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                val inputStream = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val result = inputStream.readLine()
                inputStream.close()
                result
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                try {
                    val jsonObject = JSONObject(result)
                    val temperatura = jsonObject.getDouble("temperatura")
                    val humedad = jsonObject.getDouble("humedad")

                    // Actualizar los TextViews con los datos recibidos
                    textViewTemperature.text = "Temperatura: $temperatura °C"
                    textViewHumidity.text = "Humedad: $humedad %"
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this@MainActivity, "Error al recibir los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
