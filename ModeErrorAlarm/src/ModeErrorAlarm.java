
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Object;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.im.InputContext;
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

public class ModeErrorAlarm extends JFrame implements WindowListener, NativeKeyListener {
	
	/** The text area to display event info. */
	private JTextArea txtEventInfo;
	private ArrayList<String> restoreString;
	private ArrayList<String> tmpString;
	private boolean state;
	private int backCount;
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
		
		restoreString = new ArrayList<String>();
		tmpString = new ArrayList<String>();
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		state = false;
		backCount = 0;
		
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
	
	public static int getKeyCode(String input){
		int result = 65535;
	    switch (input) {
			case "A":
				result = KeyEvent.VK_A;
				break;
			case "B":
				result = KeyEvent.VK_B;
				break;
    		case "C":
    			result = KeyEvent.VK_C;
    			break;
        	case "D":
        		result = KeyEvent.VK_D;
        		break;	    
	        case "E":
	        	result = KeyEvent.VK_E;
	        	break;
	        case "F":
	        	result = KeyEvent.VK_F;
	        	break;
	        case "G":
	        	result = KeyEvent.VK_G;
	        	break;
	        case "H":
	        	result = KeyEvent.VK_H;
	        	break;
	        case "I":
	        	result = KeyEvent.VK_I;
	        	break;
	        case "J":
	        	result = KeyEvent.VK_J;
	        	break;
	        case "K":
	        	result = KeyEvent.VK_K;
	        	break;
	        case "L":
	        	result = KeyEvent.VK_L;
	        	break;
	        case "M":
	        	result = KeyEvent.VK_M;
	        	break;
	        case "N":
	        	result = KeyEvent.VK_N;
	        	break;
	        case "O":
	        	result = KeyEvent.VK_O;
	        	break;
	        case "P":
	        	result = KeyEvent.VK_P;
	        	break;
	        case "Q":
	        	result = KeyEvent.VK_Q;
	        	break;
	        case "R":
	        	result = KeyEvent.VK_R;
	        	break;
	        case "S":
	        	result = KeyEvent.VK_S;
	        	break;
	        case "T":
	        	result = KeyEvent.VK_T;
	        	break;
	        case "U":
	        	result = KeyEvent.VK_U;
	        	break;
	        case "V":
	        	result = KeyEvent.VK_V;
	        	break;
	        case "W":
	        	result = KeyEvent.VK_W;
	        	break;
	        case "X":
	        	result = KeyEvent.VK_X;
	        	break;
	        case "Y":
	        	result = KeyEvent.VK_Y;
	        	break;
	        case "Z":
	        	result = KeyEvent.VK_Z;
	        	break;
	    }
	    return result;	
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
		
		robot.keyPress(KeyEvent.KEY_LOCATION_RIGHT);
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.KEY_LOCATION_RIGHT);
		robot.keyRelease(KeyEvent.VK_SPACE);
		
		for (int i = 0; i < arrayString.size(); i++) {
			robot.keyPress(KeyEvent.VK_BACK_SPACE);
			robot.keyRelease(KeyEvent.VK_BACK_SPACE);
		}
		
