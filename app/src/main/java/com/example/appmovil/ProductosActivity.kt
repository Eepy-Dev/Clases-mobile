package com.example.appmovil

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream

class ProductosActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                ProductosScreen(
                    onVolverClick = { finish() },
                    onProductoClick = { producto ->
                        mostrarDialogoEliminar(producto)
                    },
                    onMensaje = { mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    },
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }

    private fun mostrarDialogoEliminar(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que quieres eliminar el producto '${producto.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                productoViewModel.eliminarProducto(producto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

@Composable
fun ProductosScreen(
    onVolverClick: () -> Unit,
    onProductoClick: (Producto) -> Unit,
    onMensaje: (String) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var allProductos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var mensaje by remember { mutableStateOf("") }
    
    DisposableEffect(Unit) {
        val productosObserver = androidx.lifecycle.Observer<List<Producto>> { productos ->
            allProductos = productos
        }
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            mensaje = msg
        }
        productoViewModel.allProductos.observeForever(productosObserver)
        productoViewModel.mensaje.observeForever(mensajeObserver)
        onDispose {
            productoViewModel.allProductos.removeObserver(productosObserver)
            productoViewModel.mensaje.removeObserver(mensajeObserver)
        }
    }
    
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            onMensaje(mensaje)
            productoViewModel.limpiarMensaje()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Título
            Text(
                text = "Lista de Productos",
                fontSize = 24.sp,
                color = ChocolateDark,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ChocolateMedium)
                    .padding(24.dp)
            )
            
            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allProductos) { producto ->
                    ProductoItem(
                        producto = producto,
                        onClick = { onProductoClick(producto) }
                    )
                }
            }
        }
        
        // Botón Volver fijo en la parte inferior
        Button(
            onClick = onVolverClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                containerColor = ChocolateMedium
            )
        ) {
            Text(
                text = "Volver",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${producto.nombre} (ID: ${producto.id})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Precio: $${producto.precio} | Stock: ${producto.cantidad}",
                fontSize = 14.sp,
                color = ChocolateMedium
            )
        }
    }
}