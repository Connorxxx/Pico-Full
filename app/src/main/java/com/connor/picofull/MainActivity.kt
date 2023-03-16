package com.connor.picofull

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.connor.picofull.databinding.ActivityMainBinding
import com.vi.vioserial.NormalSerial
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbarMain.title = when (destination.id) {
                R.id.homeFragment -> getString(R.string.app_name)
                R.id.settingsFragment -> getString(R.string.settings)
                R.id.videoFragment -> getString(R.string.video)
                R.id.aboutFragment -> getString(R.string.about)
                else -> getString(R.string.app_name)
            }
            binding.imgBack.isVisible = when (destination.id) {
                R.id.homeFragment -> false
                else -> true
            }
        }
        binding.rgMain.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.radio_settings -> navController.navigate(R.id.action_global_settingsFragment)
                R.id.radio_video -> navController.navigate(R.id.action_global_videoFragment)
                R.id.radio_about -> navController.navigate(R.id.action_global_aboutFragment)
            }
        }
        binding.imgBack.setOnClickListener {
            gotoHome()
        }
        onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id == R.id.homeFragment) finish()
            else gotoHome()
        }
    }

    private fun gotoHome() {
        binding.rgMain.clearCheck()
        navController.navigate(R.id.action_global_homeFragment)
    }


    companion object {
        private const val TAG = "PICO_FULL_LOG"
    }
}