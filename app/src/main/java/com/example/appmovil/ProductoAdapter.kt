package com.example.appmovil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ProductoAdapter(
    private val context: Context,
    private var productos: List<Producto>
) : BaseAdapter() {
    
    override fun getCount(): Int = productos.size
    
    override fun getItem(position: Int): Producto = productos[position]
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        
        val producto = getItem(position)
        
        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        val textView2 = view.findViewById<TextView>(android.R.id.text2)
        
        textView1.text = "${producto.nombre} (ID: ${producto.id})"
        textView2.text = "Precio: $${producto.precio} | Stock: ${producto.cantidad}"
        
        return view
    }
    
    fun actualizarProductos(nuevosProductos: List<Producto>) {
        productos = nuevosProductos
        notifyDataSetChanged()
    }
}
