package com.pinetree408.keme.recover;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.AWTException;
import java.awt.Robot;

import com.pinetree408.keme.util.ModeErrorLogger;
import com.pinetree408.keme.util.TopProcess;

public class KEMERecover implements NativeKeyListener {

  private static Recover recover;
  private static ModeErrorLogger meLogger;
  private static TopProcess topProcess;

  private static Robot robot;

  private KEMERecover() {
    meLogger = new ModeErrorLogger("result.txt");
    topProcess = new TopProcess();

    recover = new Recover();
    try {
      robot = new Robot();
    } catch (AWTException ex) {
      ex.printStackTrace();
    }
  }

  public void nativeKeyPressed(NativeKeyEvent e) {
    recover.keyPressed(e, robot);
    meLogger.log(
            e,
            topProcess.getNowLanguage(),
            topProcess.getNowTopProcess(),
            recover.getRecoverState(),
            "null");
  }

  public static void main(String[] args) {
    new Tray();
    Logger EventLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    EventLogger.setLevel(Level.OFF);

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      System.err.println(ex.getMessage());

      System.exit(1);
    }

    GlobalScreen.addNativeKeyListener(new KEMERecover());
    Timer jobScheduler = new Timer();
    jobScheduler.schedule(
        new TimerTask() {
          @Override
          public void run() {
            if (topProcess.isChangeProcess()) {
              recover.initialize();
            }
          }
        },
        0,
        100);
  }

  public void nativeKeyReleased(NativeKeyEvent e) {}
  public void nativeKeyTyped(NativeKeyEvent e) {}
}
