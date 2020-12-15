package com.skrypchenko.telegramtest.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skrypchenko.telegramtest.data.AuthState;
import com.skrypchenko.telegramtest.data.TdChatObj;
import com.skrypchenko.telegramtest.data.TdMessageObj;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {

//    public MutableLiveData<String> responseval;
//    public MutableLiveData<String> getResponseval() {
//        return responseval;
//    }
    //  responseval = authRepositry.getData("username","password");


    public MutableLiveData<AuthState> authStateData;
    private AuthRepositry authRepositry;
    public void init() {
        authRepositry =  AuthRepositry.getInstance();
        authStateData = authRepositry.getAuthStateData();
    }

    public MutableLiveData<AuthState> getAuthState(){
        return authStateData;
    }

    public void onLogOut(){
        authRepositry.onLogOut();
    }
    public void sendCodeToPhone(String phone) {
        authRepositry.sendCodeToPhone(phone);
    }
    public void sendPasswordTxt(String password){authRepositry.sendPasswordTxt(password);}
    public void resendCodeToPhoneBySms(){
        authRepositry.resendCodeToPhoneBySms();
    }
    public void sendAuthCode(String code) {
        authRepositry.sendAuthCode(code);
    }


    public MutableLiveData<ArrayList<TdChatObj>> getMainChatList(){
        return  authRepositry.getMainChatList();
    }

    public void sendTmMessage(long chatId, String message){
        authRepositry.sendTmMessage(chatId,message);
    }



    public void updateListMessagesByGroup(long chatId, long lastMessageId){
         authRepositry.updateListMessagesByGroup(chatId, lastMessageId);
    }


    public MutableLiveData<TdChatObj> updateChatLastMessage(){
        return authRepositry.getChatLastMessage();
    }


}
