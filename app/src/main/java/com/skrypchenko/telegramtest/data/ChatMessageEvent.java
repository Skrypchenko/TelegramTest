package com.skrypchenko.telegramtest.data;

import com.skrypchenko.telegramtest.data.TdChatObj;

public class ChatMessageEvent {
    private TdChatObj tdChatObj;
    public ChatMessageEvent(TdChatObj tdChatObj){this.tdChatObj=tdChatObj;}

    public TdChatObj getTdChatObj() {
        return tdChatObj;
    }
}
