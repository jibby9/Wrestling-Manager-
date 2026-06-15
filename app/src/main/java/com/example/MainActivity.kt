package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.GameDatabase
import com.example.repository.GameRepository
import com.example.ui.screens.MainSimulationApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var database: GameDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup Edge-To-Edge
        enableEdgeToEdge()

        // Room Database Setup
        database = GameDatabase.getInstance(applicationContext)

        val repository = GameRepository(database, applicationContext)
        val factory = GameViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Inner padding ignored inside MainSimulationApp to support edge-to-edge bleed designs
                    // as MainSimulationApp handles safeDrawing insets appropriately
                    MainSimulationApp(viewModel = viewModel)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
