package com.studentdetails

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.studentdetails.databinding.ActivityDashboardScreenBinding

class DashboardScreen : AppCompatActivity() {

   private lateinit var binding: ActivityDashboardScreenBinding
   private lateinit var navHostController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityDashboardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentDashboard.id) as NavHostFragment
        navHostController = navHostFragment.navController
    }
    override fun onSupportNavigateUp(): Boolean {
        return navHostController.navigateUp() || super.onSupportNavigateUp()
    }
}