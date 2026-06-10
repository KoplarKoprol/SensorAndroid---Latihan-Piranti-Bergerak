package com.renos.ba244_sensor

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    
    private lateinit var tvStatus: TextView
    private lateinit var tvNilai: TextView
    private lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Inisialisasi View
        tvStatus = findViewById(R.id.tvStatus)
        tvNilai = findViewById(R.id.tvNilai)
        mainLayout = findViewById(R.id.mainLayout)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor == null) {
            tvStatus.text = "Sensor Cahaya Tidak Tersedia"
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val nilaiCahaya = event.values[0]
            tvNilai.text = "${nilaiCahaya.toInt()} lux"

            // Logika sesuai modul Pertemuan 11:
            // Jika cahaya rendah (< 20 lux), ubah background jadi gelap
            if (nilaiCahaya < 20) {
                tvStatus.text = "Kondisi: GELAP"
                mainLayout.setBackgroundColor(Color.BLACK)
                tvStatus.setTextColor(Color.WHITE)
                tvNilai.setTextColor(Color.WHITE)
            } else {
                tvStatus.text = "Kondisi: TERANG"
                mainLayout.setBackgroundColor(Color.WHITE)
                tvStatus.setTextColor(Color.BLACK)
                tvNilai.setTextColor(Color.BLACK)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Tidak perlu diimplementasikan untuk latihan ini
    }

    override fun onResume() {
        super.onResume()
        // Daftarkan listener sensor cahaya
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Lepas listener untuk menghemat baterai saat aplikasi di background
        sensorManager.unregisterListener(this)
    }
}
