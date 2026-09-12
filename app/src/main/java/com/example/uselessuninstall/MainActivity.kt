package com.example.uselessuninstall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.uselessuninstall.ui.RandomUninstallerApp
import com.example.uselessuninstall.ui.RandomUninstallerViewModel
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = RandomUninstallerViewModel.Factory(applicationContext)
        val viewModel = ViewModelProvider(this, factory)[RandomUninstallerViewModel::class.java]

        setContent {
            UselessUninstallTheme {
                RandomUninstallerApp(viewModel = viewModel)
            }
        }
    }
}