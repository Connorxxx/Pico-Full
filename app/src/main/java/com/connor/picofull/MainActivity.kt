package com.connor.picofull

import android.Manifest
import android.os.Bundle
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
import com.connor.picofull.ui.dialog.AlertDialogFragment
import com.connor.picofull.utils.logCat
import com.connor.picofull.viewmodels.MainViewModel
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val viewModel by viewModels<MainViewModel>()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        PermissionX.init(this).permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).request { allGranted, _, _ ->
            if (allGranted) File(videoPath).also { if (!it.exists()) it.mkdirs() }
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbarMain.title = when (destination.id) {
                R.id.homeFragment -> getString(R.string.app_name)
                R.id.settingsFragment -> getString(R.string.settings)
                R.id.videoFragment -> getString(R.string.video)
                R.id.aboutFragment -> getString(R.string.about)
                R.id.inputFragment -> getString(R.string.input)
                R.id.backstageFragment -> getString(R.string.backstage)
                R.id.tempAlertFragment,
                R.id.flowAlertFragment,
                R.id.xenonAlertFragment,
                R.id.powerAlertFragment -> getString(R.string.alert)
                else -> getString(R.string.app_name)
            }
            binding.imgBack.isVisible = when (destination.id) {
                R.id.homeFragment -> false
                else -> true
            }
            binding.rgMain.isVisible = when (destination.id) {
                R.id.playVideoFragment, R.id.inputFragment, R.id.backstageFragment -> false
                else -> true
            }
            sandVisible(
                when (destination.id) {
                    R.id.aboutFragment -> false
                    else -> true
                }
            )
            binding.backstage.isVisible = when (destination.id) {
                R.id.homeFragment -> true
                else -> false
            }
            binding.layoutAlert.isVisible = when (destination.id) {
                R.id.tempAlertFragment,
                R.id.flowAlertFragment,
                R.id.xenonAlertFragment,
                R.id.powerAlertFragment -> true
                else -> false
            }
            binding.layoutHome.isVisible = !binding.layoutAlert.isVisible
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
        binding.backstage.setOnClickListener {
            navController.navigate(R.id.action_global_inputFragment)
        }
        binding.imgBack.setOnClickListener {
            if (navController.currentDestination?.id == R.id.playVideoFragment) {
                navController.navigate(R.id.action_playVideoFragment_to_videoFragment)
            } else gotoHome()
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.receiveEvent.collect {
                        it.logCat()
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
                            GOTO_TEMP_ALARM, GOTO_TEMP_ALARM.uppercase() -> {
                                navController.navigate(R.id.action_global_tempAlertFragment)
                                binding.radioHighTemp.isChecked = true
                            }
                            GOTO_FLOW_ERR, GOTO_FLOW_ERR.uppercase() -> {
                                navController.navigate(R.id.action_global_flowAlertFragment)
                                binding.radioFlowErr.isChecked = true
                            }
                            GOTO_XENON_LAMP_FAIL, GOTO_XENON_LAMP_FAIL.uppercase() -> {
                                navController.navigate(R.id.action_global_xenonAlertFragment)
                                binding.radioXenonFailure.isChecked = true
                            }
                            GOTO_LASER_POWER_FAIL, GOTO_LASER_POWER_FAIL.uppercase() -> {
                                navController.navigate(R.id.action_global_powerAlertFragment)
                                binding.radioPowerErr.isChecked = true
                            }
                        }
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id == R.id.homeFragment) AlertDialogFragment(
                title = getString(R.string.exit_tips),
                msg = getString(R.string.exit_msg)
            ) { finish() }.show(supportFragmentManager, AlertDialogFragment.TAG)
            else gotoHome()
        }
    }

    private fun sandVisible(visible: Boolean) {
        binding.radioSettings.isVisible = visible
        binding.radioVideo.isVisible = visible
    }

    private fun gotoHome() {
        binding.rgMain.clearCheck()
        navController.navigate(R.id.action_global_homeFragment)
    }


    companion object {
        private const val TAG = "PICO_FULL_LOG"
    }
}