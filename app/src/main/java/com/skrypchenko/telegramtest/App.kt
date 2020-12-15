package com.skrypchenko.telegramtest

import android.app.Application
import com.skrypchenko.telegramtest.vm.AuthRepositry
import com.skrypchenko.telegramtest.vm.ViewModelFactory

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class App: Application(), KodeinAware  {

    //di
    override val kodein = Kodein.lazy {
        import(androidXModule(this@App))

        //bind() from singleton { AuthRepositry.instance }
        bind() from factory {id: Long -> ViewModelFactory(id) }

    }


}