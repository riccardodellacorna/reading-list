package com.riccardodellacorna.readinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.riccardodellacorna.readinglist.core.navigation.AppNavHost
import com.riccardodellacorna.readinglist.core.ui.theme.ReadingListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadingListTheme {
                AppNavHost()
            }
        }
    }
}
