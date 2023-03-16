package com.connor.picofull

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.connor.picofull.constant.*
import com.connor.picofull.databinding.ActivityMainBinding
import com.connor.picofull.utils.logCat
import com.connor.picofull.viewmodels.MainViewModel
import com.vi.vioserial.NormalSerial
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val viewModel by viewModels<MainViewModel>()

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
                R.id.radio_settings -> {
                    if (binding.radioSettings.isChecked) {
                        navController.navigate(R.id.action_global_settingsFragment)
                        viewModel.sendHex(UPLOAD_SETTINGS)
                    }
                }
                R.id.radio_video -> {
                    if (binding.radioVideo.isChecked) {
                        navController.navigate(R.id.action_global_videoFragment)
                        viewModel.sendHex(UPLOAD_VIDEO)
                    }
                }
                R.id.radio_about -> {
                    if (binding.radioAbout.isChecked) {
                        navController.navigate(R.id.action_global_aboutFragment)
                        viewModel.sendHex(UPLOAD_MSG)
                    }
                }
            }
        }
        binding.imgBack.setOnClickListener {
            gotoHome()
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.receiveEvent.collect {
                        when (it) {
                            GOTO_SETTINGS, GOTO_SETTINGS.uppercase() -> {
                                navController.navigate(R.id.action_global_settingsFragment)
                                binding.radioSettings.isChecked = true
                            }
                            GOTO_VIDEO, GOTO_VIDEO.uppercase() -> {
                                navController.navigate(R.id.action_global_videoFragment)
                                binding.radioSettings.isChecked = true
                            }
                            GOTO_ABOUT, GOTO_ABOUT.uppercase() -> {
                                navController.navigate(R.id.action_global_aboutFragment)
                                binding.radioAbout.isChecked = true
                            }
                        }
                    }
                }
            }
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