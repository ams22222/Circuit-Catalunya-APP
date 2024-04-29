package com.example.m13_circuitdecatalunya

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.m13_circuitdecatalunya.databinding.ComandaItemBinding

class ComandaAdapter(
    var comandas: List<Comanda> = emptyList(),
    private val mContext: Context
) :
    RecyclerView.Adapter<ComandaAdapter.ViewHolder>() {
    var onItemClick: ((Comanda) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.comanda_item, parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val poblacio = comandas[position]
        holder.bind(poblacio)

        val binding = ComandaItemBinding.bind(holder.itemView)
    }


    override fun getItemCount(): Int = comandas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding: ComandaItemBinding = ComandaItemBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(comanda: Comanda) {
            binding.tvId.text = "ID:"
            binding.tvIdComanda.text = comanda.id.toString()

            binding.tvEmail.text = "MAIL:"
            binding.tvEmailComanda.text = comanda.email

            binding.tvIdEspai.text = "ID ESPAI:"
            binding.tvEspai.text = comanda.espai_id.toString()

            binding.tvNomEspai.text = "NOM ESPAI:"
            binding.tvEspaiN.text = getNombreEspacio(comanda.espai_id)

            binding.tvEstadoComanda.text = "ESTAT COMANDA:"
            binding.tvEstat.text = comanda.estat_comanda
        }
    }

    private fun getNombreEspacio(espacioId: Int): String {
        val ubicaciones = mContext.resources.getStringArray(R.array.Ubicacio)
        return ubicaciones.getOrNull(espacioId - 1) ?: ""
    }

}