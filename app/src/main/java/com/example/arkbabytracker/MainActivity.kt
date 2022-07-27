package com.example.arkbabytracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.arkbabytracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var appBarConfiguration:AppBarConfiguration

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(navController.currentDestination?.id == item.itemId)
            return true
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController


        appBarConfiguration = AppBarConfiguration(setOf(R.id.babyTroughFragment,R.id.dino_stats_fragment,R.id.troughFragment),binding.drawerLayout)
        setSupportActionBar(binding.toolbar)
        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController,appBarConfiguration)

    }
}