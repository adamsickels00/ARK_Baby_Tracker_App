package com.example.arkbabytracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.arkbabytracker.databinding.ActivityMainBinding


// TODO add a menu to different apps (eg. colors tracker)


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    private lateinit var navController: NavController

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.apps_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.turtleMenuItem -> {
                navController.navigate(R.id.turtleMenu)
                true
            }
            R.id.babyTrackerMenuItem -> {
                navController.navigate(R.id.babyTroughFragment)
                true
            }
            else-> super.onOptionsItemSelected(item)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

    }
}