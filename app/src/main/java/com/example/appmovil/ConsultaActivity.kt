package com.example.appmovil

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream

class ConsultaActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                ConsultaScreen(
                    onVolverClick = { finish() },
                    onProductoClick = { producto ->
                        mostrarDetallesProducto(producto)
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
    
    private fun mostrarDetallesProducto(producto: Producto) {
        val intent = Intent(this, DetalleProductoActivity::class.java)
        intent.putExtra("producto", producto)
        startActivity(intent)
    }
}

@Composable
fun ConsultaScreen(
    onVolverClick: () -> Unit,
    onProductoClick: (Producto) -> Unit,
    onMensaje: (String) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var terminoBusqueda by remember { mutableStateOf("") }
    var productosFiltrados by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var mensaje by remember { mutableStateOf("") }
    
    DisposableEffect(Unit) {
        val productosObserver = androidx.lifecycle.Observer<List<Producto>> { productos ->
            productosFiltrados = productos
        }
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            mensaje = msg
        }
        productoViewModel.productosFiltrados.observeForever(productosObserver)
        productoViewModel.mensaje.observeForever(mensajeObserver)
        onDispose {
            productoViewModel.productosFiltrados.removeObserver(productosObserver)
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
                text = "Consulta de Productos",
                fontSize = 24.sp,
                color = ChocolateDark,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ChocolateMedium)
                    .padding(24.dp)
            )
            
            // Campo de búsqueda y botón
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = terminoBusqueda,
                    onValueChange = { terminoBusqueda = it },
                    label = { Text("Buscar productos") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChocolateMedium,
                        unfocusedBorderColor = ChocolateMedium,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = {
                        if (terminoBusqueda.trim().isNotEmpty()) {
                            productoViewModel.buscarProductos(terminoBusqueda.trim())
                        } else {
                            onMensaje("Ingresa un término de búsqueda")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    )
                ) {
                    Text(
                        text = "Buscar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Lista de resultados
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productosFiltrados) { producto ->
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