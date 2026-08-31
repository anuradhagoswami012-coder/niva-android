package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.NivaApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NivaViewModel
import com.example.ui.viewmodel.NivaViewModelFactory

class MainActivity : ComponentActivity() {

    private val nivaViewModel: NivaViewModel by viewModels {
        NivaViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NivaApp(viewModel = nivaViewModel)
            }
        }
    }
}

