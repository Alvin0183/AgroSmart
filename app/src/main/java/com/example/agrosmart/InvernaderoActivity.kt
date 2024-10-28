package com.example.agrosmart

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InvernaderoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invernadero)

        // Actualizar datos de ejemplo
        val tvTemperatura: TextView = findViewById(R.id.tvTemperatura)
        val tvHumedad: TextView = findViewById(R.id.tvHumedad)
        val tvLuz: TextView = findViewById(R.id.tvLuz)

        // Simulación de datos (esto luego puede ser dinámico)
        tvTemperatura.text = "25°C"
        tvHumedad.text = "60%"
        tvLuz.text = "Encendida"

        // Configurar los botones (no tienen funcionalidad aún)
        val btnRiego: Button = findViewById(R.id.btnRiego)
        val btnLuces: Button = findViewById(R.id.btnLuces)
        val btnApagarLuces: Button = findViewById(R.id.btnApagarLuces)

        // Aquí puedes implementar la funcionalidad de los botones más adelante
    }
}
