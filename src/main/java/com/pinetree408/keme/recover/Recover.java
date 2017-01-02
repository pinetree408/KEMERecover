package com.pinetree408.keme.recover;

/**
 * Created by user on 2017-01-02.
 */

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.jna.Platform;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.AWTException;
import java.awt.Robot;

import com.pinetree408.keme.util.Util;

public class Recover implements NativeKeyListener {

    static Robot robot;
    static Util util;
    static String prevTopProcess;
    static String nowTopProcess;

    private static int limitNumber;
    private static int state;
    /*
    * state
    * 1 : store
    * 2 : recover
    * */

    private static ArrayList<Integer> restoreString;
    private static ArrayList<Integer> tmpString;

    private static boolean cmdKeyPressed; // For mac

    public Recover() {

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        util = new Util();
        prevTopProcess = "initial";
        nowTopProcess = "initial";

        limitNumber = 0;
        state = 1;
        restoreString = new ArrayList<Integer>();
        tmpString = new ArrayList<Integer>();

        cmdKeyPressed= false;
    }

    public boolean isLanguageChangeKeyPressed(int keyCode) {

        if (Platform.isWindows()) {

            if (keyCode == 112) {
                return true;
            }

        } else if (Platform.isMac()) {

            if (cmdKeyPressed == true && keyCode == 57) {
                //cmdKeyPressed = false;
                return true;
            }

        }
        return false;
    }

    public boolean canRecover(ArrayList<Integer> restoreString) {

        if (Platform.isWindows()) {

            if (((util.realLanguage(restoreString) == "ko") && (util.nowLanguage() == "ko"))
                    || ((util.realLanguage(restoreString) == "en") && (util.nowLanguage() == "en"))){
                return true;
            }

        }else if (Platform.isMac()){

            if (((util.realLanguage(restoreString) == "ko") && (util.nowLanguage() == "en"))
                    || ((util.realLanguage(restoreString) == "en") && (util.nowLanguage() == "ko"))){
                return true;
            }

        }

        return false;
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        nowTopProcess = util.nowTopProcess();

        if (!nowTopProcess.equals("") && !prevTopProcess.equals("") && !nowTopProcess.equals(prevTopProcess)) {

            prevTopProcess = nowTopProcess;

            limitNumber = 0;
            state = 1;
            restoreString.clear();
            tmpString.clear();

        }

        if (limitNumber < 11) {

            switch (state){

                case 1:

                    // ko/en change => e.getKeyCode = 112;
                    // backspace => e.getKeyCode = 14

                    /// mac command => 3676

                    if (e.getKeyCode() == 3676) {
                        cmdKeyPressed = true;
                    }

                    if (isLanguageChangeKeyPressed(e.getKeyCode()) == true && restoreString.size() != 0) {

                        if (canRecover(restoreString)){

                            state = 2;

                            if (Platform.isWindows()) {

                                try {
                                    util.robotInput(robot, restoreString, util.nowLanguage());
                                } catch (Exception e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }

                            }

                        } else {
                            restoreString.clear();
                            tmpString.clear();
                        }

                    } else {

                        // space => e.getKeyCode = 57
                        if (e.getKeyCode() == 57 && cmdKeyPressed == false) {
                            restoreString.clear();
                            tmpString.clear();
                        } else {
                            if (!(restoreString.size() == 0 && e.getKeyCode() == 14)) {
                                if ((e.getKeyCode() != 112) && (e.getKeyCode() != 14) && (e.getKeyCode() != 3676)) {
                                    restoreString.add(e.getKeyCode());
                                    tmpString.add(e.getKeyCode());
                                    limitNumber += 1;
                                } else {
                                    if (restoreString.size() != 0 && tmpString.size() != 0 && (e.getKeyCode() != 3676)){
                                        restoreString.remove(restoreString.size()-1);
                                        tmpString.remove(tmpString.size()-1);
                                    }
                                }
                            }
                        }
                    }

                    break;

                case 2:

                    if (tmpString.size() == 0) {
                        tmpString.addAll(restoreString);
                    } else if (tmpString.size() == 1){
                        state = 1;
                        restoreString.clear();
                        tmpString.clear();
                    } else if (e.getKeyCode() != 14 && e.getKeyCode() != 57) {
                        if (tmpString.size() != 0){
                            tmpString.remove(tmpString.size()-1);
                        }
                    }

                    break;
            }

        }
    }

    public static void main(String[] args) {
        // Set jnativehook logger level to off state
        Logger EventLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        EventLogger.setLevel(Level.OFF);

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new Recover());
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        //System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }

}
