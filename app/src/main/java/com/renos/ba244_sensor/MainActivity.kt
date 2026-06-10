package com.renos.ba244_sensor

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var accelSensor: Sensor? = null

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var layoutUtama: LinearLayout
    private lateinit var layoutRiwayat: LinearLayout
    
    private lateinit var tvTitle: TextView
    private lateinit var tvStatusSetir: TextView
    private lateinit var tvSumbuX: TextView
    private lateinit var tvStatusLight: TextView
    private lateinit var tvDataRiwayat: TextView
    private lateinit var tvTitleRiwayat: TextView

    private lateinit var btnKirim: Button
    private lateinit var btnRiwayat: Button
    private lateinit var btnKembali: Button

    private val listRiwayat = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inisialisasi View
        mainLayout = findViewById(R.id.mainLayout)
        layoutUtama = findViewById(R.id.layoutUtama)
        layoutRiwayat = findViewById(R.id.layoutRiwayat)
        
        tvTitle = findViewById(R.id.tvTitle)
        tvStatusSetir = findViewById(R.id.tvStatusSetir)
        tvSumbuX = findViewById(R.id.tvSumbuX)
        tvStatusLight = findViewById(R.id.tvStatusLight)
        tvDataRiwayat = findViewById(R.id.tvDataRiwayat)
        tvTitleRiwayat = findViewById(R.id.tvTitleRiwayat)

        btnKirim = findViewById(R.id.btnKirim)
        btnRiwayat = findViewById(R.id.btnRiwayat)
        btnKembali = findViewById(R.id.btnKembali)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Tombol Kirim ke Cloud (Simulasi Simpan Data)
        btnKirim.setOnClickListener {
            val timestamp = SimpleDateFormat("HH.mm.ss", Locale.getDefault()).format(Date())
            val data = "Sumbu X: ${tvSumbuX.text.toString().replace("Sumbu X: ", "")}\n" +
                       "Status Setir: ${tvStatusSetir.text}\n" +
                       "Timestamp: $timestamp\n"
            listRiwayat.add(0, data) // Tambah di paling atas
            Toast.makeText(this, "Data berhasil dikirim ke cloud", Toast.LENGTH_SHORT).show()
        }

        // Tombol Lihat Riwayat
        btnRiwayat.setOnClickListener {
            layoutUtama.visibility = View.GONE
            layoutRiwayat.visibility = View.VISIBLE
            
            if (listRiwayat.isEmpty()) {
                tvDataRiwayat.text = "Belum ada data..."
            } else {
                tvDataRiwayat.text = listRiwayat.joinToString("\n")
            }
        }

        // Tombol Kembali
        btnKembali.setOnClickListener {
            layoutRiwayat.visibility = View.GONE
            layoutUtama.visibility = View.VISIBLE
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // 1. Logika Sensor Cahaya
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val nilaiCahaya = event.values[0]
            if (nilaiCahaya < 20) {
                updateTema(true) // Gelap
            } else {
                updateTema(false) // Terang
            }
        }

        // 2. Logika Sensor Accelerometer
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            tvSumbuX.text = "Sumbu X: ${String.format(Locale.getDefault(), "%.2f", x)}"

            if (x > 2.0) {
                tvStatusSetir.text = "Belok Kiri"
            } else if (x < -2.0) {
                tvStatusSetir.text = "Belok Kanan"
            } else {
                tvStatusSetir.text = "Lurus"
            }
        }
    }

    private fun updateTema(isGelap: Boolean) {
        if (isGelap) {
            mainLayout.setBackgroundColor(Color.BLACK)
            setTextColorAll(Color.WHITE)
            tvStatusLight.text = "Kondisi: GELAP"
        } else {
            mainLayout.setBackgroundColor(Color.WHITE)
            setTextColorAll(Color.BLACK)
            tvStatusLight.text = "Kondisi: TERANG"
        }
    }

    private fun setTextColorAll(color: Int) {
        tvTitle.setTextColor(color)
        tvStatusSetir.setTextColor(color)
        tvSumbuX.setTextColor(color)
        tvStatusLight.setTextColor(color)
        tvTitleRiwayat.setTextColor(color)
        tvDataRiwayat.setTextColor(color)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        lightSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        accelSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
