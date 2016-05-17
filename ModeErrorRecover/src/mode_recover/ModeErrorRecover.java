package mode_recover;

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

import mode_recover.ModeErrorUtil;


public class ModeErrorRecover extends JFrame implements WindowListener, NativeKeyListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 821193587029528109L;
	
	/** The text area to display event info. */
	private static JTextArea txtEventInfo;

	private Robot robot;
	
	private ArrayList<String> restoreString;
	private ArrayList<String> tmpString;
	private String state;
	private int backCount;

	public ModeErrorRecover() {
		setTitle("ModeError Alarm");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(200, 100);
		addWindowListener(this);

		Dimension frameSize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((screenSize.width - frameSize.width), 0);
		
		
		restoreString = new ArrayList<String>();
		tmpString = new ArrayList<String>();
		state = "store";
		backCount = 0;
		
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
		String type = array[2].replace("? •?˜?˜ì§? ?•Š?Œ", "NULL");
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
	
	public void robotInput(ArrayList<String> arrayString, int backCount) {
		
		int restoreSize = arrayString.size();
		int deleteSize = arrayString.size();
		String nowLang = ModeErrorUtil.nowlanguage();
		
		if (nowLang.equals("en")) {
			deleteSize = ModeErrorUtil.eTok(ModeErrorUtil.joinArrayList(arrayString).toLowerCase()).length();
		}
		
		deleteSize = deleteSize - backCount;
		
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
		txtEventInfo.append("-" + state);
		txtEventInfo.append("-" + restoreString.toString());
		txtEventInfo.append("-" + tmpString.toString());
		txtEventInfo.append("-" + ModeErrorUtil.nowlanguage());
		txtEventInfo.append("-" + ModeErrorUtil.nowTopProcess());
		txtEventInfo.append("\n");
		txtEventInfo.append("*********\n");
				
		switch (state){
		
		case "store":
			// ko/en change => e.getKeyCode = 112;
			// backspace => e.getKeyCode = 14
			if (e.getKeyCode() == 112 && restoreString.size() != 0) {
				
				state = "recover";

				if (ModeErrorUtil.isWordInDic(restoreString) == false){
					robotInput(restoreString, backCount);
				}

			} else {

				// space => e.getKeyCode = 57
				if (e.getKeyCode() == 57) {
					restoreString.clear();
					tmpString.clear();
				} else {
					restoreString.add(NativeKeyEvent.getKeyText(e.getKeyCode()));
					tmpString.add(NativeKeyEvent.getKeyText(e.getKeyCode()));
				}
			}
			
			break;
			
		case "recover":
			
			if (tmpString.size() == 0) {
				tmpString.addAll(restoreString);
			}
			
			if (tmpString.size() == 1){
				state = "store";
				restoreString.clear();
				tmpString.clear();
			}
			
			if (e.getKeyCode() != 14 && e.getKeyCode() != 57) {
				tmpString.remove(tmpString.size()-1);
			}
			
			break;
	}
		
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
	 * The ModeErrorRecover project entry point.
	 *
	 * @param args unused.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ModeErrorRecover();
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