package com.samchendev.blecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.samchendev.blecompose.navigation.NavDisplay
import com.samchendev.blecompose.ui.theme.BLEComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLEComposeTheme {
                NavDisplay()
            }
        }
    }
}