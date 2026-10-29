package com.example.employmentsystemapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.employmentsystemapp.data.AppDatabase
import com.example.employmentsystemapp.data.EmployeeRepository
import com.example.employmentsystemapp.ui.EmployeeScreen
import com.example.employmentsystemapp.ui.theme.EmploymentSystemAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = EmployeeRepository(database.employeeDao())
        setContent {
            EmploymentSystemAppTheme {
                EmployeeScreen(repository = repository)
            }
        }
    }
}
