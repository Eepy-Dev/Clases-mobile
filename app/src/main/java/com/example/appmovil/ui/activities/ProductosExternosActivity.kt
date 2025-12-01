package com.example.appmovil.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import coil.compose.AsyncImage
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import com.example.appmovil.data.mapper.ProductMapper
import com.example.appmovil.data.remote.model.ProductoExterno
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream

class ProductosExternosActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                ProductosExternosScreen(
                    onVolverClick = { finish() },
                    onAgregarClick = { producto ->
                        agregarProducto(producto)
                    },
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun agregarProducto(producto: Producto) {
        val intent = Intent(this, com.example.appmovil.ui.activities.IngresoActivity::class.java)
        intent.putExtra("producto", producto)
        startActivity(intent)
    }
}

@Composable
fun ProductosExternosScreen(
    onVolverClick: () -> Unit,
    onAgregarClick: (Producto) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    fun cargarProductos() {
        coroutineScope.launch {
            try {
                isLoading = true
                error = null
                val result = productoViewModel.obtenerProductosDelServidor()
                result.fold(
                    onSuccess = {
                        productos = it
                        isLoading = false
                    },
                    onFailure = { exception ->
                        error = exception.message ?: "Error desconocido al cargar productos"
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                error = "Error de conexión: ${e.message ?: "No se pudo conectar al catálogo"}"
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        cargarProductos()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Catálogo Online",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando catálogo...",
                            color = ChocolateDark
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error al cargar el catálogo",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = error ?: "Error desconocido",
                            color = ChocolateMedium,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { cargarProductos() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChocolateMedium
                            )
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
                productos.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay productos disponibles",
                            fontSize = 16.sp,
                            color = ChocolateDark,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { cargarProductos() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChocolateMedium
                            )
                        ) {
                            Text("Recargar", color = Color.White)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 80.dp)
                    ) {
                        items(productos) { producto ->
                            ProductoItem(
                                producto = producto,
                                onAgregarClick = { onAgregarClick(producto) }
                            )
                        }
                    }
                }
            }
        }
        
        Button(
            onClick = onVolverClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
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
    onAgregarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!producto.foto.isNullOrBlank()) {
                AsyncImage(
                    model = producto.foto,
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateDark
                )
                if (producto.descripcion.isNotBlank()) {
                    Text(
                        text = producto.descripcion,
                        fontSize = 12.sp,
                        color = ChocolateMedium,
                        modifier = Modifier.padding(vertical = 4.dp),
                        maxLines = 2
                    )
                }
                Text(
                    text = "Stock: ${producto.cantidad}",
                    fontSize = 14.sp,
                    color = ChocolateMedium
                )
                Text(
                    text = "$${producto.precio}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateDark
                )
            }
            
            Button(
                onClick = onAgregarClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                )
            ) {
                Text("Agregar", color = Color.White)
            }
        }
    }
}

