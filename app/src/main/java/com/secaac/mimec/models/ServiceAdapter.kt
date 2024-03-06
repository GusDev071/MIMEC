package com.secaac.mimec

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(
    private var services: List<Service>,
    private val onEdit: (Service) -> Unit,
    private val onDelete: (Service) -> Unit,
    private val onSave: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceName: TextView = view.findViewById(R.id.service_name)
        val cost: TextView = view.findViewById(R.id.cost)
        val serviceType: TextView = view.findViewById(R.id.service_type)
        val description: TextView = view.findViewById(R.id.description)
        val editButton: Button = view.findViewById(R.id.edit_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = services[position]
        holder.serviceName.text = "Nombre del servicio: ${service.serviceName}"
        holder.cost.text = "Costo: ${service.cost}"
        holder.serviceType.text = "Tipo de servicio: ${service.serviceType}"
        holder.description.text = "Descripción: ${service.description}"
        holder.editButton.setOnClickListener { onEdit(service) }
        holder.deleteButton.setOnClickListener { onDelete(service) }

    }

    override fun getItemCount() = services.size

    fun updateData(newServices: List<Service>) {
        services = newServices
        notifyDataSetChanged()
    }
}