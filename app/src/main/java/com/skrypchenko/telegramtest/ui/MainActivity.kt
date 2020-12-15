package com.skrypchenko.telegramtest.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.skrypchenko.telegramtest.R
import com.skrypchenko.telegramtest.data.AuthState
import com.skrypchenko.telegramtest.vm.MainViewModel
import com.skrypchenko.telegramtest.vm.ViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.x.closestKodein


class MainActivity : AppCompatActivity() , KodeinAware {

    override val kodein by closestKodein()


    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.navHostFragment)
        viewModel = ViewModelProvider(this, ViewModelFactory(-1)).get(MainViewModel::class.java)
        if(isStoragePermissionGranted()){
            onInitViewModel();
        }
    }


    fun onInitViewModel(){
        viewModel.init()
        viewModel.authState?.observe(this, object : Observer<AuthState?> {
            override fun onChanged(state: AuthState?) {
                Log.e("LOGI", "AuthState")
                //navController.popBackStack();
                when(state?.getState()){
                    AuthState.PHONE ->{
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.navigation_graph,true).build()
                        navController.navigate(R.id.authPhoneFragment , null, navOptions)
                    }
                    AuthState.CODE ->{
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.navigation_graph,true).build()
                        navController.navigate(R.id.authCodeFragment, null, navOptions)
                    }
                    AuthState.HOME ->{
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.navigation_graph,true).build()
                        navController.navigate(R.id.homeFragment, null, navOptions)
                    }
                    AuthState.PASSWORD ->{
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.navigation_graph,true).build()
                        navController.navigate(R.id.authPasswordFragment, null, navOptions)
                    }
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("LOGI", "Permission is granted")
                true
            } else {
                Log.v("LOGI", "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("LOGI", "Permission is granted")
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onInitViewModel()
        }
    }
}