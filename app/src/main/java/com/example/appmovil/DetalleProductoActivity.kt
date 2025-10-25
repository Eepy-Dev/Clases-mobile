package com.example.appmovil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import java.io.File

class DetalleProductoActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val producto = intent.getSerializableExtra("producto") as? Producto
        
        if (producto == null) {
            Toast.makeText(this, "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setContent {
            AppMovilTheme {
                DetalleProductoScreen(
                    producto = producto,
                    onVolverClick = { finish() },
                    onCompartirClick = { producto ->
                        compartirProducto(producto)
                    }
                )
            }
        }
    }
    
    private fun compartirProducto(producto: Producto) {
        val mensaje = """
            🍫 Detalles del Producto:
            
            📋 ID: ${producto.id}
            🏷️ Nombre: ${producto.nombre}
            📝 Descripción: ${producto.descripcion}
            💰 Precio: $${producto.precio}
            📦 Stock: ${producto.cantidad}
        """.trimIndent()
        
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, mensaje)
            startActivity(intent)
        } catch (e: Exception) {
            // Si WhatsApp no está instalado, usar cualquier app de mensajería
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, mensaje)
            startActivity(Intent.createChooser(intent, "Compartir producto"))
        }
    }
}

@Composable
fun DetalleProductoScreen(
    producto: Producto,
    onVolverClick: () -> Unit,
    onCompartirClick: (Producto) -> Unit
) {
    var imagenBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Cargar imagen si existe
    LaunchedEffect(producto.foto) {
        if (producto.foto != null) {
            val bitmap = cargarImagen(producto.foto)
            if (bitmap != null) {
                imagenBitmap = bitmap
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
    ) {
        // Título
        Text(
            text = "Detalles del Producto",
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(ChocolateMedium)
                .padding(24.dp)
        )
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del producto
            if (imagenBitmap != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Image(
                        bitmap = imagenBitmap!!.asImageBitmap(),
                        contentDescription = "Foto del producto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.appmovil.ui.theme.CreamDark)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = "Sin imagen",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
            
            // Información del producto
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ID:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = producto.id,
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Nombre
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Nombre:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = producto.nombre,
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Descripción
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Descripción:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = producto.descripcion,
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Precio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Precio:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = "$${producto.precio}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Cantidad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Stock:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = "${producto.cantidad} unidades",
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                }
            }
            
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Volver
                Button(
                    onClick = onVolverClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
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
                
                // Botón Compartir
                Button(
                    onClick = { onCompartirClick(producto) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.appmovil.ui.theme.ChocolateLight
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Compartir",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Espacio adicional al final
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun cargarImagen(ruta: String): Bitmap? {
    val archivo = File(ruta)
    return if (archivo.exists()) {
        BitmapFactory.decodeFile(ruta)
    } else {
        null
    }
}
