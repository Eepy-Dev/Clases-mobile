package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream

class HomeActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            AppMovilTheme {
                HomeScreen(
                    onProductosClick = {
                        val intent = Intent(this, ProductosActivity::class.java)
                        startActivity(intent)
                    },
                    onConsultaClick = {
                        val intent = Intent(this, ConsultaActivity::class.java)
                        startActivity(intent)
                    },
                    onIngresoClick = {
                        val intent = Intent(this, IngresoActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    onProductosClick: () -> Unit,
    onConsultaClick: () -> Unit,
    onIngresoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logos),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(254.dp)
                    .height(337.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Productos
            Button(
                onClick = onProductosClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Productos",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Consulta
            Button(
                onClick = onConsultaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Consulta",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Ingreso
            Button(
                onClick = onIngresoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Ingreso",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
