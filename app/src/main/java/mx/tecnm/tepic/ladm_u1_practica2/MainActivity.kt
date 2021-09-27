package mx.tecnm.tepic.ladm_u1_practica2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import mx.tecnm.tepic.ladm_u1_practica2.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    //Sacar todos los archivos
    var files = ArrayList<String>()
    var notas = ArrayList<Notas>()
    lateinit var adapter: adapter
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        //Crear nota
        b.addbutton.setOnClickListener {
            startActivity(Intent(this,agregar_notas::class.java))
           b.menubotones.collapse()
        }
        //llenamos la lista
        llenar()
        //Mandar información al recycler
        b.lista.layoutManager = LinearLayoutManager(this)
        adapter = adapter(applicationContext,notas)
        b.lista.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        llenar()
        adapter = adapter(applicationContext,notas)
        b.lista.adapter = adapter
    }

        fun llenar(){
        if(this.fileList().size == 1){
            files.clear()
            files.add(this.fileList()[0])
        }
        else if(this.fileList().size >= 1){
            files.clear()
            files = this.fileList().toList() as ArrayList<String>
        }
        notas = ArrayList()
        //abrir todos los archivos y mandar la información de interna
        files.forEach {
            openFileInput(it).bufferedReader().useLines { lines ->
                val texto =lines.fold("") { some, text ->
                    "$some\n$text"
                }
                notas.add(Notas(it,texto,"Memoria Interna"))
            }
        } //se termina foreach
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(applicationContext, null)
        val primaryExternalStorage = externalStorageVolumes[externalStorageVolumes.lastIndex]
        val filesE: Array<File> = File(primaryExternalStorage.absoluteFile.toURI()).listFiles()
        filesE.forEach {
            val externo = File(primaryExternalStorage,it.name)
            notas.add(Notas(it.name,externo.readText(),"Memoria Externa"))
        }
    }
}