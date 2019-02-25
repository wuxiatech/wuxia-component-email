package cn.wuxia.component.security.encrypter;

import cn.wuxia.component.security.DataEncrypter;
import cn.wuxia.component.security.MiscUtil;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;




public class DataEncrypterZ implements DataEncrypter {
	
	public static Map<String,Integer> mapRandom=new HashMap<String,Integer>();
	
	static {
		for (int i=0;i<=10;i++) {
			mapRandom.put(MiscUtil.md5Hash(i+""), i);
		}
	}

	@Override
	public String encrypt(String source) throws Exception {
		int n=(int)(Math.random()*10.0);
		
		String str1=Base64.encodeBase64String(source.getBytes("utf-8"));
		String str2=MiscUtil.md5Hash(n+"");
		String str3=MiscUtil.md5Hash(str1);
		
		StringBuffer sb=new StringBuffer();
		
		for (int i=0;i<32;i++) {
			sb.append(getCharAt(str1, i));
			sb.append(getCharAt(str2, i));
			sb.append(getCharAt(str3, i));
		}
		
		if (str1.length()>32) {
			sb.append(displaceString(str1.substring(32),n));
		}
		
		sb.append("Z");
		
		return sb.toString();
	}

	@Override
	public String decrypt(String source) throws Exception {
		int len=source.length()<=97?source.length()-65:32;
		
		StringBuffer sb1=new StringBuffer();
		StringBuffer sb2=new StringBuffer();
		StringBuffer sb3=new StringBuffer();
		
		int p=0;
		
		for (;p<len;p++) {
			sb1.append(source.charAt(p*3));
			sb2.append(source.charAt(p*3+1));
			sb3.append(source.charAt(p*3+2));
		}
		
		for (int i=0;i<32-p;i++) {
			sb2.append(source.charAt(p*3+i*2));
			sb3.append(source.charAt(p*3+i*2+1));
		}
		
		int n=mapRandom.get(sb2.toString())*-1;
		
		if (source.length()>97) {
			sb1.append(displaceString(source.substring(96,source.length()-1), n));
		}
		
		String str1=sb1.toString();
		
		if (MiscUtil.md5Hash(str1).equals(sb3.toString())) {
			
			byte[] bytes=Base64.decodeBase64(str1);
			
			return new String(bytes,"utf-8");
		}
		
		return null;
	}
	
	public static void main(String args[]) throws Exception {
		DataEncrypter epter=new DataEncrypterZ();
		System.out.println(epter.encrypt("123234234234"));
		System.out.println(epter.decrypt("5a6b89m72/fb5fcL65i70c935aa525yf9B395e6L75i15tda594b16G89x1f5adb66i73Cb1578r53K4cZ2d512r2eq2eqc16KOF6ZWH5rC45YW06KGX6I+y5YS/5pe2Z"));
	}
	
	// 位移，正数右移，负数左移
	public static String displaceString(String source,int num) {
		int len=source.length();
		char [] chars=source.toCharArray();
		char [] tochars=new char[chars.length];
		
		for (int i=0;i<len;i++) {
			int k=((i+num)%len+len)%len;
			
			tochars[k]=chars[i];
		}
		
		return new String(tochars);
	}
	
	// 获取字符串的第n个字符
	public static String getCharAt(String str,int index) {
		if (index>=str.length()) {
			return "";
		}
		return str.substring(index, index+1);
	}

}
