package com.pinetree408.keme.recover;

import org.jnativehook.keyboard.NativeKeyEvent;
import com.sun.jna.Platform;
import java.util.ArrayList;
import java.awt.Robot;

import com.pinetree408.keme.util.Util;

class Recover {

  private static Util util;

  private static int recoverState;
  private static final int store = 0;
  private static final int recover = 1;
  private static final int recoverLimit = 11;
  // 0 : Recover
  // 1 : Auto-swith

  private static ArrayList<Integer> restoreString;
  private static int recoveredStringLength;

  private static String platform;
  private static boolean cmdKeyPressed; // For mac

  Recover() {
    util = new Util();

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

  private boolean isLanguageChangeKeyPressed(int keyCode) {

    if (platform.equals("Windows")) {
      return (keyCode == 112);
    } else if (platform.equals("Mac")) {
      return cmdKeyPressed && (keyCode == 57);
    }
    return false;
  }

  private boolean canRecover(ArrayList<Integer> restoreString) {

    if (platform.equals("Windows")) {
      return (
              ((util.realLanguage(restoreString).equals("ko")) && (util.nowLanguage().equals("ko")))
              || ((util.realLanguage(restoreString).equals("en")) && (util.nowLanguage().equals("en")))
      );
    } else if (platform.equals("Mac")) {
      return (
              ((util.realLanguage(restoreString).equals("ko")) && (util.nowLanguage().equals("en")))
              || ((util.realLanguage(restoreString).equals("en")) && (util.nowLanguage().equals("ko")))
      );
    }
    return false;
  }

  void keyPressed(NativeKeyEvent e, Robot robot) {
    if (restoreString.size() < recoverLimit) {
      switch (recoverState) {
        case store:
          // ko/en change => e.getKeyCode = 112;
          // backspace => e.getKeyCode = 14
          /// mac command => 3676
          if (e.getKeyCode() == 3676) {
            cmdKeyPressed = true;
          }
          if (isLanguageChangeKeyPressed(e.getKeyCode()) && restoreString.size() != 0) {
            if (canRecover(restoreString)) {
              recoverState = recover;
              if (platform.equals("Windows")) {
                try {
                  util.robotInput(robot, restoreString, util.nowLanguage());
                } catch (Exception e1) {
                  e1.printStackTrace();
                }
              }
            } else {
              restoreString.clear();
              recoveredStringLength = 0;
            }
          } else {
            // space => e.getKeyCode = 57
            if (e.getKeyCode() == 57 && !cmdKeyPressed) {
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
  }

  String getRecoverState() {
    return String.valueOf(recoverState);
  }

  void initialize() {
    recoverState = store;
    restoreString.clear();
    recoveredStringLength = 0;
  }
}
