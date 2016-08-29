package mode_recover;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.jna.Platform;

import mode_recover.ModeErrorUtil;


public class ModeErrorRecover extends JFrame implements WindowListener, NativeKeyListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 821193587029528109L;
	
	/** The text area to display event info. */
	private static JTextArea txtEventInfo;

	/** buffer writer to save log */
	private static ModeErrorUtil.Logger logger;
	
	private static ArrayList<Integer> restoreString;
	private static ArrayList<Integer> tmpString;
	private static String state;
	private int backCount;
	private static int limitNumber;
	private static boolean cmdKeyPressed;
	private static String topProcess;
	private static boolean deploy;

	public ModeErrorRecover() {
		
		logger = new ModeErrorUtil.Logger("result.txt");
		restoreString = new ArrayList<Integer>();
		tmpString = new ArrayList<Integer>();
		state = "store";
		backCount = 0;
		limitNumber = 0;
		cmdKeyPressed= false;
		topProcess = "initial";
		deploy = true;
		
		setTitle("ModeError Recover");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (!deploy) {
			setSize(500, 600);
		} else {
			setSize(0, 0);
		}
		addWindowListener(this);

		Dimension frameSize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((screenSize.width - frameSize.width), 0);
		
		txtEventInfo = new JTextArea();
		txtEventInfo.setEditable(false);
		txtEventInfo.setBackground(new Color(0xFF, 0xFF, 0xFF));
		txtEventInfo.setForeground(new Color(0x00, 0x00, 0x00));

		JScrollPane scrollPane = new JScrollPane(txtEventInfo);
		scrollPane.setPreferredSize(new Dimension(375, 125));
		add(scrollPane, BorderLayout.CENTER);
		
        Logger EventLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        EventLogger.setLevel(Level.OFF);
		
		GlobalScreen.setEventDispatcher(new SwingDispatchService());
		
		setVisible(true);

	}
	
	private void displayEventInfo(NativeKeyEvent e) {
		txtEventInfo.append(NativeKeyEvent.getKeyText(e.getKeyCode()));
		txtEventInfo.append("-" + String.valueOf(e.getKeyCode()));

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
	
	public boolean isLanguageChangeKeyPressed(int keyCode) {
		
		if (Platform.isWindows()) {

			if (keyCode == 112) {
				return true;
			}
			
		}else if (Platform.isMac()){

			if (cmdKeyPressed == true && keyCode == 57) {
				//cmdKeyPressed = false;
				return true;
			}	
			
		}
		return false;
	}
	
	public boolean canRecover(ArrayList<Integer> restoreString) {
		
		if (Platform.isWindows()) {

			if (((ModeErrorUtil.realLanguage(restoreString) == "ko") && (ModeErrorUtil.nowlanguage() == "ko"))
					|| ((ModeErrorUtil.realLanguage(restoreString) == "en") && (ModeErrorUtil.nowlanguage() == "en"))){
				return true;
			}
			
		}else if (Platform.isMac()){

			if (((ModeErrorUtil.realLanguage(restoreString) == "ko") && (ModeErrorUtil.nowlanguage() == "en"))
					|| ((ModeErrorUtil.realLanguage(restoreString) == "en") && (ModeErrorUtil.nowlanguage() == "ko"))){
				return true;
			}	
			
		}
		
		return false;
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		// TODO Auto-generated method stub
		if (cmdKeyPressed == true && state.equals("recover")) {
			cmdKeyPressed = false;
			try {
				ModeErrorUtil.robotInput(restoreString, backCount);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		
		if (limitNumber < 11) {
			
			switch (state){
			
				case "store":
					
					// ko/en change => e.getKeyCode = 112;
					// backspace => e.getKeyCode = 14
					
					/// mac command => 3676
					
					if (e.getKeyCode() == 3676) {
						cmdKeyPressed = true;
					}
					
					if (isLanguageChangeKeyPressed(e.getKeyCode()) == true && restoreString.size() != 0) {
						
						if (canRecover(restoreString)){
						
							state = "recover";
						
						    if (Platform.isWindows()) {
							
						    	try {
						    		ModeErrorUtil.robotInput(restoreString, backCount);
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
				
				case "recover":
				
					if (tmpString.size() == 0) {
						tmpString.addAll(restoreString);
					} else if (tmpString.size() == 1){
						state = "store";
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
		
		txtEventInfo.append("----------------------\n");
		displayEventInfo(e);
		txtEventInfo.append("-" + String.valueOf(limitNumber));
		txtEventInfo.append("-" + state);
		txtEventInfo.append("-" + cmdKeyPressed);
		txtEventInfo.append("-" + restoreString.toString());
		txtEventInfo.append("-" + tmpString.toString());
		txtEventInfo.append("-" + ModeErrorUtil.nowlanguage());
		txtEventInfo.append("-" + ModeErrorUtil.nowTopProcess());
		txtEventInfo.append("\n");
		txtEventInfo.append("*********\n");
		logger.log(e);
		
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

		while (true) {
			String nowTopProcess = ModeErrorUtil.nowTopProcess();
			
			if (!nowTopProcess.equals("") && !topProcess.equals("") && !nowTopProcess.equals(topProcess)) {
			
				topProcess = nowTopProcess;
			
				limitNumber = 0;
				state = "store";
				restoreString.clear();
				tmpString.clear();
			
			}
		}
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
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}