		for (int i = 0; i < arrayString.size() - 1; i++) {
			robot.keyPress(getKeyCode(arrayString.get(i)));
			robot.keyRelease(getKeyCode(arrayString.get(i)));
		}
		
	}
	
	public static String nowlanguage(){
		String ret;
		
		//For mac
		InputContext test = InputContext.getInstance();
		
		ret = test.getLocale().getLanguage();
		
		return ret;
	}

	public String eTok(String english) {
		
		String enH = "rRseEfaqQtTdwWczxvg";
		String regH = "[" + enH + "]";
		
		Map<String, Integer> enB = new HashMap<String, Integer>();
		enB.put("k", 0);
		enB.put("o", 1);
		enB.put("i", 2);
		enB.put("O", 3);
		enB.put("j", 4);
		enB.put("p", 5);
		enB.put("u", 6);
		enB.put("P", 7);
		enB.put("h", 8);
		enB.put("hk", 9);
		enB.put("ho", 10);
		enB.put("hl", 11);
		enB.put("y", 12);
		enB.put("n", 13);
		enB.put("nj", 14);
		enB.put("np", 15);
		enB.put("nl", 16);
		enB.put("b", 17);
		enB.put("m", 18);
		enB.put("ml", 19);
		enB.put("l", 20);
		String regB = "hk|ho|hl|nj|np|nl|ml|k|o|i|O|j|p|u|P|h|y|n|b|m|l";
		
		Map<String, Integer> enF = new HashMap<String, Integer>();
		enF.put("", 0);
		enF.put("r", 1);
		enF.put("R", 2);
		enF.put("rt", 3);
		enF.put("s", 4);
		enF.put("sw", 5);
		enF.put("sg", 6);
		enF.put("e", 7);
		enF.put("f", 8);
		enF.put("fr", 9);
		enF.put("fa", 10);
		enF.put("fq", 11);
		enF.put("ft", 12);
		enF.put("fx", 13);
		enF.put("fv", 14);
		enF.put("fg", 15);
		enF.put("a", 16);
		enF.put("q", 17);
		enF.put("qt", 18);
		enF.put("t", 19);
		enF.put("T", 20);
		enF.put("d", 21);
		enF.put("w", 22);
		enF.put("c", 23);
		enF.put("z", 24);
		enF.put("x", 25);
		enF.put("v", 26);
		enF.put("g", 27);
		String regF = "rt|sw|sg|fr|fa|fq|ft|fx|fv|fg|qt|r|R|s|e|f|a|q|t|T|d|w|c|z|x|v|g|";
		
		String regex = "("+regH+")("+regB+")(("+regF+")(?=("+regH+")("+regB+"))|("+regF+"))";
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(english);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			
			txtEventInfo.append("\n t" + String.valueOf(m.group().length()));
			int charCode = enH.indexOf((m.group().charAt(0))) * 588;
			
			
			if (m.group().length() > 2) {
				if (enF.get(String.valueOf(m.group().charAt(2))) != null) {
					charCode = charCode + enB.get(String.valueOf(m.group().charAt(1))) * 28 + enF.get(String.valueOf(m.group().charAt(2)));
				} else {
					charCode = charCode + enB.get(String.valueOf(m.group().charAt(1))+String.valueOf(m.group().charAt(2))) * 28;
				}
			} else {
				charCode = charCode + enB.get(String.valueOf(m.group().charAt(1))) * 28;
			}
			
			charCode = charCode + 44032;
			char c = (char) charCode;
			
			txtEventInfo.append(Character.toString(c));
		    m.appendReplacement(sb, Character.toString(c));
		}
		m.appendTail(sb);

		return sb.toString();
	}
	
	public boolean isWordInDic(ArrayList<String> arrayString) {
		
		boolean isIn = false;
		String result = nowlanguage();
		String dict;
	    String s;
	    
	    String listString = "";

	    for (String temp : arrayString)
	    {
	        listString += temp;
	    }
	    
	    String compared = listString.toLowerCase();
	    
	    FileReader filereader = null;

	    if (result == "ko") {
	    	dict = "dict/wordsEn.txt";
	    } else {
	    	dict = "dict/wordsKo.txt";
	    	compared = eTok(compared);
	    }
	    
	    try { 
	    	filereader = new FileReader(dict);
	    } 
	    catch (FileNotFoundException e) { 
	    	// TODO Auto-generated catch block
	    	e.printStackTrace(); 
	    } 
	    
	    BufferedReader in = new BufferedReader(filereader);
	    
		try {
		    
		    while ((s = in.readLine()) != null) {
		    	if (s.equals(compared)){
		    		isIn = true;
		    	};
		    }
		    in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (isIn){
			return true;
		}
		return false;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		
		if (state == false) {
			
			// backspace => e.getKeyCode = 14
			if (e.getKeyCode() == 14 && restoreString.size() != 0) {
				
				state = true;
				
				txtEventInfo.append(String.valueOf(backCount));
				if (backCount == 0) {
					boolean test = isWordInDic(restoreString);
					if (test == true){
						robotInput(restoreString);
					}
					backCount = 1;
				} else {
					robotInput(restoreString);
					backCount = 0;
				}

			} else {
				// space => e.getKeyCode = 57
				if (e.getKeyCode() == 57) {
					restoreString.clear();
					tmpString.clear();
				} else {
					if (e.getKeyCode() != 14) {
						restoreString.add(e.getKeyText(e.getKeyCode()));
						tmpString.add(e.getKeyText(e.getKeyCode()));
					}
				}
			}
			
		} else {
			
			if (tmpString.size() == 0) {
				tmpString.addAll(restoreString);
			}
			
			if (e.getKeyCode() != 14 && e.getKeyCode() != 57) {
				tmpString.remove(tmpString.size()-1);
			}
			
			if (tmpString.size() == 1){
				state = false;
				restoreString.remove(restoreString.size()-1);
				tmpString.clear();
			}
		}
		
		displayEventInfo(e);
		
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