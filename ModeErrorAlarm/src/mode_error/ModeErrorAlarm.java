package mode_error;

import mode_error.ModeErrorUtil;

import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.jna.Platform;

public class ModeErrorAlarm extends JFrame implements WindowListener, NativeKeyListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 821193587029528109L;
	
	/** The text area to display event info. */
	private static JTextArea txtEventInfo;

	private Robot robot;
	
	public ModeErrorAlarm() {
		setTitle("ModeError Alarm");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(200, 100);
		addWindowListener(this);

		Dimension frameSize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((screenSize.width - frameSize.width), 0);
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		txtEventInfo = new JTextArea();
		txtEventInfo.setEditable(false);
		txtEventInfo.setBackground(new Color(0xFF, 0xFF, 0xFF));
		txtEventInfo.setForeground(new Color(0x00, 0x00, 0x00));
		txtEventInfo.setText("");

		JScrollPane scrollPane = new JScrollPane(txtEventInfo);
		scrollPane.setPreferredSize(new Dimension(375, 125));
		add(scrollPane, BorderLayout.CENTER);
		
		GlobalScreen.setEventDispatcher(new SwingDispatchService());
		
		setVisible(true);
		
	}
	
	private String realAlphabet(String paramString) {
		String input = paramString;
		String[] array = input.split(",");
		String type = array[2].replace("정의되지 않음", "NULL");
		String[] result = type.split("=");
		
		return result[1];
	}
	
	private void displayEventInfo(final NativeInputEvent e) {
		txtEventInfo.append(realAlphabet(e.paramString()));

		try {
			//Clean up the history to reduce memory consumption.
			if (txtEventInfo.getLineCount() > 100) {
				txtEventInfo.replaceRange("", 0, txtEventInfo.getLineEndOffset(txtEventInfo.getLineCount() - 1 - 100));
			}

			txtEventInfo.setCaretPosition(txtEventInfo.getLineStartOffset(txtEventInfo.getLineCount() - 1));
		}
		catch (BadLocationException ex) {
			txtEventInfo.setCaretPosition(txtEventInfo.getDocument().getLength());
		}
	}
	
	public void robotInput(ArrayList<String> arrayString) {
		
		int restoreSize = arrayString.size();
		int deleteSize = arrayString.size() - 1;
		String nowLang = ModeErrorUtil.nowlanguage();
		
		if (nowLang.equals("ko")) {
			deleteSize = ModeErrorUtil.eTok(ModeErrorUtil.joinArrayList(arrayString).toLowerCase()).length() - 1;
		}
		
		robot.keyPress(KeyEvent.KEY_LOCATION_RIGHT);
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.KEY_LOCATION_RIGHT);
		robot.keyRelease(KeyEvent.VK_SPACE);
		
		while(true) {
			if(!nowLang.equals(ModeErrorUtil.nowlanguage())) {
				break;
			}
		}
		
		for (int i = 0; i < deleteSize; i++) {
			robot.keyPress(KeyEvent.VK_BACK_SPACE);
			robot.keyRelease(KeyEvent.VK_BACK_SPACE);
		}
		
		for (int i = 0; i < restoreSize; i++) {
			robot.keyPress(ModeErrorUtil.getKeyCode(arrayString.get(i)));
			robot.keyRelease(ModeErrorUtil.getKeyCode(arrayString.get(i)));
		}

	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		
		txtEventInfo.append("----------------------\n");
		displayEventInfo(e);
		if(Platform.isMac()) {
			String script="tell application \"System Events\"\n" +
					"\tname of application processes whose frontmost is true\n" +
					"end";
			ScriptEngine appleScript = new ScriptEngineManager().getEngineByName("AppleScriptEngine");
		
			ArrayList stockList = null;
			
			try {
				stockList = (ArrayList) appleScript.eval(script);
			} catch (ScriptException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			txtEventInfo.append("-" + stockList.toString());
		}
		txtEventInfo.append("\n");
		txtEventInfo.append("*********\n");
		
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			txtEventInfo.append("Error: " + ex.getMessage() + "\n");
		}
		GlobalScreen.addNativeKeyListener(this);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.runFinalization();
		System.exit(0);	
	}

	/**
	 * The ModeErrorAlarm project entry point.
	 *
	 * @param args unused.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ModeErrorAlarm();
			}
		});
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}