package com.skrypchenko.telegramtest.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.skrypchenko.telegramtest.R
import com.skrypchenko.telegramtest.data.TdChatObj
import com.skrypchenko.telegramtest.ui.chat.ChatFragment
import com.skrypchenko.telegramtest.vm.MainViewModel
import com.skrypchenko.telegramtest.vm.ViewModelFactory
import kotlinx.android.synthetic.main.home_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import java.util.*

class HomeFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()


    var adapter: GroupsAdapter? = null
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(-1)
        ).get(MainViewModel::class.java)
        viewModel.init()
        idLogOut.setOnClickListener {
            viewModel.onLogOut();
        }

        idRecyclerView.layoutManager = LinearLayoutManager(context);
        adapter = GroupsAdapter();
        idRecyclerView.adapter = adapter
        adapter?.setOnItemClickListener(GroupsAdapter.OnItemClickListener {
            Log.e("VVV",""+it.id)
            val action = HomeFragmentDirections.actionHomeFragmentToChatFragment().apply {
                   // listId = 0
                  tdChatObj = it;
                }
                Navigation.findNavController(requireView()).navigate(action)

        })

        viewModel.mainChatList.observe(viewLifecycleOwner, object : Observer<ArrayList<TdChatObj>?> {
            override fun onChanged(list: ArrayList<TdChatObj>?) {
                adapter!!.updateListData(list)
                Log.e("VVV",""+list?.size)
            }
        })



    }
}