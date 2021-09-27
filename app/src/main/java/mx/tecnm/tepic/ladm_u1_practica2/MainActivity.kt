package mx.tecnm.tepic.ladm_u1_practica2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import mx.tecnm.tepic.ladm_u1_practica2.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException

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

        //Boton abrir
        b.searchbutton.setOnClickListener{
            abrir()
            b.menubotones.collapse()
        }
    }

    private fun abrir() {
        var estado = -2
        val nom = EditText(this)
        nom.inputType = InputType.TYPE_CLASS_TEXT
        nom.hint = "Introduce el nombre del archivo"
        AlertDialog.Builder(this)
            .setTitle("En que memoria quieres buscar")
            .setPositiveButton("Interna"){ dialog, i ->
                dialog.dismiss()
                AlertDialog.Builder(this)
                    .setTitle("Atención")
                    .setMessage("¿Cuál archivo quiere abrir?")
                    .setView(nom)
                    .setPositiveButton("Buscar"){d,i->
                        //interna
                            try{
                                openFileInput(nom.text.toString()).bufferedReader().useLines { lines ->
                                    val texto = lines.fold("") { some, text ->
                                        "$some\n$text"
                                    }
                                    val intento = Intent(this, EditarActivity::class.java)
                                    intento.putExtra("nArchivo",nom.text.toString())
                                    intento.putExtra("nTexto", texto)
                                    intento.putExtra("nTipo", "Memoria Interna")
                                    ContextCompat.startActivity(this, intento, null)
                                }
                            }
                            catch (e:IOException){
                                Toast.makeText(this,"Ha ocurrido un error, posiblemente no exista el archivo", Toast.LENGTH_LONG).show()
                            }
                        d.dismiss()
                    }
                    .setNegativeButton("Cancelar"){d,i->
                        d.cancel()
                    }
                    .show()
            }
            .setNegativeButton("Externa"){ dialog,i->
                if(tieneSd()){
                    estado = 1
                    dialog.dismiss()
                    AlertDialog.Builder(this)
                        .setTitle("Atención")
                        .setMessage("¿Cuál archivo quiere abrir?")
                        .setView(nom)
                        .setPositiveButton("Buscar"){d,i->
                            //externa
                                try{
                                    val externalStorageVolumes: Array<out File> =
                                        ContextCompat.getExternalFilesDirs(applicationContext, null)
                                    val primaryExternalStorage = externalStorageVolumes[externalStorageVolumes.lastIndex]
                                    val externo = File(primaryExternalStorage,nom.text.toString())
                                    val intento = Intent(this, EditarActivity::class.java)
                                    intento.putExtra("nArchivo",nom.text.toString())
                                    intento.putExtra("nTexto", externo.readText())
                                    intento.putExtra("nTipo", "Memoria Externa")
                                    ContextCompat.startActivity(this, intento, null)
                                }
                                catch (e:IOException){
                                    Toast.makeText(this,"Ha ocurrido un error, posiblemente no exista el archivo", Toast.LENGTH_LONG).show()
                                }
                            d.dismiss()
                        }
                        .setNegativeButton("Cancelar"){d,i->
                            d.cancel()
                        }
                        .show()
                }
                else{
                    Toast.makeText(this,"No cuenta con memoria externa o no se permite leer en ella", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .show()

        if(estado==-1){
            finish()
        }



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