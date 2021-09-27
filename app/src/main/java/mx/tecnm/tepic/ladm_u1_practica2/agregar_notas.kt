package mx.tecnm.tepic.ladm_u1_practica2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import mx.tecnm.tepic.ladm_u1_practica2.databinding.ActivityAgregarNotasBinding
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

public class agregar_notas : AppCompatActivity() {
    private lateinit var b: ActivityAgregarNotasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         b = ActivityAgregarNotasBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnAgrego.setOnClickListener {
            agregar()
        }

    }

    fun agregar(){
        val filename = b.etxArchivo.text.toString()
        if(filename==""){
            Toast.makeText(this,"El nombre del archivo no puede ser vacio",Toast.LENGTH_SHORT).show()
            return
        }
        val fileContents = b.etxNota.text.toString()

        AlertDialog.Builder(this)
            .setTitle("En que memoria lo quieres guardar")
            .setPositiveButton("Interna"){ dialog, i ->
                try {
                    openFileOutput(filename, Context.MODE_PRIVATE).use {
                        it.write(fileContents.toByteArray())
                        Toast.makeText(this, "Se ha guardado el archivo con exito", Toast.LENGTH_SHORT).show()
                    }
                } catch (io: SecurityException) {
                    Toast.makeText(this,"Ocurrio un error al guardar",Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Externa"){ dialog,i->
                if(tieneSd()){
                    try {
                        /*Los dispositivos por una u otra razon pueden tener mas de un dispositivo
                          de almacenamiento externo, en algunos casos hacen hasta particiones
                          locales y las toman como su almacenamiento externo en este caso por metodos
                          didacticos tomare el ultimo que normalmente sera una sd card aunque se
                          recomienda usar el primero a menos de que este lleno*/
                        val externalStorageVolumes: Array<out File> =
                            ContextCompat.getExternalFilesDirs(applicationContext, null)
                        val primaryExternalStorage = externalStorageVolumes[externalStorageVolumes.lastIndex]
                        val file = File(primaryExternalStorage, filename)
                        file.writeText(fileContents)
                        file.createNewFile()
                        Toast.makeText(this, "Se ha guardado el archivo con exito", Toast.LENGTH_SHORT).show()
                    }
                    catch (io: SecurityException) {
                        Toast.makeText(this,"Ocurrio un error al guardar",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this,"No cuenta con memoria externa o no se permite escribir en ella",Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()

                finish()
            }
            .show()
    }

}
fun tieneSd(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}
