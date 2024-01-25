package com.example.ficheroskotlin

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ficheroskotlin.databinding.ActivityMainBinding
import java.io.IOException


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    //private lateinit var memoria: Memoria

    companion object {
        const val FICHERO = "ficheroExterna.txt"
        const val REQUEST_WRITE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.botonEscribir.setOnClickListener(this)
        binding.botonLeer.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        // Si pulsamos en el botón escribir
        if (view === binding.botonEscribir)
            guardar()
        else if (view === binding.botonLeer)
            leer()
    }

    fun guardar() {
        val permiso = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ActivityCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED
        ) {
            // pedir los permisos necesarios, porque no están concedidos
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                mostrarMensaje("Permiso rechazado para escribir en m. externa\nModifique los ajustes")
            } else {
                ActivityCompat.requestPermissions(this, arrayOf<String>(permiso), REQUEST_WRITE)
                // Cuando se cierre el cuadro de diálogo se ejecutará onRequestPermissionsResult
            }
        } else {
            escribir(binding.editText.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permiso = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        // chequeo los permisos de nuevo
        // chequeo los permisos de nuevo
        if (requestCode === REQUEST_WRITE) if (ActivityCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED) // permiso concedido
            escribir(binding.editText.toString())
        else  // no hay permiso
            mostrarMensaje("No se ha concedido permiso para escribir en la memoria externa")
    }


    private fun mostrarMensaje(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
    }

    private fun escribir(texto: String) {
        if (Memoria.disponibleEscritura()) {
            try {
                if (Memoria.escribirExterna(FICHERO, texto)) {
                    binding.textView.text = Memoria.mostrarPropiedadesExterna(FICHERO)
                    mostrarMensaje("Fichero escrito OK")
                } else {
                    binding.textView.text = "Error al escribir en el fichero " + FICHERO
                }
            } catch (e: IOException) {
                e.printStackTrace()
                mostrarMensaje("IOExepction: " + e.message)
                Log.e("Error", "Error al escribir en la memoria externa: " + e.message)
            }
        } else {
            binding.textView.text = "Memoria externa no disponible"
        }
   }

    private fun leer() {
        try {
            binding.editText.setText(Memoria.leerExterna(FICHERO))
            mostrarMensaje("Fichero leido OK")
        } catch (e: IOException) {
            e.printStackTrace()
            binding.editText.setText(" ")
            mostrarMensaje("Error al leer de la memoria externa: " + e.message)
            Log.e("Error", "Error al leer de la memoria externa: " + e.message)
        }
    }
}