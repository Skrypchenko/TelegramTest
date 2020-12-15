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
import com.skrypchenko.telegramtest.R
import com.skrypchenko.telegramtest.vm.MainViewModel
import com.skrypchenko.telegramtest.vm.ViewModelFactory
import kotlinx.android.synthetic.main.auth_password_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

class AuthPasswordFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()

    private lateinit var viewModel: MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.auth_password_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(-1)
        ).get(MainViewModel::class.java)
        viewModel.init()
        idSendPassword.setOnClickListener {
            if(isValid(idPasswordEdt.text.toString())){
                viewModel.sendPasswordTxt(idPasswordEdt.text.toString())
//                val action = AuthPhoneFragmentDirections.actionAuthPhoneFragmentToAuthCodeFragment()
//                Navigation.findNavController(requireView()).navigate(action)
            }else{
                Toast.makeText(context,"Password is not valid",Toast.LENGTH_SHORT).show();
            }
        }
    }


    fun isValid(password: String): Boolean {
        //return true if and only if password:
        //1. have at least eight characters.
        //2. consists of only letters and digits.
        //3. must contain at least two digits.
        if (password.length < 8) {
            return false
        } else {
            var c: Char
            var count = 1
            for (i in 0 until password.length - 1) {
                c = password[i]
                if (!Character.isLetterOrDigit(c)) {
                    return false
                } else if (Character.isDigit(c)) {
                    count++
                    if (count < 2) {
                        return false
                    }
                }
            }
        }
        return true
    }



}