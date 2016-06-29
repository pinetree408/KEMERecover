package mode_recover;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
	
	public static void robotInput(ArrayList<Integer> arrayString, int backCount) throws Exception {
		
		int restoreSize = arrayString.size();
		String joinedString = ModeErrorUtil.joinArrayList(arrayString);
		int deleteSize = joinedString.length();
		String nowLang = ModeErrorUtil.nowlanguage();
		
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
	
	public static boolean isWordInDic(ArrayList<Integer> arrayString) {
		
		boolean isIn = false;
		String dict;
		String s;
	    
	    String compared = ModeErrorUtil.joinArrayList(arrayString).replace(".", "");
	    
	    if (ModeErrorUtil.nowlanguage() == "ko") {
	    	dict = "/dict/wordsEn.txt";
	    } else {
	    	dict = "/dict/wordsKo.txt";
	    	compared = ModeErrorUtil.eTok(compared);
	    }
	    
	    InputStream input = ModeErrorUtil.class.getResourceAsStream(dict);
	    BufferedReader in = new BufferedReader(new InputStreamReader(input));
	    
		try {
		    while ((s = in.readLine()) != null) {
		    	if (s.equals(compared)){
		    		isIn = true;
		    	};
		    }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (isIn){
			return true;
		}
		return false;
	}
	
	public static boolean isKeyShift(int KeyCode) {
		
		if ((KeyCode == 42) || (KeyCode == 54)) {
			return true;
		}
		return false;
	}
	
	public static String eTok(String english) {
		
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
	
	public static String nowlanguage(){
		
		String ret = new String();
		
		if (Platform.isWindows()) {
			
			User32 user32 = User32.INSTANCE;
			WinDef.HWND windowHandle=user32.GetForegroundWindow();
		    Ime ime = Ime.INSTANCE;
		    WinDef.HWND hwndIme = ime.ImmGetDefaultIMEWnd(windowHandle);
		    int test = MyUser32.INSTANCE.SendMessage(hwndIme, 0x0283, 0x05, 0);
			
		    if (test == 0){
		    	ret = "en";
		    }else{
		    	ret = "ko";
		    }
		    
		}else if (Platform.isMac()){
			
			InputContext test = InputContext.getInstance();
			
			ret = test.getLocale().getLanguage();
			
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
	public static String nowTopProcess() {
		
		String processName = null;
		
		if (Platform.isWindows()) {
			
			final int PROCESS_VM_READ=0x0010;
		    final int PROCESS_QUERY_INFORMATION=0x0400;
		    final User32 user32 = User32.INSTANCE;
		    final Kernel32 kernel32=Kernel32.INSTANCE;
		    final Psapi psapi = Psapi.INSTANCE;
		    WinDef.HWND windowHandle=user32.GetForegroundWindow();
		    IntByReference pid= new IntByReference();
		    user32.GetWindowThreadProcessId(windowHandle, pid);
		    WinNT.HANDLE processHandle=kernel32.OpenProcess(PROCESS_VM_READ | PROCESS_QUERY_INFORMATION, true, pid.getValue());
		    
		    byte[] filename = new byte[512];
		    psapi.GetModuleBaseNameW(processHandle.getPointer(), Pointer.NULL, filename, filename.length);
		    
		    processName = new String(filename);
		    
		} else 
		if(Platform.isMac()) {
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

		// Populate all the virtual key codes from NativeKeyEvent 
		HashMap<Integer, String> nativeKeyCodes = new HashMap<Integer, String>(); 
		Field nativeFields[] = NativeKeyEvent.class.getDeclaredFields(); 
		for (int i = 0; i < nativeFields.length; i++) { 
			String name = nativeFields[i].getName();
			int mod = nativeFields[i].getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && name.startsWith("VC_")) {
				nativeKeyCodes.put(nativeFields[i].getInt(null), name); 
			} 
		}
	 
		// Populate all the virtual key codes from KeyEvent 
		HashMap<String, Integer> javaKeyCodes = new HashMap<String, Integer>(); 
		Field javaFields[] = KeyEvent.class.getDeclaredFields(); 
		for (int i = 0; i < javaFields.length; i++) { 
			String name = javaFields[i].getName(); 
			int mod = javaFields[i].getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && name.startsWith("VK_")) {
				javaKeyCodes.put(name, javaFields[i].getInt(null)); 
			} 
		}
		
		String nativeKey = nativeKeyCodes.get(input);
		String[] nativeKeyArray = nativeKey.split("_");
		String finalNativeKey = "VK_" + nativeKeyArray[1];

		int result = javaKeyCodes.get(finalNativeKey);
	    return result;
	}
	
	public static class Logger {
		
		private File logFile;
		
		public Logger() {
			
		}
		
		public Logger(String fileName) {
			logFile = new File(fileName);
		}
		
		public void log(NativeKeyEvent e) {
			
			Date d = new Date(e.getWhen());
			SimpleDateFormat dateformat = new SimpleDateFormat("EEE MMM d HH:mm:ss:SSS z yyyy", Locale.KOREA);
			String date = dateformat.format(d);
			
			String logString =
				date +
				"-" + nowlanguage() + 
				"-" + nowTopProcess() +
				"-" + NativeKeyEvent.getKeyText(e.getKeyCode()) +
				"\r\n";			

			try {
				FileWriter fw = new FileWriter(this.logFile, true);
				fw.write(logString);
				fw.close();
			} catch (IOException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}
		}
	}

}