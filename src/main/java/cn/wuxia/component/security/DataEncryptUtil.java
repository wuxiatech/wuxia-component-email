package cn.wuxia.component.security;


import cn.wuxia.common.util.StringUtil;
import cn.wuxia.component.security.encrypter.DataEncrypterY;
import cn.wuxia.component.security.encrypter.DataEncrypterZ;

public class DataEncryptUtil {

	private static DataEncrypter getDataEncrypter(String type) {
		switch(type) {
		case "Z":return new DataEncrypterZ();
		case "Y":return new DataEncrypterY();
		}
		return null;
	}
	
	public static String encryptData(String source) throws Exception {
		if (StringUtil.isEmpty(source)) {
			return null;
		}
		
		DataEncrypter encrypter=getDataEncrypter("Z");
		
		String str=encrypter.encrypt(source);
		
		return str;
	}
	
	public static String decrypt(String source) throws Exception {
		if (StringUtil.isEmpty(source)) {
			return null;
		}
		
		String type=source.substring(source.length()-1);
		
		DataEncrypter encrypter=getDataEncrypter(type);
		
		String str=encrypter.decrypt(source);
		
		return str;
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println(encryptData("smtp.exmail.qq.com"));
		System.out.println(encryptData("Fengmi123"));
		System.out.println(decrypt(encryptData("1234567890")));
	}
	
}
