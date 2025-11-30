package com.example.appmovil.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.example.appmovil.data.MovimientoInventario
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistorialScreen(
    onVolverClick: () -> Unit,
    onCompartirHistorial: () -> Unit,
    productoViewModel: ProductoViewModel
) {
    var movimientos by remember { mutableStateOf<List<MovimientoInventario>>(emptyList()) }
    var tipoFiltro by remember { mutableStateOf("Todos") }
    
    val movimientosLiveData = productoViewModel.getAllMovimientos()
    
    DisposableEffect(Unit) {
        val observer = androidx.lifecycle.Observer<List<MovimientoInventario>> { lista ->
            movimientos = lista ?: emptyList()
        }
        movimientosLiveData.observeForever(observer)
        onDispose {
            movimientosLiveData.removeObserver(observer)
        }
    }
    
    // Animaciones
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Aplicar filtro
    val movimientosFiltrados = when (tipoFiltro) {
        "ENTRADA" -> movimientos.filter { it.tipo == "ENTRADA" }
        "SALIDA" -> movimientos.filter { it.tipo == "SALIDA" }
        "VENTA" -> movimientos.filter { it.tipo == "VENTA" }
        else -> movimientos
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
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(600, delayMillis = 100)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 100)
                ),
                exit = ExitTransition.None
            ) {
                Text(
                    text = "Historial de Movimientos",
                    fontSize = 24.sp,
                    color = ChocolateDark,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChocolateMedium)
                        .padding(24.dp)
                )
            }
            
            // Botón Compartir y Hint
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(400, delayMillis = 300)
                ),
                exit = ExitTransition.None
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onCompartirHistorial,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChocolateMedium
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Compartir Historial",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ℹ️ Comparte los últimos 5 movimientos por WhatsApp",
                        fontSize = 12.sp,
                        color = ChocolateDark,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Filtros
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400, delayMillis = 400)
                ) + fadeIn(
                    animationSpec = tween(400, delayMillis = 400)
                ),
                exit = ExitTransition.None
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tipos = listOf("Todos", "ENTRADA", "SALIDA", "VENTA")
                    tipos.forEach { tipo ->
                        FilterChip(
                            selected = tipoFiltro == tipo,
                            onClick = { tipoFiltro = tipo },
                            label = { Text(tipo, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ChocolateMedium,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
            
            // Lista de movimientos con padding inferior para el botón
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(600, delayMillis = 500)
                ),
                exit = ExitTransition.None
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (movimientosFiltrados.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "No hay movimientos registrados",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = ChocolateMedium
                                )
                            }
                        }
                    } else {
                        items(
                            items = movimientosFiltrados,
                            key = { movimiento -> movimiento.id }
                        ) { movimiento ->
                            MovimientoItem(movimiento = movimiento)
                        }
                    }
                }
            }
        }
        
        // Botón Volver fijo en la parte inferior
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(400, delayMillis = 600)
            ),
            exit = ExitTransition.None,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Button(
                onClick = onVolverClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
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
}

@Composable
fun MovimientoItem(movimiento: MovimientoInventario) {
    val fechaFormateada = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        .format(Date(movimiento.fecha))
    
    val iconoEmoji = when (movimiento.tipo) {
        "ENTRADA" -> "➕"
        "SALIDA" -> "➖"
        "VENTA" -> "💰"
        else -> "📦"
    }
    
    val colorTipo = when (movimiento.tipo) {
        "ENTRADA" -> Color(0xFF4CAF50) // Verde
        "SALIDA" -> Color(0xFFFF9800) // Naranja
        "VENTA" -> Color(0xFF2196F3) // Azul
        else -> ChocolateMedium
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$iconoEmoji ${movimiento.tipo}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTipo
                )
                Text(
                    text = fechaFormateada,
                    fontSize = 12.sp,
                    color = ChocolateDark
                )
            }
            
            Divider(color = ChocolateMedium, thickness = 1.dp)
            
            Text(
                text = movimiento.nombreProducto,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateDark
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cantidad: ${if (movimiento.tipo == "ENTRADA") "+" else "-"}${movimiento.cantidad}",
                        fontSize = 14.sp,
                        color = ChocolateDark
                    )
                    Text(
                        text = "Stock: ${movimiento.cantidadAnterior} → ${movimiento.cantidadNueva}",
                        fontSize = 14.sp,
                        color = ChocolateMedium
                    )
                }
            }
        }
    }
}

