package com.ekosoftware.financialpreview

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val TAG = "MainActivity LOG"
        var ars = ""
        Currency.getAvailableCurrencies().toList().sortedBy { it.currencyCode }.forEach {
            println("${it.displayName} -> ${it.currencyCode}\n")
            if (it.displayName == "peso argentino") {
                println("\n\n\n peso argentinooo papaaaa => $it ${it.symbol}\n\n\n")
                ars = it.toString()
            }
        }

        val c = Currency.getInstance(ars)*/

      /*  supportActionBar?.apply {
            //setDisplayHomeAsUpEnabled((destination.id in NO_HOME_ICON_FRAGMENTS).not())
            setHomeAsUpIndicator(*//*if (destination.id in arrayOf(
                        R.id.editMovementDialogFragment,
                        R.id.editBudgetDialogFragment,
                        R.id.editSettleGroupDialogFragment,
                        R.id.editAccountDialogFragment
                    )) *//*R.drawable.ic_close*//* else R.drawable.ic_back*//*
            )
        }*/


        val navView: BottomNavigationView = binding.bottomNavigationView

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.home_page_fragment, R.id.pending_page_fragment))

        //setupActionBarWithNavController(this, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        /*NavigationUI.setupWithNavController(navView, navController, appBarConfiguration)*/

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.editMovement) {
                //supportActionBar!!.setHomeAsUpIndicator( R.drawable.ic_close)
                //supportActionBar!!.title = "Add movement"
                //supportActionBar!!.show()
            } else {
                //supportActionBar!!.setHomeAsUpIndicator(0)
                if (destination.id in arrayOf(R.id.selection)) {
                   //supportActionBar!!.show()
                } else {
                    //supportActionBar!!.title = getString(R.string.app_name)
                    //supportActionBar!!.hide()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}