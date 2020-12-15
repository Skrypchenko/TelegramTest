package com.skrypchenko.telegramtest.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.drinkless.td.libcore.telegram.TdApi;

public class TdMessageObj implements Parcelable {

    public static final int OUTGOING = 0;
    public static final int INCOMING = 1;
    public int type;

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }


    private String message;
    private long id;
    private int date;
    private boolean sendingState;

    public TdMessageObj(TdApi.Message chat){
        id = chat.id;
        type = chat.isOutgoing?OUTGOING:INCOMING;
        date = chat.date;
        sendingState = chat.sendingState==null?true:false;

        if(chat.content instanceof TdApi.MessageText){
            TdApi.MessageText content = (TdApi.MessageText) chat.content;
            message = content.text.text;
            Log.e("TdApi.Message",chat.id+" "+content.text.text);
        }
        if(chat.content instanceof TdApi.MessageVideo){
            TdApi.MessageVideo content = (TdApi.MessageVideo) chat.content;
            message = content.caption.text;
        }
    }

    public boolean isSendingState() {
        return sendingState;
    }

    public int getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.message);
        dest.writeLong(this.id);
    }

    protected TdMessageObj(Parcel in) {
        this.type = in.readInt();
        this.message = in.readString();
        this.id = in.readLong();
    }

    public static final Creator<TdMessageObj> CREATOR = new Creator<TdMessageObj>() {
        @Override
        public TdMessageObj createFromParcel(Parcel source) {
            return new TdMessageObj(source);
        }

        @Override
        public TdMessageObj[] newArray(int size) {
            return new TdMessageObj[size];
        }
    };
}
