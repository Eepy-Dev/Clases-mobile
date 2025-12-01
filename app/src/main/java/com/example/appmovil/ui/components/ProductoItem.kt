package com.example.appmovil.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium

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
            containerColor = androidx.compose.ui.graphics.Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del producto
            if (producto.foto != null && producto.foto.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = producto.foto,
                        contentDescription = producto.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
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
}

