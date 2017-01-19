package com.pinetree408.keme.recover;

/** Created by user on 2017-01-02. */
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.jna.Platform;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.AWTException;
import java.awt.Robot;

import com.pinetree408.keme.util.Util;
import com.pinetree408.keme.util.ModeErrorLogger;

public class Recover implements NativeKeyListener {

  static Robot robot;
  static Util util;
  /** buffer writer to save log */
  private static ModeErrorLogger meLogger;

  static String prevTopProcess;
  static String nowTopProcess;
  static String nowLanguage;

  private static int recoverState;
  private static final int store = 0;
  private static final int recover = 1;

  private static ArrayList<Integer> restoreString;
  private static int recoveredString;

  private static boolean cmdKeyPressed; // For mac

  public Recover() {

    try {
      robot = new Robot();
    } catch (AWTException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    util = new Util();
    meLogger = new ModeErrorLogger("result.txt");

    prevTopProcess = "initial";
    nowTopProcess = "initial";
    nowLanguage = "initial";

    recoverState = store;
    restoreString = new ArrayList<Integer>();
    recoveredString = 0;

    cmdKeyPressed = false;
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
          || ((util.realLanguage(restoreString) == "en") && (util.nowLanguage() == "en"))) {
        return true;
      }

    } else if (Platform.isMac()) {

      if (((util.realLanguage(restoreString) == "ko") && (util.nowLanguage() == "en"))
          || ((util.realLanguage(restoreString) == "en") && (util.nowLanguage() == "ko"))) {
        return true;
      }
    }

    return false;
  }

  public void nativeKeyPressed(NativeKeyEvent e) {
    if (restoreString.size() < 11) {
      switch (recoverState) {
        case store:
          // ko/en change => e.getKeyCode = 112;
          // backspace => e.getKeyCode = 14

          /// mac command => 3676
          if (e.getKeyCode() == 3676) {
            cmdKeyPressed = true;
          }
          if (isLanguageChangeKeyPressed(e.getKeyCode()) == true && restoreString.size() != 0) {
            if (canRecover(restoreString)) {
              recoverState = recover;
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
              recoveredString = 0;
            }
          } else {
            // space => e.getKeyCode = 57
            if (e.getKeyCode() == 57 && cmdKeyPressed == false) {
              restoreString.clear();
              recoveredString = 0;
            } else {
              if (!(restoreString.size() == 0 && e.getKeyCode() == 14)) {
                if ((e.getKeyCode() != 112) && (e.getKeyCode() != 14) && (e.getKeyCode() != 3676)) {
                  restoreString.add(e.getKeyCode());
                  recoveredString += 1;
                } else {
                  if (restoreString.size() != 0
                      && recoveredString != 0
                      && (e.getKeyCode() != 3676)) {
                    restoreString.remove(restoreString.size() - 1);
                    recoveredString -= 1;
                  }
                }
              }
            }
          }
          break;

        case recover:
          if (recoveredString == 0) {
            recoveredString = restoreString.size();
          } else if (recoveredString == 1) {
            recoverState = store;
            restoreString.clear();
            recoveredString = 0;
          } else if (e.getKeyCode() != 14 && e.getKeyCode() != 57) {
            if (recoveredString != 0) {
              recoveredString -= 1;
            }
          }
          break;
      }
    }
    meLogger.log(e, nowLanguage, nowTopProcess, "null", String.valueOf(recoverState));
  }

  public static void main(String[] args) {
    // Set jnativehook logger level to off state
    Logger EventLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    EventLogger.setLevel(Level.OFF);

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      System.err.println(ex.getMessage());

      System.exit(1);
    }

    GlobalScreen.addNativeKeyListener(new Recover());

    Timer jobScheduler = new Timer();
    jobScheduler.schedule(
        new TimerTask() {
          @Override
          public void run() {

            nowTopProcess = util.nowTopProcess();

            if (!nowTopProcess.equals("")) {

              if (!prevTopProcess.equals(nowTopProcess)) {

                prevTopProcess = util.nowTopProcess();

                nowLanguage = util.nowLanguage();

                recoverState = store;
                restoreString.clear();
                recoveredString = 0;
              }
            }
          }
        },
        0,
        100);
  }

  public void nativeKeyReleased(NativeKeyEvent e) {
    //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
  }

  public void nativeKeyTyped(NativeKeyEvent e) {
    //System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
  }
}
