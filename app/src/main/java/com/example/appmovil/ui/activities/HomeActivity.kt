package com.example.appmovil.ui.activities

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
import com.example.appmovil.R
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
                    onConsultaClick = {
                        val intent = Intent(this, com.example.appmovil.ui.activities.ConsultaActivity::class.java)
                        startActivity(intent)
                    },
                    onCatalogoOnlineClick = {
                        val intent = Intent(this, com.example.appmovil.ui.activities.ProductosExternosActivity::class.java)
                        startActivity(intent)
                    },
                    onIngresoClick = {
                        val intent = Intent(this, com.example.appmovil.ui.activities.IngresoActivity::class.java)
                        startActivity(intent)
                    },
                    onSalidaClick = {
                        val intent = Intent(this, com.example.appmovil.ui.activities.SalidaActivity::class.java)
                        startActivity(intent)
                    },
                    onHistorialClick = {
                        val intent = Intent(this, com.example.appmovil.ui.activities.HistorialActivity::class.java)
                        startActivity(intent)
                    },
                    onCerrarSesionClick = {
                        val intent = Intent(this, com.example.appmovil.ui.activities.LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    onConsultaClick: () -> Unit,
    onCatalogoOnlineClick: () -> Unit,
    onIngresoClick: () -> Unit,
    onSalidaClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
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
            
            // Botón Catálogo Online
            Button(
                onClick = onCatalogoOnlineClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Catálogo Online",
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Salida
            Button(
                onClick = onSalidaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Salida",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Historial
            Button(
                onClick = onHistorialClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Historial",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Cerrar Sesión
            Button(
                onClick = onCerrarSesionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Cerrar Sesión",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

