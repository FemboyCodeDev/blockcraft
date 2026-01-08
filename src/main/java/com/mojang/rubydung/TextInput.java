package com.mojang.rubydung;

import org.lwjgl.input.Keyboard;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;

public class TextInput {
    int[] keys =  {Keyboard.KEY_A,Keyboard.KEY_B,Keyboard.KEY_C,Keyboard.KEY_D,Keyboard.KEY_E,Keyboard.KEY_F,Keyboard.KEY_G
            ,Keyboard.KEY_H,Keyboard.KEY_I,Keyboard.KEY_J,Keyboard.KEY_K,Keyboard.KEY_L,Keyboard.KEY_M,Keyboard.KEY_N, Keyboard.KEY_O,Keyboard.KEY_P
            ,Keyboard.KEY_Q,Keyboard.KEY_R,Keyboard.KEY_S,Keyboard.KEY_T,Keyboard.KEY_U,Keyboard.KEY_V,Keyboard.KEY_W,Keyboard.KEY_X
            ,Keyboard.KEY_Y,Keyboard.KEY_Z,Keyboard.KEY_SPACE,Keyboard.KEY_0,Keyboard.KEY_1,Keyboard.KEY_2,Keyboard.KEY_3,Keyboard.KEY_4
            ,Keyboard.KEY_5,Keyboard.KEY_6,Keyboard.KEY_7,Keyboard.KEY_8,Keyboard.KEY_9};
    char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789".toCharArray();
    boolean[]  pressedKeys = new boolean[300];

    public boolean getBackspace(){
        //System.out.println(this.pressedKeys[Keyboard.KEY_BACK]);
        if (Keyboard.isKeyDown(Keyboard.KEY_BACK)){
            if (this.pressedKeys[Keyboard.KEY_BACK] == false){
                this.pressedKeys[Keyboard.KEY_BACK] = true;
                return true;
            }

        }else{

            this.pressedKeys[Keyboard.KEY_BACK] = false;
        }
        return false;
    }

    public char[] getKeys(){
        //int count = 0;
        //int length = 2;
        //char[] pressed = new char[length];
        String pressedString = "";
        for (int i = 0; i< keys.length; i++){
            if (Keyboard.isKeyDown(keys[i])){
                if (!pressedKeys[keys[i]]) {

                    pressedString = pressedString + letters[i];
                    pressedKeys[keys[i]] = true;
                }
            }else {
                pressedKeys[keys[i]] = false;
            }
        }
        char[] pressed = new char[0];

        boolean isCapital = false;
        if (Keyboard.isKeyDown(Keyboard.KEY_CAPITAL)){
            isCapital = !isCapital;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            isCapital = !isCapital;

        }

        if (isCapital){
            pressedString = pressedString.toUpperCase();
        } else{
            pressedString = pressedString.toLowerCase();
        }

        if(!pressedString.isEmpty()) {
            pressed = pressedString.toCharArray();
            //
            //System.out.println(pressed);

        }else {

        }
        return pressed;
    }
    public static String removeLastCharacter(String str) {
        if (str.isEmpty()) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }

}
