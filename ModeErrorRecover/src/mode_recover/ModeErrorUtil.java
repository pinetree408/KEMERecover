package mode_recover;

import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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


public class ModeErrorUtil {
	
	public static boolean isWordInDic(ArrayList<String> arrayString) {
		
		boolean isIn = false;
		String dict;
		String s;
	    
	    String compared = ModeErrorUtil.joinArrayList(arrayString).toLowerCase();
	    
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
	
	public static String joinArrayList(ArrayList<String> arrayString) {

	    String listString = "";

	    for (String temp : arrayString)
	    {
	        listString += temp;
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
			
			InputContext test = InputContext.getInstance();
			
		}
	}
	
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
		
			ArrayList stockList = null;
			
			try {
				stockList = (ArrayList) appleScript.eval(script);
			} catch (ScriptException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			processName = stockList.toString();

		}
		
		return processName;
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

}
