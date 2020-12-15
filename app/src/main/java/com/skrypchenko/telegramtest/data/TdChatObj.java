package com.skrypchenko.telegramtest.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.drinkless.td.libcore.telegram.TdApi;

public class TdChatObj implements Parcelable {
    private String title;
    private long id;

    private TdMessageObj lastMessage;
    public TdChatObj(TdApi.Chat chat){
        id = chat.id;
        title = chat.title;
        lastMessage = new TdMessageObj(chat.lastMessage);
    }

    public TdMessageObj getLastMessage() {
        return lastMessage;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.id);
        dest.writeParcelable(this.lastMessage, flags);
    }

    protected TdChatObj(Parcel in) {
        this.title = in.readString();
        this.id = in.readLong();
        this.lastMessage = in.readParcelable(TdMessageObj.class.getClassLoader());
    }

    public static final Creator<TdChatObj> CREATOR = new Creator<TdChatObj>() {
        @Override
        public TdChatObj createFromParcel(Parcel source) {
            return new TdChatObj(source);
        }

        @Override
        public TdChatObj[] newArray(int size) {
            return new TdChatObj[size];
        }
    };
}
