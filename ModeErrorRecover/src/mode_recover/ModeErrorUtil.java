package mode_recover;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jnativehook.keyboard.NativeKeyEvent;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.lang.reflect.Field; 
import java.lang.reflect.Modifier; 


public class ModeErrorUtil {
	
	static Map<Integer, Double> pme;
	static Map<Integer, Double> pmk;
	
	static String enH = "rRseEfaqQtTdwWczxvg";
	static String regH;
	static String regB = "hk|ho|hl|nj|np|nl|ml|k|o|i|O|j|p|u|P|h|y|n|b|m|l";
    static String regF = "rt|sw|sg|fr|fa|fq|ft|fx|fv|fg|qt|r|R|s|e|f|a|q|t|T|d|w|c|z|x|v|g|";
    static Map<String, Integer> enB;
    static Map<String, Integer> enF;
    static String regex;
    static HashMap<Integer, String> nativeKeyCodes; 
    static HashMap<String, Integer> javaKeyCodes; 
    
    
	public static double pme(int a, int b) {
		
		int key = a * 10 + b;
		double result = pme.get(key);
		
		return result;
	}
	
	public static double pmk(int a, int b) {
		
		int key = a * 10 + b;
		double result = pmk.get(key);
		
		return result;
	}
	
	public static String eTok(String english) {
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(english);
		StringBuffer sb = new StringBuffer();
		
		while (m.find()) {
			
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
			
		    m.appendReplacement(sb, Character.toString((char) charCode));
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	public static boolean isCompleteKorean(ArrayList<Integer> arrayString) {
		
	    String english = ModeErrorUtil.joinArrayList(arrayString).replace(".", "");
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(english);
		StringBuffer sb = new StringBuffer();
		
		int initialLength = english.length();
		int finalLength = 0;

		while (m.find()) {
			
			finalLength = finalLength + m.group().length();

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
			
		    m.appendReplacement(sb, Character.toString((char) charCode));
		}
		m.appendTail(sb);
		
		if ((initialLength - finalLength) == 0) {
			return true;
		}
		
		return false;
	}
	
	public String realLanguage(ArrayList<Integer> arrayString) {
		
	    String english = ModeErrorUtil.joinArrayList(arrayString).replace(".", "");
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(english);
		StringBuffer sb = new StringBuffer();
		
		int initialLength = english.length();
		int finalLength = 0;

		while (m.find()) {
			
			finalLength = finalLength + m.group().length();

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
			
		    m.appendReplacement(sb, Character.toString((char) charCode));
		}
		m.appendTail(sb);
		
		if (ModeErrorUtil.pme(initialLength, finalLength) > ModeErrorUtil.pmk(initialLength, finalLength)) {
			return "en";
		}
		
		return "ko";
	}
	
	public void robotInput(ArrayList<Integer> arrayString, String nowLang, int backCount) throws Exception {
		
		int restoreSize = arrayString.size();
		String joinedString = ModeErrorUtil.joinArrayList(arrayString);
		int deleteSize = joinedString.length();
		
		if (nowLang.equals("en")) {
			deleteSize = ModeErrorUtil.eTok(joinedString).length();
		}
		
		deleteSize = deleteSize - backCount;
		
		Robot robot = null;
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public static boolean isKeyShift(int KeyCode) {
		
		if ((KeyCode == 42) || (KeyCode == 54)) {
			return true;
		}
		return false;
	}
	
	public static String joinArrayList(ArrayList<Integer> arrayString) {

	    String listString = "";
	    String regex = "[A-Za-z]";
	    
	    for (int i = 0; i < arrayString.size(); i++) {
	    	String temp = NativeKeyEvent.getKeyText(arrayString.get(i));

			if (temp.matches(regex)){
				if (!((i > 0) && NativeKeyEvent.getKeyText(arrayString.get(i-1)).contains("Shift"))) {
					temp = temp.toLowerCase();
				}
				listString += temp;
			} else if (!temp.contains("Shift")){
				listString += ".";
			}
	    }

		return listString;
	}
	
	public interface Psapi extends StdCallLibrary {
	    Psapi INSTANCE = (Psapi) Native.loadLibrary("Psapi", Psapi.class);

	    WinDef.DWORD GetModuleBaseNameW(Pointer hProcess, Pointer hModule, byte[] lpBaseName, int nSize);
	}

	public interface MyUser32 extends User32 {
	    MyUser32 INSTANCE = (MyUser32)Native.loadLibrary("user32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);
	    int SendMessage(HWND hWnd, int Msg, int wParam, int lParam);
	}
	
	public interface Ime extends User32 {
	    Ime INSTANCE = (Ime)Native.loadLibrary("imm32.dll", Ime.class);
	    HWND ImmGetDefaultIMEWnd(HWND hWnd);
	}
 
	public String nowlanguage(){
		
		String ret = "";
		
		if (Platform.isWindows()) {
			
			WinDef.HWND windowHandle = User32.INSTANCE.GetForegroundWindow();
		    WinDef.HWND hwndIme = Ime.INSTANCE.ImmGetDefaultIMEWnd(windowHandle);
			
		    if ( MyUser32.INSTANCE.SendMessage(hwndIme, 0x0283, 0x05, 0) == 0){
		    	ret = "en";
		    }else{
		    	ret = "ko";
		    }
		    
		}else if (Platform.isMac()){
			
			ret = InputContext.getInstance().getLocale().getLanguage();
			
		}
		
		return ret;
	}
	
	public static void changelanguage() {
		
		if (Platform.isWindows()) {
			
			User32 user32 = User32.INSTANCE;
			WinDef.HWND windowHandle=user32.GetForegroundWindow();
		    Ime ime = Ime.INSTANCE;
		    WinDef.HWND hwndIme = ime.ImmGetDefaultIMEWnd(windowHandle);
		    int test = MyUser32.INSTANCE.SendMessage(hwndIme, 0x0283, 0x05, 0);
		    int change = test == 0 ? 1 : 0; 
		    MyUser32.INSTANCE.SendMessage(hwndIme, 0x0283, 2, change);

		}else if (Platform.isMac()){
			
			//InputContext test = InputContext.getInstance();
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public String nowTopProcess() {
		
		String processName = "";
		
		if (Platform.isWindows()) {
			
			final int PROCESS_VM_READ = 0x0010;
		    final int PROCESS_QUERY_INFORMATION = 0x0400;
		    
		    WinDef.HWND windowHandle = User32.INSTANCE.GetForegroundWindow();
		    IntByReference pid = new IntByReference();
		    User32.INSTANCE.GetWindowThreadProcessId(windowHandle, pid);
		    WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(PROCESS_VM_READ | PROCESS_QUERY_INFORMATION, true, pid.getValue());
		    
		    byte[] filename = new byte[512];
		    
		    try {
		    	Psapi.INSTANCE.GetModuleBaseNameW(processHandle.getPointer(), Pointer.NULL, filename, filename.length);
		    } catch(NullPointerException e) {
		    	e.getStackTrace();
		    }
		    
		    String temp = "";
		    
		    for (int i = 0; i < 32; i++) {
		    	if (filename[i] != 0x00) {
		            temp += (char)filename[i];
		    	} 
		    }
		    
		    processName = temp;
		    
		} else if(Platform.isMac()) {
			String script="tell application \"System Events\"\n" +
					"\tname of application processes whose frontmost is true\n" +
					"end";
			ScriptEngine appleScript = new ScriptEngineManager().getEngineByName("AppleScriptEngine");
		
			ArrayList<String> stockList = null;
			
			try {
				stockList = (ArrayList<String>) appleScript.eval(script);
			} catch (ScriptException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			processName = stockList.toString();

		}
        
		return processName;
	}
	
	public static int getKeyCode(Integer input) throws Exception{
		
		String nativeKey = nativeKeyCodes.get(input);
		String[] nativeKeyArray = nativeKey.split("_");
		String finalNativeKey = "VK_" + nativeKeyArray[1];

		int result = javaKeyCodes.get(finalNativeKey);
	    return result;
	}

	public ModeErrorUtil() {

		pme = new HashMap<Integer, Double>();
		
		pme.put(20, 60.28);
		pme.put(22, 39.72);
		
		pme.put(30, 55.64);
		pme.put(32, 12.26);
		pme.put(33, 32.1);
		
		pme.put(40, 60.74);
		pme.put(42, 16.11);
		pme.put(43, 11.45);
		pme.put(44, 11.69);
		
		pme.put(50, 62.56);
		pme.put(52, 17.75);
		pme.put(53, 8.24);
		pme.put(54, 3.44);
		pme.put(55, 8.02);
		
		pme.put(60, 67.77);
		pme.put(62, 14.4);
		pme.put(63, 7.18);
		pme.put(64, 3.29);
		pme.put(65, 4.43);
		pme.put(66, 2.93);
		
		pme.put(70, 66.63);
		pme.put(72, 15.42);
		pme.put(73, 7.38);
		pme.put(74, 3.43);
		pme.put(75, 3.27);
		pme.put(76, 66.63);
		pme.put(77, 66.63);
		
		pme.put(80, 66.63);
		pme.put(82, 16.68);
		pme.put(83, 7.98);
		pme.put(84, 2.45);
		pme.put(85, 2.68);
		pme.put(86, 1.04);
		pme.put(87, 1.25);
		pme.put(88, 1.57);
		
		pme.put(90, 64.72);
		pme.put(92, 18.1);
		pme.put(93, 7.98);
		pme.put(94, 2.35);
		pme.put(95, 2.54);
		pme.put(96, 0.96);
		pme.put(97, 1.12);
		pme.put(98, 1.4);
		pme.put(99, 0.82);
		
		pme.put(100, 63.78);
		pme.put(102, 18.17);
		pme.put(103, 7.62);
		pme.put(104, 2.34);
		pme.put(105, 3.6);
		pme.put(106, 0.94);
		pme.put(107, 0.53);
		pme.put(108, 2.03);
		pme.put(109, 0.24);
		pme.put(110, 0.77);
		
		pmk = new HashMap<Integer, Double>();
		
		pmk.put(20, 0.0);
		pmk.put(22, 100.0);
		
		pmk.put(30, 0.0);
		pmk.put(32, 0.45);
		pmk.put(33, 99.55);
		
		pmk.put(40, 0.0);
		pmk.put(42, 0.0);
		pmk.put(43, 43.69);
		pmk.put(44, 56.31);
		
		pmk.put(50, 0.0);
		pmk.put(52, 0.0);
		pmk.put(53, 0.0);
		pmk.put(54, 2.91);
		pmk.put(55, 97.09);
		
		pmk.put(60, 0.0);
		pmk.put(62, 0.0);
		pmk.put(63, 0.0);
		pmk.put(64, 0.0);
		pmk.put(65, 15.98);
		pmk.put(66, 84.02);
		
		pmk.put(70, 0.0);
		pmk.put(72, 0.0);
		pmk.put(73, 0.0);
		pmk.put(74, 0.0);
		pmk.put(75, 0.0);
		pmk.put(76, 24.84);
		pmk.put(77, 75.16);
		
		pmk.put(80, 0.0);
		pmk.put(82, 0.0);
		pmk.put(83, 0.0);
		pmk.put(84, 0.0);
		pmk.put(85, 0.0);
		pmk.put(86, 0.0);
		pmk.put(87, 7.85);
		pmk.put(88, 92.15);
		
		pmk.put(90, 0.0);
		pmk.put(92, 0.0);
		pmk.put(93, 0.0);
		pmk.put(94, 0.0);
		pmk.put(95, 0.0);
		pmk.put(96, 0.0);
		pmk.put(97, 0.0);
		pmk.put(98, 14.83);
		pmk.put(99, 85.17);
		
		pmk.put(100, 0.0);
		pmk.put(102, 0.0);
		pmk.put(103, 0.0);
		pmk.put(104, 0.0);
		pmk.put(105, 0.0);
		pmk.put(106, 0.0);
		pmk.put(107, 0.0);
		pmk.put(108, 0.0);
		pmk.put(109, 17.04);
		pmk.put(110, 82.96);
		
		regH = "[" + enH + "]";
		enB = new HashMap<String, Integer>();
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
		
		enF = new HashMap<String, Integer>();
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
		
		regex = "("+regH+")("+regB+")(("+regF+")(?=("+regH+")("+regB+"))|("+regF+"))";
		
		// Populate all the virtual key codes from NativeKeyEvent 
		nativeKeyCodes = new HashMap<Integer, String>(); 
		Field nativeFields[] = NativeKeyEvent.class.getDeclaredFields(); 
		for (int i = 0; i < nativeFields.length; i++) { 
			String name = nativeFields[i].getName();
			int mod = nativeFields[i].getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && name.startsWith("VC_")) {
				try {
					nativeKeyCodes.put(nativeFields[i].getInt(null), name);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} 
		}
	 
		// Populate all the virtual key codes from KeyEvent 
		javaKeyCodes = new HashMap<String, Integer>(); 
		Field javaFields[] = KeyEvent.class.getDeclaredFields(); 
		for (int i = 0; i < javaFields.length; i++) { 
			String name = javaFields[i].getName(); 
			int mod = javaFields[i].getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && name.startsWith("VK_")) {
				try {
					javaKeyCodes.put(name, javaFields[i].getInt(null));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} 
		}
	}

}