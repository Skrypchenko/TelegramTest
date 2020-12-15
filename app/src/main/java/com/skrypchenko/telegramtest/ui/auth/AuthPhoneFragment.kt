package com.skrypchenko.telegramtest.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skrypchenko.telegramtest.*
import com.skrypchenko.telegramtest.vm.MainViewModel
import com.skrypchenko.telegramtest.vm.ViewModelFactory
import kotlinx.android.synthetic.main.auth_phone_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

class AuthPhoneFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()

    private lateinit var viewModel: MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.auth_phone_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(-1)
        ).get(MainViewModel::class.java)
        viewModel.init()
        idSendCode.setOnClickListener {
            if(isValidPhone(idPhoneEdt.text)){
                viewModel.sendCodeToPhone(idPhoneEdt.text.toString())
//                val action = AuthPhoneFragmentDirections.actionAuthPhoneFragmentToAuthCodeFragment()
//                Navigation.findNavController(requireView()).navigate(action)
            }else{
                Toast.makeText(context,"Phone number is not valid",Toast.LENGTH_SHORT).show();
            }
        }
    }




    fun isValidPhone(phone: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(phone)) {
            false
        } else {
            Patterns.PHONE.matcher(phone).matches()
        }
    }

}