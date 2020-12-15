package com.skrypchenko.telegramtest.data;

public class AuthState {
    public static final int PHONE = 1;
    public static final int CODE = 2;
    public static final int PASSWORD = 3;
    public static final int HOME = 4;

    private int state;
    public AuthState(int state){
        this.state=state;
    }

    public int getState() {
        return state;
    }
}
