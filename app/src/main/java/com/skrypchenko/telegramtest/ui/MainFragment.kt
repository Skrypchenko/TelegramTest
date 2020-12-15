package com.skrypchenko.telegramtest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.skrypchenko.telegramtest.vm.MainViewModel
import com.skrypchenko.telegramtest.R
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein


class MainFragment : Fragment() , KodeinAware  {
    override val kodein by closestKodein()


    private lateinit var viewModel: MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {}
}