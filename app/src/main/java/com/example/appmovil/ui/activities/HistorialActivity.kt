package com.example.appmovil.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.data.local.entity.MovimientoInventario
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistorialActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                HistorialScreen(
                    onVolverClick = { finish() },
                    onCompartirWhatsApp = { movimientos ->
                        compartirPorWhatsApp(movimientos)
                    },
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun compartirPorWhatsApp(movimientos: List<MovimientoInventario>) {
        if (movimientos.isEmpty()) {
            return
        }
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val texto = StringBuilder()
        texto.append("📊 Últimos 5 Movimientos de Inventario\n\n")
        
        movimientos.forEachIndexed { index, movimiento ->
            val fecha = sdf.format(Date(movimiento.fecha))
            val tipoEmoji = when {
                movimiento.tipo == "ENTRADA" -> "⬆️"
                movimiento.tipo.startsWith("SALIDA") -> "⬇️"
                else -> "📝"
            }
            
            texto.append("${index + 1}. $tipoEmoji ${movimiento.nombreProducto}\n")
            texto.append("   Tipo: ${movimiento.tipo}\n")
            texto.append("   Cantidad: ${movimiento.cantidad}\n")
            texto.append("   Stock: ${movimiento.stockAnterior} → ${movimiento.stockNuevo}\n")
            texto.append("   Fecha: $fecha\n\n")
        }
        
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, texto.toString())
            
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Si WhatsApp no está instalado, compartir con cualquier app
                val intentGenerico = Intent(Intent.ACTION_SEND)
                intentGenerico.type = "text/plain"
                intentGenerico.putExtra(Intent.EXTRA_TEXT, texto.toString())
                startActivity(Intent.createChooser(intentGenerico, "Compartir movimientos"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun HistorialScreen(
    onVolverClick: () -> Unit,
    onCompartirWhatsApp: (List<MovimientoInventario>) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var movimientos by remember { mutableStateOf<List<MovimientoInventario>>(emptyList()) }
    var filtroTipo by remember { mutableStateOf("TODOS") }
    
    LaunchedEffect(Unit) {
        productoViewModel.obtenerMovimientos().fold(
            onSuccess = { movimientos = it },
            onFailure = {}
        )
    }
    
    val movimientosFiltrados = when (filtroTipo) {
        "ENTRADA" -> movimientos.filter { it.tipo == "ENTRADA" }
        "SALIDA" -> movimientos.filter { it.tipo.startsWith("SALIDA") }
        else -> movimientos
    }
    
    val ultimos5Movimientos = movimientosFiltrados.take(5).sortedByDescending { it.fecha }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial de Movimientos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateDark
                )
                
                // Botón compartir
                if (ultimos5Movimientos.isNotEmpty()) {
                    IconButton(
                        onClick = { onCompartirWhatsApp(ultimos5Movimientos) }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir por WhatsApp",
                            tint = ChocolateMedium
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                FilterChip(
                    selected = filtroTipo == "TODOS",
                    onClick = { filtroTipo = "TODOS" },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = filtroTipo == "ENTRADA",
                    onClick = { filtroTipo = "ENTRADA" },
                    label = { Text("Ingreso") }
                )
                FilterChip(
                    selected = filtroTipo == "SALIDA",
                    onClick = { filtroTipo = "SALIDA" },
                    label = { Text("Salida") }
                )
            }
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movimientosFiltrados) { movimiento ->
                    MovimientoItem(movimiento = movimiento)
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
fun MovimientoItem(movimiento: MovimientoInventario) {
    val fechaFormateada = remember {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(movimiento.fecha))
    }
    
    val colorTipo = when {
        movimiento.tipo == "ENTRADA" -> Color(0xFF4CAF50)
        movimiento.tipo.startsWith("SALIDA") -> Color(0xFFF44336)
        else -> Color.Gray
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = movimiento.nombreProducto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = movimiento.tipo,
                color = colorTipo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Cantidad: ${movimiento.cantidad}")
            Text("Stock anterior: ${movimiento.stockAnterior} → Stock nuevo: ${movimiento.stockNuevo}")
            Text(
                text = fechaFormateada,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
