package com.example.appmovil.ui.activities

import android.app.AlertDialog
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
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream

class SalidaActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                SalidaScreen(
                    onVolverClick = { finish() },
                    onMensaje = { mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                    },
                    onProductoSeleccionado = { producto ->
                        mostrarDialogoSeleccionarTipo(producto)
                    },
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun mostrarDialogoSeleccionarTipo(producto: Producto) {
        val tipos = arrayOf("Venta", "Merma", "Movimiento")
        var tipoSeleccionado: String? = null
        
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Seleccionar tipo de salida")
        dialogBuilder.setSingleChoiceItems(tipos, -1) { dialog, which ->
            tipoSeleccionado = when (which) {
                0 -> "SALIDA_VENTA"
                1 -> "SALIDA_MERMA"
                2 -> "SALIDA_MOVIMIENTO"
                else -> null
            }
        }
        
        dialogBuilder.setPositiveButton("Siguiente") { dialog, _ ->
            tipoSeleccionado?.let { tipo ->
                mostrarDialogoCantidad(producto, tipo)
            }
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }
    
    private fun mostrarDialogoCantidad(producto: Producto, tipo: String) {
        val tipoTexto = when (tipo) {
            "SALIDA_VENTA" -> "Venta"
            "SALIDA_MERMA" -> "Merma"
            "SALIDA_MOVIMIENTO" -> "Movimiento"
            else -> tipo
        }
        
        val cantidadInput = android.widget.EditText(this)
        cantidadInput.hint = "Cantidad"
        cantidadInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        
        AlertDialog.Builder(this)
            .setTitle("Cantidad para ${tipoTexto}")
            .setMessage("Producto: ${producto.nombre}\nStock disponible: ${producto.cantidad}")
            .setView(cantidadInput)
            .setPositiveButton("Confirmar") { _, _ ->
                val cantidadTexto = cantidadInput.text.toString()
                val cantidad = cantidadTexto.toIntOrNull()
                
                if (cantidad == null || cantidad <= 0) {
                    Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                } else if (cantidad > producto.cantidad) {
                    Toast.makeText(this, "La cantidad no puede ser mayor al stock disponible", Toast.LENGTH_SHORT).show()
                } else {
                    mostrarDialogoConfirmacion(producto, cantidad, tipo)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun mostrarDialogoConfirmacion(producto: Producto, cantidad: Int, tipo: String) {
        val tipoTexto = when (tipo) {
            "SALIDA_VENTA" -> "Venta"
            "SALIDA_MERMA" -> "Merma"
            "SALIDA_MOVIMIENTO" -> "Movimiento"
            else -> tipo
        }
        
        AlertDialog.Builder(this)
            .setTitle("Confirmar Salida")
            .setMessage("¿Deseas registrar una salida de ${cantidad} unidad(es) del producto '${producto.nombre}' como ${tipoTexto}?")
            .setPositiveButton("Confirmar") { _, _ ->
                productoViewModel.registrarSalida(producto, cantidad, tipo)
                // El mensaje se manejará desde el observer y mostrará toast
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

@Composable
fun SalidaScreen(
    onVolverClick: () -> Unit,
    onMensaje: (String) -> Unit,
    onProductoSeleccionado: (Producto) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var terminoBusqueda by remember { mutableStateOf("") }
    var productosEncontrados by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var mensaje by remember { mutableStateOf("") }
    var estaBuscando by remember { mutableStateOf(false) }
    var mensajeProcesado by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Cargar los últimos 5 productos al iniciar
    LaunchedEffect(Unit) {
        productoViewModel.cargarUltimosProductos(5)
    }
    
    // Observer para los productos filtrados (últimos productos)
    DisposableEffect(Unit) {
        val productosObserver = androidx.lifecycle.Observer<List<Producto>> { productos ->
            // Solo actualizar si no hay término de búsqueda activo
            if (terminoBusqueda.isEmpty()) {
                productosEncontrados = productos
            }
        }
        productoViewModel.productosFiltrados.observeForever(productosObserver)
        onDispose {
            productoViewModel.productosFiltrados.removeObserver(productosObserver)
        }
    }
    
    DisposableEffect(Unit) {
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            if (msg.isNotEmpty() && msg.contains("Salida registrada") && !mensajeProcesado) {
                mensaje = msg
            }
        }
        productoViewModel.mensaje.observeForever(mensajeObserver)
        onDispose {
            productoViewModel.mensaje.removeObserver(mensajeObserver)
        }
    }
    
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty() && mensaje.contains("Salida registrada") && !mensajeProcesado) {
            mensajeProcesado = true
            onMensaje("Salida registrada exitosamente")
            productoViewModel.limpiarMensaje()
            // Limpiar búsqueda y resultados para volver al estado inicial
            kotlinx.coroutines.delay(500) // Pequeño delay antes de limpiar
            terminoBusqueda = ""
            productosEncontrados = emptyList()
            mensaje = ""
            mensajeProcesado = false
        }
    }
    
    // Búsqueda con delay para evitar búsquedas en cada tecla
    LaunchedEffect(terminoBusqueda) {
        if (terminoBusqueda.isNotEmpty()) {
            estaBuscando = true
            kotlinx.coroutines.delay(300) // Esperar 300ms antes de buscar
            val resultados = productoViewModel.buscarProductosSuspend(terminoBusqueda)
            productosEncontrados = resultados
            estaBuscando = false
        } else {
            // Si no hay término de búsqueda, mostrar los últimos 5 productos
            productoViewModel.cargarUltimosProductos(5)
            estaBuscando = false
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
                text = "Salida de Productos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ChocolateMedium)
                    .padding(24.dp)
            )
            
            // Campo de búsqueda con botón
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = terminoBusqueda,
                    onValueChange = { terminoBusqueda = it },
                    label = { androidx.compose.material3.Text("Buscar producto (nombre o ID)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    trailingIcon = if (estaBuscando) {
                        {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChocolateMedium,
                        unfocusedBorderColor = ChocolateMedium,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark
                    )
                )
                
                Button(
                    onClick = {
                        if (terminoBusqueda.trim().isNotEmpty()) {
                            estaBuscando = true
                            scope.launch {
                                val resultados = productoViewModel.buscarProductosSuspend(terminoBusqueda.trim())
                                productosEncontrados = resultados
                                estaBuscando = false
                            }
                        }
                    },
                    enabled = terminoBusqueda.trim().isNotEmpty() && !estaBuscando,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    )
                ) {
                    androidx.compose.material3.Text(
                        text = "Buscar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Mostrar mensaje cuando no hay término de búsqueda
            if (terminoBusqueda.isEmpty() && productosEncontrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Últimos productos agregados",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        if (estaBuscando) {
                            CircularProgressIndicator()
                        } else {
                            androidx.compose.material3.Text(
                                text = "Busca un producto o espera a que se carguen los últimos productos",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
            // Lista de resultados
            else if (productosEncontrados.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productosEncontrados) { producto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    onProductoSeleccionado(producto)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                androidx.compose.material3.Text(
                                    text = producto.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ChocolateDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                androidx.compose.material3.Text(
                                    text = "ID: ${producto.id} | Stock: ${producto.cantidad}",
                                    fontSize = 14.sp,
                                    color = ChocolateMedium
                                )
                            }
                        }
                    }
                }
            } else if (terminoBusqueda.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "No se encontraron productos",
                        color = Color.Gray
                    )
                }
            }
        }
        
        // Botón Volver
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
            androidx.compose.material3.Text(
                text = "Volver",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
