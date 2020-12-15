package com.skrypchenko.telegramtest.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skrypchenko.telegramtest.R
import com.skrypchenko.telegramtest.data.ChatHistoryEvent
import com.skrypchenko.telegramtest.data.TdChatObj
import com.skrypchenko.telegramtest.data.TdMessageObj
import com.skrypchenko.telegramtest.vm.MainViewModel
import com.skrypchenko.telegramtest.vm.ViewModelFactory
import kotlinx.android.synthetic.main.chat_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import java.util.*


class ChatFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()


    var messages: ArrayList<TdMessageObj> = ArrayList<TdMessageObj>()
    var adapter: ChatAdapter? = null;
    var layoutManager: LinearLayoutManager? = null;
    private lateinit var viewModel: MainViewModel
    var tdChatObj: TdChatObj? = null;
    var isLoading: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    private fun extractSafeArgs(safeArgs: ChatFragmentArgs?) {
        tdChatObj = safeArgs?.tdChatObj;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        extractSafeArgs(arguments?.let { ChatFragmentArgs.fromBundle(it) })
        viewModel = ViewModelProvider(this, ViewModelFactory(-1)).get(MainViewModel::class.java)
        viewModel.init()
        layoutManager = LinearLayoutManager(context)
        layoutManager!!.setReverseLayout(true);

        messages.clear()
        Log.e("CLEAR ALL", "");
        idListMessages.layoutManager = layoutManager
        adapter = ChatAdapter();
        idListMessages.adapter = adapter
        idListMessages.addOnScrollListener(scrollListener);

        messages.add(tdChatObj!!.lastMessage)
        adapter!!.updateListData(messages)


        fabSend.setOnClickListener {
            if (!etMessage.text.isEmpty()) {
                viewModel.sendTmMessage(tdChatObj!!.id, etMessage.text.toString());
                etMessage.text.clear()
            }
        }

        viewModel.updateChatLastMessage().observe(viewLifecycleOwner, object : Observer<TdChatObj> {
            override fun onChanged(tdChat: TdChatObj?) {
                if (tdChat!!.getId() == tdChatObj!!.id) {
                    if (tdChat!!.lastMessage.isSendingState && canAddMessage(
                            messages,
                            tdChat!!.lastMessage
                        )
                    ) {
                        Log.e("STDCHECK", "")
                        messages.add(0, tdChat!!.lastMessage)
                        adapter!!.updateListData(messages)
                    }
                }
            }
        })

        swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                if(messages.size>0){
                     viewModel.updateListMessagesByGroup(tdChatObj!!.getId(), messages.last().id);
                }else{
                    swiperefresh.setRefreshing(false);
                }
            }
        });
    }


    var scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val visibleItemCount: Int =
                layoutManager!!.getChildCount() //смотрим сколько элементов на экране
            val totalItemCount: Int = layoutManager!!.getItemCount() //сколько всего элементов
            val firstVisibleItems: Int =
                layoutManager!!.findFirstVisibleItemPosition() //какая позиция первого элемента
            if (!isLoading) { //проверяем, грузим мы что-то или нет, эта переменная должна быть вне класса  OnScrollListener
                if (visibleItemCount + firstVisibleItems >= totalItemCount) {
                    isLoading = true //ставим флаг что мы попросили еще элемены
                    if(messages.size>0){
                        viewModel.updateListMessagesByGroup(tdChatObj!!.getId(), messages.last().id);
                    }else{
                        swiperefresh.setRefreshing(false);
                    }
                    Log.e("VVVZLoadMore", "LoadMore id " + messages.last().id)
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ChatHistoryEvent?) { /* Do something */
        isLoading = false
        messages.addAll(check(messages, event?.messageObjs!!))
        adapter!!.updateListData(messages)
        swiperefresh.setRefreshing(false);
    }

    fun check(
        fullList: ArrayList<TdMessageObj>,
        inputList: ArrayList<TdMessageObj>
    ): ArrayList<TdMessageObj> {
        var result: ArrayList<TdMessageObj> = ArrayList()
        for (input in inputList) {
            if (input.isSendingState && canAddMessage(fullList, input)) {
                result.add(input)
            }
        }
        return result
    }


    fun canAddMessage(list: ArrayList<TdMessageObj>, message: TdMessageObj): Boolean {
        for (item in list) {
            if (item.id == message.id) {
                return false
            }
        }
        return true;
    }


}