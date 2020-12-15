package com.skrypchenko.telegramtest.vm;

import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.skrypchenko.telegramtest.data.AuthState;
import com.skrypchenko.telegramtest.data.TdChatObj;
import com.skrypchenko.telegramtest.data.TdMessageObj;
import com.skrypchenko.telegramtest.data.ChatHistoryEvent;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuthRepositry {
    //    private MutableLiveData<String> data = new MutableLiveData<>();
//    public  MutableLiveData<String> getData(String username , String userpass) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() { data.setValue("Login Repo Set Value"); }}, 5000);
//        return data;
//    }
    public static AuthRepositry instance;

    public static AuthRepositry getInstance() {
        if (instance == null) {
            instance = new AuthRepositry();
        }
        return instance;
    }

    private MutableLiveData<AuthState> authStateData = new MutableLiveData<>();
    private Lock authorizationLock = new ReentrantLock();
    private Condition gotAuthorization = authorizationLock.newCondition();
    private boolean haveAuthorization = false;
    private boolean needQuit = false;
    private boolean canQuit = false;

    private TdApi.AuthorizationState authState;
    private Client client;

    public AuthRepositry() {
        client = Client.create(new UpdateHandler(), null, null);
    }

    public MutableLiveData<AuthState> getAuthStateData() {
        return authStateData;
    }


    public void onLogOut() {
        haveAuthorization = false;
        client.send(new TdApi.LogOut(), defaultHandler);
    }

    public void sendCodeToPhone(String phone) {
        client.send(new TdApi.SetAuthenticationPhoneNumber(phone, true, true), new AuthorizationRequestHandler());
    }

    public void sendPasswordTxt(String password) {
        client.send(new TdApi.CheckAuthenticationPassword(password), new AuthorizationRequestHandler());
    }

    public void resendCodeToPhoneBySms() {
        client.send(new TdApi.ResendAuthenticationCode(), new AuthorizationRequestHandler());
    }

    public void sendAuthCode(String code) {
        client.send(new TdApi.CheckAuthenticationCode(code, "", ""), new AuthorizationRequestHandler());
    }


    public void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            authState = authorizationState;
        }
        switch (authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                Log.e("LOGI", "AuthorizationStateWaitTdlibParameters");
                File file = new File(Environment.getExternalStorageDirectory(), "tdlib");
                if (!file.exists()) {
                    file.mkdir();
                }
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = file.getAbsolutePath(); //"tdlib";
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = 2850189;
                parameters.apiHash = "540769656d23a49be6dfa36ba05ff37e";
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Desktop";
                parameters.applicationVersion = "1.0";
                parameters.enableStorageOptimizer = true;
                parameters.systemVersion = "1.0";

                client.send(new TdApi.SetTdlibParameters(parameters), new AuthorizationRequestHandler());

                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                Log.e("LOGI", "AuthorizationStateWaitEncryptionKey");
                client.send(new TdApi.CheckDatabaseEncryptionKey(), new AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                Log.e("LOGI", "AuthorizationStateWaitPhoneNumber");
                authStateData.postValue(new AuthState(AuthState.PHONE));
                //  String phoneNumber = "+380638447712"; //promptString("Please enter phone number: ");
                //  client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, true, true), new AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                Log.e("LOGI", "AuthorizationStateWaitCode");
                authStateData.postValue(new AuthState(AuthState.CODE));
                // String code = "47906";//promptString("Please enter authentication code: ");
                //  client.send(new TdApi.CheckAuthenticationCode(code,"",""), new AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                authStateData.postValue(new AuthState(AuthState.PASSWORD));
                break;
            }


            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                Log.e("LOGI", "AuthorizationStateReady");
                haveAuthorization = true;
                authorizationLock.lock();
                try {
                    gotAuthorization.signal();
                } finally {
                    authorizationLock.unlock();
                }
                authStateData.postValue(new AuthState(AuthState.HOME));
                break;


            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                haveAuthorization = false;
                Log.e("LOGI", "AuthorizationStateLoggingOut");
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                haveAuthorization = false;
                Log.e("LOGI", "AuthorizationStateClosing");
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                Log.e("LOGI", "AuthorizationStateClosed");
                if (!needQuit) {
                    client = Client.create(new UpdateHandler(), null, null); // recreate client after previous has closed
                } else {
                    canQuit = true;
                }
                break;
        }
    }

    class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object obj) {
            switch (obj.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    Log.e("LOGI", "Receive an error: " + obj);
                    onAuthorizationStateUpdated(null); // repeat last action
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    Log.e("LOGI", "TdApi.Ok.CONSTRUCTOR");
                    break;
                default:
                    Log.e("LOGI", "Receive wrong response from TDLib:$newLine$obj");
                    break;
            }
        }
    }

    private static class DefaultHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            Log.e("", object.toString());
        }
    }

    Client.ResultHandler defaultHandler = new DefaultHandler();


    class UpdateHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object obj) {
            Log.e("LOGI1", "UpdateHandler " + " " + obj.toString());
            TdApi.Chat chat;
            switch (obj.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    // Log.e("LOGI", "UpdateHandler " + " UpdateAuthorizationState " + obj.toString())
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) obj).authorizationState);
                    break;
                case TdApi.UpdateOption.CONSTRUCTOR:
                    // Log.e("LOGI", "UpdateHandler " + "UpdateOption " + obj.toString())
                    break;
                case TdApi.UpdateConnectionState.CONSTRUCTOR:
                    // Log.e("LOGI", "UpdateHandler " + "UpdateConnectionState " + obj.toString())
                    break;

                case TdApi.UpdateNewChat.CONSTRUCTOR:
                    TdApi.UpdateNewChat updateNewChat = (TdApi.UpdateNewChat) obj;
                    chat = updateNewChat.chat;
                    synchronized (chat) {
                        chats.put(chat.id, chat);
                        long order = chat.order;
                        chat.order = 0;
                        setChatOrder(chat, order);
                    }
                    break;
                case TdApi.UpdateChatOrder.CONSTRUCTOR:
                    TdApi.UpdateChatOrder updateChat = (TdApi.UpdateChatOrder) obj;
                    chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        setChatOrder(chat, updateChat.order);
                    }
                    break;
                case TdApi.UpdateChatLastMessage.CONSTRUCTOR:
                    TdApi.UpdateChatLastMessage updateChatLM = (TdApi.UpdateChatLastMessage) obj;
                    chat = chats.get(updateChatLM.chatId);
                    synchronized (chat) {
                        chat.lastMessage = updateChatLM.lastMessage;
                        setChatOrder(chat, updateChatLM.order);
                    }
                    mutableChatLastMessage.postValue(new TdChatObj(chat));
                    break;
                default:
                    //    Log.e("LOGI", "" + obj.getConstructor());
                    //    Log.e("LOGI", "x == 3")
            }
        }
    }

    private MutableLiveData<TdChatObj> mutableChatLastMessage = new MutableLiveData<>();

    public MutableLiveData<TdChatObj> getChatLastMessage() {
        return mutableChatLastMessage;
    }

    private static void setChatOrder(TdApi.Chat chat, long order) {
        synchronized (chatList) {
            if (chat.order != 0) {
                boolean isRemoved = chatList.remove(new OrderedChat(chat.order, chat.id));
                assert isRemoved;
            }
            chat.order = order;
            if (chat.order != 0) {
                boolean isAdded = chatList.add(new OrderedChat(chat.order, chat.id));
                assert isAdded;
            }
        }
    }


    private static final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<Long, TdApi.Chat>();
    private static final NavigableSet<OrderedChat> chatList = new TreeSet<OrderedChat>();
    private static boolean haveFullChatList = false;

    //  private static final NavigableSet<OrderedChat> mainChatList = new TreeSet<OrderedChat>();
