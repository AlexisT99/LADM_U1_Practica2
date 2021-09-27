package mx.tecnm.tepic.ladm_u1_practica2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import mx.tecnm.tepic.ladm_u1_practica2.databinding.ActivityEditarBinding
import mx.tecnm.tepic.ladm_u1_practica2.databinding.ActivityMainBinding
import java.io.File

class EditarActivity : AppCompatActivity() {
    private lateinit var b: ActivityEditarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditarBinding.inflate(layoutInflater)
        setContentView(b.root)
        val bundle = intent.extras
        val archivo = bundle?.getString("nArchivo")
        val texto = bundle?.getString("nTexto")
        val tipo= bundle?.getString("nTipo")
        b.txtNota.setText(archivo)
        b.etxedNota.setText(texto)

        b.btnActual.setOnClickListener {
            if(tipo.equals("Memoria Interna")){
                try {
                    openFileOutput(archivo, Context.MODE_PRIVATE).use {
                        it.write(b.etxedNota.text.toString().toByteArray())
                        Toast.makeText(this,"Actualizado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (io: SecurityException) {
                    Toast.makeText(this,"Ocurrio un error al Actualizar", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
            else{
                val externalStorageVolumes: Array<out File> =
                    ContextCompat.getExternalFilesDirs(applicationContext, null)
                val primaryExternalStorage = externalStorageVolumes[externalStorageVolumes.lastIndex]
                val file = File(primaryExternalStorage, archivo)
                file.writeText(b.etxedNota.text.toString())
                file.createNewFile()
                Toast.makeText(this, "Se ha Actualizado el archivo con exito", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}