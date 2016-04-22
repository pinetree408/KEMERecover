package mode_error;

import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ModeErrorUtil {
	
	public static boolean isWordInDic(ArrayList<String> arrayString) {
		
		boolean isIn = false;
		String dict;
		String s;
	    
	    String compared = ModeErrorUtil.joinArrayList(arrayString).toLowerCase();
	    
	    FileReader filereader = null;

	    if (ModeErrorUtil.nowlanguage() == "ko") {
	    	dict = "dict/wordsEn.txt";
	    } else {
	    	dict = "dict/wordsKo.txt";
	    	compared = ModeErrorUtil.eTok(compared);
	    }
	    
	    try { 
	    	filereader = new FileReader(dict);
	    } 
	    catch (FileNotFoundException e) { 
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
		
		String regF = "|r|R|rt|s|sw|sg|e|f|fr|fa|fq|ft|fx|fv|fg|a|q|qt|t|T|d|w|c|z|x|v|g";
		
		//String regF = "rt|sw|sg|fr|fa|fq|ft|fx|fv|fg|qt|r|R|s|e|f|a|q|t|T|d|w|c|z|x|v|g|";
		
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
	
	public static String nowlanguage(){
		
		//For mac
		InputContext input = InputContext.getInstance();
		
		String inputLanguage = input.getLocale().getLanguage();
		
		return inputLanguage;
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
