package com.example.unishare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.unishare.ui.navigation.AppNavHost
import com.example.unishare.ui.theme.UniShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as UniShareApplication
        setContent {
            UniShareTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(application = app)
                }
            }
        }
    }
}
