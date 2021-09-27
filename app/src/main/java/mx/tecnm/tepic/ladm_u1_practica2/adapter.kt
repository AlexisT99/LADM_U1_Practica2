package mx.tecnm.tepic.ladm_u1_practica2

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.io.File

public class adapter :
    RecyclerView.Adapter<adapter.MyViewHolder> {

    lateinit var contexto: Context
    lateinit var nota: ArrayList<Notas>

    constructor(context: Context, notas: ArrayList<Notas>?) {
        contexto = context
        if (notas != null) {
            nota = notas
        }
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombre : TextView ?= null
        var descripcion : TextView ?=null
        var tipo : TextView ?=null;


        init {
            nombre = itemView.findViewById(R.id.titulo)
            descripcion = itemView.findViewById(R.id.descnota)
            tipo = itemView.findViewById(R.id.locacion)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(contexto).inflate(R.layout.item_view,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val n: Notas = nota.get(position)
        holder.nombre?.setText(n.nombre)
        holder.descripcion?.setText(n.texto)
        holder.tipo?.setText(n.tipo)

        holder.itemView.setOnLongClickListener {
            val menu:PopupMenu = PopupMenu(contexto,it)
            menu.menu.add("Borrar")
            menu.setOnMenuItemClickListener {
                if(it.title.equals("Borrar")){
                    val externalStorageVolumes: Array<out File> =
                        ContextCompat.getExternalFilesDirs(contexto, null)
                    val primaryExternalStorage = externalStorageVolumes[externalStorageVolumes.lastIndex]
                    if(n.tipo.equals("Memoria Interna") == true){
                        val file = File(contexto.filesDir, n.nombre)
                        file.delete()
                    }
                    else{
                        val file = File(primaryExternalStorage, n.nombre)
                        file.delete()
                    }
                    nota.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, getItemCount())
                }
                true
            }
            menu.show()
             true
        }
        holder.itemView.setOnClickListener {
            val intento = Intent(contexto, EditarActivity::class.java)
            intento.putExtra("nArchivo", n.nombre)
            intento.putExtra("nTexto", n.texto)
            intento.putExtra("nTipo", n.tipo)
            intento.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(contexto,intento, null)
        }
    }

    override fun getItemCount(): Int {
        return nota.size
    }
}