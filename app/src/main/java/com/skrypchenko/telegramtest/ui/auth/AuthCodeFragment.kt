package com.skrypchenko.telegramtest.ui.auth

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.skrypchenko.telegramtest.vm.MainViewModel
import com.skrypchenko.telegramtest.R
import com.skrypchenko.telegramtest.vm.ViewModelFactory

import kotlinx.android.synthetic.main.auth_code_fragment.*
import kotlinx.android.synthetic.main.auth_phone_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

class AuthCodeFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()


    private lateinit var viewModel: MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.auth_code_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(-1)
        ).get(MainViewModel::class.java)
        viewModel.init()
        idVarifiCode.setOnClickListener {
            if(TextUtils.isDigitsOnly(idCodeEdt.text)){
               // val action = AuthPhoneFragmentDirections.actionAddEditRecipeFragmentToAddStepRecipeFragment();
             //   Navigation.findNavController(view!!).navigate(action)
                viewModel.sendAuthCode(idCodeEdt.text.toString())
            }else{
                Toast.makeText(context,"Phone number is not valid",Toast.LENGTH_SHORT).show();
            }
        }
        idSendViaSms.setOnClickListener {
            viewModel.resendCodeToPhoneBySms()
        }
    }

    fun switchToHomeFragment(){
        val action =
            AuthCodeFragmentDirections.actionAuthCodeFragmentToHomeFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }


}




