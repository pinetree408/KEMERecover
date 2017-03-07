package com.pinetree408.keme.recover;

/** Created by user on 2017-01-02. */
import org.jnativehook.keyboard.NativeKeyEvent;

import com.sun.jna.Platform;

import java.util.ArrayList;

import java.awt.AWTException;
import java.awt.Robot;

import com.pinetree408.keme.util.Util;
import com.pinetree408.keme.util.ModeErrorLogger;

public class Recover {

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
  private static final int recoverLimit = 11;

  private static ArrayList<Integer> restoreString;
  private static int recoveredStringLength;

  private static String platform;
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
    recoveredStringLength = 0;

    if (Platform.isWindows()){
      platform = "Windows";
    } else if (Platform.isMac()) {
      platform = "Mac";
    } else {
      platform = "Unknown";
    }

    cmdKeyPressed = false;
  }

  public boolean isLanguageChangeKeyPressed(int keyCode) {

    if (platform.equals("Windows")) {
      if (keyCode == 112) {
        return true;
      }
    } else if (platform.equals("Mac")) {
      if (cmdKeyPressed == true && keyCode == 57) {
        return true;
      }
    }
    return false;
  }

  public boolean canRecover(ArrayList<Integer> restoreString) {

    if (platform.equals("Windows")) {

      if (((util.realLanguage(restoreString) == "ko") && (util.nowLanguage() == "ko"))
          || ((util.realLanguage(restoreString) == "en") && (util.nowLanguage() == "en"))) {
        return true;
      }

    } else if (platform.equals("Mac")) {

      if (((util.realLanguage(restoreString) == "ko") && (util.nowLanguage() == "en"))
          || ((util.realLanguage(restoreString) == "en") && (util.nowLanguage() == "ko"))) {
        return true;
      }
    }

    return false;
  }

  public void keyPressed(NativeKeyEvent e) {
    if (restoreString.size() < recoverLimit) {
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
              if (platform.equals("Windows")) {
                try {
                  util.robotInput(robot, restoreString, util.nowLanguage());
                } catch (Exception e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
                }
              }
            } else {
              restoreString.clear();
              recoveredStringLength = 0;
            }
          } else {
            // space => e.getKeyCode = 57
            if (e.getKeyCode() == 57 && cmdKeyPressed == false) {
              restoreString.clear();
              recoveredStringLength = 0;
            } else {
              if (!(restoreString.size() == 0 && e.getKeyCode() == 14)) {
                if ((e.getKeyCode() != 112) && (e.getKeyCode() != 14) && (e.getKeyCode() != 3676)) {
                  restoreString.add(e.getKeyCode());
                  recoveredStringLength += 1;
                } else {
                  if (restoreString.size() != 0
                      && recoveredStringLength != 0
                      && (e.getKeyCode() != 3676)) {
                    restoreString.remove(restoreString.size() - 1);
                    recoveredStringLength -= 1;
                  }
                }
              }
            }
          }
          break;

        case recover:
          if (recoveredStringLength == 0) {
            recoveredStringLength = restoreString.size();
          } else if (recoveredStringLength == 1) {
            recoverState = store;
            restoreString.clear();
            recoveredStringLength = 0;
          } else if (e.getKeyCode() != 14 && e.getKeyCode() != 57) {
            if (recoveredStringLength != 0) {
              recoveredStringLength -= 1;
            }
          }
          break;
      }
    }
    meLogger.log(e, nowLanguage, nowTopProcess, "null", String.valueOf(recoverState));
  }

  public void start() {

    nowTopProcess = util.nowTopProcess();

    if (!nowTopProcess.equals("")) {

      if (!prevTopProcess.equals(nowTopProcess)) {

        prevTopProcess = util.nowTopProcess();

        nowLanguage = util.nowLanguage();

        recoverState = store;
        restoreString.clear();
        recoveredStringLength = 0;
      }
    }
  }
}
