package com.skrypchenko.telegramtest.data;

import com.skrypchenko.telegramtest.data.TdMessageObj;

import java.util.ArrayList;

public class ChatHistoryEvent {
    ArrayList<TdMessageObj> messageObjs;
    public ChatHistoryEvent(ArrayList<TdMessageObj> messageObjs){
        this.messageObjs=messageObjs;
    }

    public ArrayList<TdMessageObj> getMessageObjs() {
        return messageObjs;
    }
}