//    private static boolean haveFullMainChatList = false;
//    public void getMainChatList() {
//        Log.e("LOGI", "list1 ");
//        long offsetOrder = Long.MAX_VALUE;
//        long offsetChatId = 0;
//        int limit = 100;
//        client.send(new TdApi.GetChats(offsetOrder, offsetChatId, limit), new Client.ResultHandler() {
//            @Override
//            public void onResult(TdApi.Object obj) {
//                Log.e("LOGI", "list " + obj.toString());
//                switch (obj.getConstructor()) {
//                    case TdApi.Error.CONSTRUCTOR:
//                        Log.e("LOGI", "Receive an error for GetChats: \n" + obj);
//                        break;
//                    case TdApi.Chats.CONSTRUCTOR:
//                        long[] chatIds = ((TdApi.Chats) obj).chatIds;
//                        break;
//                    default:
//                        Log.e("LOGI", "Receive wrong response from TDLib:  \n" + obj);
//                        break;
//                }
//            }
//        });
//    }


    private MutableLiveData<ArrayList<TdChatObj>> mutableChatList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<TdChatObj>> getMainChatList() {
        getChatList(20);
        return mutableChatList;
    }

    private void getChatList(final int limit) {
        synchronized (chatList) {
            if (!haveFullChatList && limit > chatList.size()) {
                // have enough chats in the chat list or chat list is too small
                long offsetOrder = Long.MAX_VALUE;
                long offsetChatId = 0;
                if (!chatList.isEmpty()) {
                    OrderedChat last = chatList.last();
                    offsetOrder = last.order;
                    offsetChatId = last.chatId;
                }
                client.send(new TdApi.GetChats(offsetOrder, offsetChatId, limit - chatList.size()), new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.Object object) {
                        switch (object.getConstructor()) {
                            case TdApi.Error.CONSTRUCTOR:
                                Log.e("LOGI", "Receive an error for GetChats: \n" + object);
                                break;
                            case TdApi.Chats.CONSTRUCTOR:
                                long[] chatIds = ((TdApi.Chats) object).chatIds;
                                if (chatIds.length == 0) {
                                    synchronized (chatList) {
                                        haveFullChatList = true;
                                    }
                                }
                                // chats had already been received through updates, let's retry request
                                getChatList(limit);
                                break;
                            default:
                                Log.e("LOGI", "Receive wrong response from TDLib: \n" + object);
                        }
                    }
                });
                return;
            }

            // have enough chats in the chat list to answer request
            java.util.Iterator<OrderedChat> iter = chatList.iterator();
            //   Log.e("LOGI", "");
            //   Log.e("LOGI","First " + limit + " chat(s) out of " + chatList.size() + " known chat(s):");
            // ArrayList<TdApi.Chat> result = new ArrayList<>();
            ArrayList<TdChatObj> result = new ArrayList<>();
            for (int i = 0; i < chatList.size(); i++) {
                long chatId = iter.next().chatId;
                TdApi.Chat chat = chats.get(chatId);
                synchronized (chat) {
                    //  Log.e("LOGIV",chatId + ": " + chat.title);
                    result.add(new TdChatObj(chat));
                }
            }
            mutableChatList.postValue(result);
            Log.e("LOGI", "");
        }
    }


    private static class OrderedChat implements Comparable<OrderedChat> {
        final long order;
        final long chatId;

        OrderedChat(long order, long chatId) {
            this.order = order;
            this.chatId = chatId;
        }

        @Override
        public int compareTo(OrderedChat o) {
            if (this.order != o.order) {
                return o.order < this.order ? -1 : 1;
            }
            if (this.chatId != o.chatId) {
                return o.chatId < this.chatId ? -1 : 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            OrderedChat o = (OrderedChat) obj;
            return this.order == o.order && this.chatId == o.chatId;
        }
    }


    public void sendTmMessage(long chatId, String message) {
        TdApi.InlineKeyboardButton[] row = {new TdApi.InlineKeyboardButton("https://telegram.org?1", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?2", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?3", new TdApi.InlineKeyboardButtonTypeUrl())};
        TdApi.ReplyMarkup replyMarkup = new TdApi.ReplyMarkupInlineKeyboard(new TdApi.InlineKeyboardButton[][]{row, row, row});
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        client.send(new TdApi.SendMessage(chatId, 0, true, false, replyMarkup, content), defaultHandler);
    }




    public void updateListMessagesByGroup(long chatId, final long lastMessageId) {
        Log.e("VVVZ", "chatId = "+chatId+" lastMessageId = " + lastMessageId);
        TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, lastMessageId, 0, 15, false);
        client.send(getChatHistory, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                ArrayList<TdMessageObj> messageObjs = new ArrayList<>();
                TdApi.Messages messages = (TdApi.Messages) object;
                for (int i = 0; i < messages.totalCount; i++) {
                    messageObjs.add(new TdMessageObj(messages.messages[i]));
                }
                Log.e("VVVZ",""+messageObjs.size());
                EventBus.getDefault().post(new ChatHistoryEvent(messageObjs));
            }
        });
    }


}
