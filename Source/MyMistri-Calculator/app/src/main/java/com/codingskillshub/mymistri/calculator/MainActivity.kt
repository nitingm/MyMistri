package com.codingskillshub.mymistri.calculator

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.codingskillshub.mymistri.calculator.ui.home.HomeScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.codingskillshub.mymistri.calculator.ui.home.HomeScreen
import com.codingskillshub.mymistri.calculator.ui.theme.AppTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                HomeScreen()
            }
        }
    }
}