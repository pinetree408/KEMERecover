package com.pinetree408.keme.recover;

/**
 * Created by user on 2017-03-07.
 */
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
;
import com.pinetree408.keme.util.ModeErrorLogger;
import com.pinetree408.keme.util.TopProcess;

public class KEMERecover implements NativeKeyListener {

  static  Recover recover;
  /** buffer writer to save log */
  private static ModeErrorLogger meLogger;
  static TopProcess topProcess;

  public KEMERecover() {
    meLogger = new ModeErrorLogger("result.txt");
    topProcess = new TopProcess();

    recover = new Recover();
  }

  public void nativeKeyPressed(NativeKeyEvent e) {
    recover.keyPressed(e);
    meLogger.log(e, topProcess.getNowLanguage(), topProcess.getNowTopProcess(), recover.getRecoverState(), "null");
  }

  public static void main(String[] args) {
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

  public void nativeKeyReleased(NativeKeyEvent e) {
    //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
  }

  public void nativeKeyTyped(NativeKeyEvent e) {
    //System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
  }
}
