package cn.wuxia.component.security.encrypter;

import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.component.security.DataEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Properties;




/**
 * 对称加密ASE128
 * @Company: Copyright @ 2016 优宜趣供应链管理有限公司 版权所有
 * @author: 陈志光
 * @date: 2015年6月1日下午12:41:08
 * @version 1.0 初稿
 */
public class DataEncrypterY implements DataEncrypter {
	
	private static Logger logger = LoggerFactory.getLogger(DataEncrypter.class);
	
	private static String SYS_ENCRYPT_KEY;
	
	private static Properties props;

	static {
		props= PropertiesUtils.loadProperties("classpath:/encrypt.properties");
		
		if (props!=null) {
			SYS_ENCRYPT_KEY=props.getProperty("SYS_ENCRYPT_KEY");
			
			if (!StringUtil.isEmpty(SYS_ENCRYPT_KEY)) {
				DataEncrypter encrypter=new DataEncrypterZ();
				try {
					SYS_ENCRYPT_KEY=encrypter.decrypt(SYS_ENCRYPT_KEY);
					
					logger.debug("SYS_ENCRYPT_KEY:"+SYS_ENCRYPT_KEY);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	@Override
	public String encrypt(String source) throws Exception {
		assert SYS_ENCRYPT_KEY!=null;
		
		StringBuffer sb = new StringBuffer();

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(SYS_ENCRYPT_KEY.getBytes()));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");// 创建密码器
		byte[] byteContent = source.getBytes("utf-8");
		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(byteContent);
		
		
		sb.append(parseByte2HexStr(result));
		sb.append("Y");
		
		logger.debug("sb:"+sb);

		return sb.toString();
	}

	@Override
	public String decrypt(String source) throws Exception {
		logger.debug("source:"+SYS_ENCRYPT_KEY);
		source=source.substring(0,source.length()-1);
		
		byte[] content = parseHexStr2Byte(source);
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(SYS_ENCRYPT_KEY.getBytes()));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");// 创建密码器
		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(content);
		return new String(result, "utf-8");
	}

	/**
	 * 将二进制转换成16进制
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
//15060518284800029	BC			ZFS	20150602499		颜白丽										20.00000					2014-04-28 00:00:00.000	准丰	准丰	4000-800-577						YT			10.00000	8.00000	5141		440120		AUS		2015-06-05 18:28:48.120	1	1			TEST						
	
	public static void main(String args[]) throws Exception {
		DataEncrypterZ de=new DataEncrypterZ();
		String content = "888888";
		
		String destr=de.encrypt(content);
		System.out.println(destr);
		
		System.out.println(de.decrypt("O1fD60g7f49fO0bD97g1d4c255af848102fca9f06af2b85aea6b028c7feab51ab927d6caZ"));
		
		DataEncrypterY deY=new DataEncrypterY();
		
		//15060516393800099	BC			ZFS	2015060247		颜白丽										20.00000					2014-04-28 00:00:00.000	准丰	准丰	4000-800-577						UEQ			10.00000	8.00000	5141		440120		AUS		2015-06-05 16:39:38.860	1	1			TEST						
		
		for (int i=0;i<50;i++) {
			System.out.println(deY.encrypt("410184198710056945"));
		}
		
		
		System.out.println(deY.decrypt("C1237A04BFFDFDB20B6629BEFCE801710C527E24732B672CDFC47938649BD0EAY"));
		System.out.println(deY.decrypt("4253F2441FBCDD98A61A5D2D0B67D4ADY"));
		System.out.println(deY.decrypt("B86266A3F8B125A30280095E404B4C56C64F55EEE67196147D6975CC902FA110594386F344D558509A9746CF4B7FE8C8C699498A55AD4004F463242C720898153B7718D27AA669F71D81CB7375EB25333879E2301C060C8A2C44917D6CB5BE67E1AF125FD0876F20113ED1B1BD1875F8Y"));
		
	}
}
