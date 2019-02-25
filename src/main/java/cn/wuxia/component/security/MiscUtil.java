package cn.wuxia.component.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 包含一些进制转换 ， 加密 等工具方法
 * 
 * @author sunny
 *
 */
public class MiscUtil {

    /** 日志记录器 */
    public static final Logger _Logger = LoggerFactory.getLogger(MiscUtil.class);

    /** 用来将字节转换成 16 进制表示的字符表 */
    public static final char _hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /** 随机数表 - 大写字母 */
    public static final String RANDOM_TABLE_UPPERCASE = "ABCDEFGHIJKLNMOPQRSTUVWXYZ";
    /** 随机数表 - 小写字母 */
    public static final String RANDOM_TABLE_LOWERCASE = "abcdefghijklnmopqrstuvwxyz";
    /** 随机数表 - 数字 */
    public static final String RANDOM_TABLE_NUMBER = "0123456789";
    /** 随机数表 - 大小写字母 */
    public static final String RANDOM_TABLE_WORD = RANDOM_TABLE_UPPERCASE + RANDOM_TABLE_LOWERCASE;
    /** 随机数表 - 字母+数字 */
    public static final String RANDOM_TABLE_ALL = RANDOM_TABLE_WORD + RANDOM_TABLE_NUMBER;
    public static final Random _Random = new Random();

    private MiscUtil() {}

    /**
     * 生成一个随机数
     * 
     * @param len 生成串的长度
     * @param table 串的内容的 种子表 ， {@link MiscUtil#RANDOM_TABLE_ALL}
     * @return
     */
    public static String genRondomString(int len, String table) {
        if (null == table || 0 == table.length()) {
            table = RANDOM_TABLE_ALL;
        }
        int size = table.length();
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = table.charAt(_Random.nextInt(size));
        }
        return new String(chars);
    }


    /**
     * HEX编码
     * 
     * @param data 字节数组
     * @return HEX编码串
     */
    public static String encodeHex(byte[] data) {
        if (null == data) {
            return null;
        }
        char[] hex = new char[data.length * 2];
        int k = 0;
        for (int i = 0; i < data.length; i++) {
            // 转换成 16 进制字符的转换
            byte bt = data[i]; // 取第 i 个字节
            hex[k++] = _hexDigits[(bt >> 4) & 0x0F]; // 取字节中高 4 位的数字转换
            hex[k++] = _hexDigits[bt & 0x0F]; // 取字节中低 4 位的数字转换
        }
        return new String(hex);
    }

    /**
     * MD5散列计算
     * 
     * @param data 要计算散列值的字节数组
     * @return MD5散列值（16进格式串）
     */
    public static String md5Hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return encodeHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            _Logger.error(e.toString());
        }
        return null;
    }

    /**
     * 按字串的UTF-8编码进行MD5散列计算
     * 
     * @param str 要计算散列值的字串
     * @return MD5散列值（16进格式串）
     */
    public static String md5Hash(String str) {
        if (null == str) {
            return "";
        }
        try {
            return md5Hash(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            _Logger.error(e.toString());
        }
        return null;
    }

    /**
     * SHA散列计算
     * 
     * @param data 要计算散列值的字节数组
     * @return SHA散列值（16进格式串）
     */
    public static String shaHash(byte[] data) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA");
            sha.update(data);
            return encodeHex(sha.digest());
        } catch (NoSuchAlgorithmException e) {
            _Logger.error(e.toString());
        }
        return null;

    }

    /**
     * 按字串的UTF-8编码进行SHA散列计算
     * 
     * @param str 要计算散列值的字串
     * @return SHA散列值（16进格式串）
     */
    public static String shaHash(String str) {
        if (null == str) {
            return "";
        }
        try {
            return shaHash(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            _Logger.error(e.toString());
        }
        return null;
    }

    /**
     * 转换为16进制格式的字串
     * 
     * @param val 32位数值
     * @return 16进制格式串（如：a1f）
     */
    public static String toHex(int val) {
        return toHex(val, new StringBuilder(8)).toString();
    }

    /**
     * 获取不填充0的16进制
     * 
     * @param val
     * @return
     */
    public static String toHexNotFix(short val) {
        StringBuilder sb = new StringBuilder(4);

        if (val < 0 || val >= 0x1000) {
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[val & 0x0F]);
        } else if (val >= 0x0100) {
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[val & 0x0F]);
        } else if (val >= 0x0010) {
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[val & 0x0F]);
        } else if (val >= 0x0001) {
            sb.append(_hexDigits[val & 0x0F]);
        } else {
            sb.append("");
        }
        return sb.toString();

    }

    /**
     * 转为 HEX字串
     * 
     * @param val 32位数值
     * @param sb 转换HEX后的追加字串缓冲区
     * @return 追加后的字串缓冲区
     */
    public static StringBuilder toHex(int val, StringBuilder sb) {
        if (val < 0 || val >= 0x10000000) {
            sb.append(_hexDigits[(val >> 28) & 0xF]);
            sb.append(_hexDigits[(val >> 24) & 0xF]);
            sb.append(_hexDigits[(val >> 20) & 0xF]);
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x01000000) {
            sb.append(_hexDigits[(val >> 24) & 0xF]);
            sb.append(_hexDigits[(val >> 20) & 0xF]);
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00100000) {
            sb.append(_hexDigits[(val >> 20) & 0xF]);
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00010000) {
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00001000) {
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00000100) {
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00000010) {
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00000001) {
            sb.append(_hexDigits[(val) & 0xF]);
        } else {
            sb.append("0");
            return sb;
        }
        return sb;
    }

    /**
     * 64位整数HEX字串，不足16个字符前端补0
     * 
     * @param val 整数
     * @return hex格式串
     */
    public static String toHex64(long val) {
        if (0 == val) {
            return "0000000000000000";
        }
        return toHexFixed(val, new StringBuilder(16)).toString();
    }

    /**
     * 32位整数HEX字串，不足8个字符前端补0
     * 
     * @param val 32位数字
     * @param sb 字串缓冲区，若为null自动创建新的
     * @return 8字符的HEX编码串
     */
    public static StringBuilder toHexFixed(int val, StringBuilder sb) {
        if (null == sb) {
            sb = new StringBuilder(8);
        }
        if (val < 0 || val >= 0x10000000) {
            sb.append(_hexDigits[(val >> 28) & 0xF]);
            sb.append(_hexDigits[(val >> 24) & 0xF]);
            sb.append(_hexDigits[(val >> 20) & 0xF]);
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x01000000) {
            sb.append('0');
            sb.append(_hexDigits[(val >> 24) & 0xF]);
            sb.append(_hexDigits[(val >> 20) & 0xF]);
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00100000) {
            sb.append("00");
            sb.append(_hexDigits[(val >> 20) & 0xF]);
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00010000) {
            sb.append("000");
            sb.append(_hexDigits[(val >> 16) & 0xF]);
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00001000) {
            sb.append("0000");
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00000100) {
            sb.append("00000");
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00000010) {
            sb.append("000000");
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[(val) & 0xF]);
        } else if (val >= 0x00000001) {
            sb.append("0000000");
            sb.append(_hexDigits[(val) & 0xF]);
        } else {
            sb.append("00000000");
            return sb;
        }
        return sb;
    }

    /**
     * 64位整数HEX字串，不足16个字符前端补0
     * 
     * @param val 64位数值
     * @param sb 字串缓冲区，若为null自动创建新的
     * @return 16字符的HEX编码串
     */
    public static StringBuilder toHexFixed(long val, StringBuilder sb) {
        if (null == sb) {
            sb = new StringBuilder(16);
        }
        // 高32位
        int i32 = (int) ((val >> 32) & 0xFFFFFFFF);
        toHexFixed(i32, sb);
        // 低32位
        i32 = (int) (val & 0xFFFFFFFF);
        toHexFixed(i32, sb);
        return sb;
    }

    /**
     * 16位整数HEX字串，不足4个字符前端补0
     * 
     * @param val
     * @param sb
     * @return
     */
    public static StringBuilder toHexFixed(short val, StringBuilder sb) {
        if (null == sb) sb = new StringBuilder(4);

        if (val < 0 || val >= 0x1000) {
            sb.append(_hexDigits[(val >> 12) & 0xF]);
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[val & 0x0F]);
        } else if (val >= 0x0100) {
            sb.append('0');
            sb.append(_hexDigits[(val >> 8) & 0xF]);
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[val & 0x0F]);
        } else if (val >= 0x0010) {
            sb.append("00");
            sb.append(_hexDigits[(val >> 4) & 0xF]);
            sb.append(_hexDigits[val & 0x0F]);
        } else if (val >= 0x0001) {
            sb.append("000");
            sb.append(_hexDigits[val & 0x0F]);
        } else {
            sb.append("0000");
        }
        return sb;
    }


    /**
     * 10位整数字串，不足5个字符前端补0
     * 
     * @param val
     * @param sb
     * @return
     */
    public static StringBuilder toNumFixed(short tar, StringBuilder sb) {
        if (null == sb) sb = new StringBuilder(4);

        int val = tar;// md ， 修复short 类型 15位后都为负数
        if (tar < 0) {
            val = 0xFFFF & tar;
        }

        if (val >= 10000 && val <= 0xFFFF) {
            sb.append(val);
        } else if (val >= 1000) {
            sb.append('0');
            sb.append(val);
        } else if (val >= 100) {
            sb.append("00");
            sb.append(val);
        } else if (val >= 10) {
            sb.append("000");
            sb.append(val);
        } else if (val >= 0) {
            sb.append("0000");
            sb.append(val);
        } else {
            sb.append("00000");
        }
        return sb;
    }
    
    
    
	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符

		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			MessageDigest md = MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
										// 用字节表示就是 16 个字节

			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
											// 所以表示成 16 进制需要 32 个字符

			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节

				// 转换成 16 进制字符的转换

				byte byte0 = tmp[i]; // 取第 i 个字节

				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
															// >>>
															// 为逻辑右移，将符号位一起右移

				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 传入参数：字符串 传出参数：字节数组的 MD5 结果字符串
	 **/
	public static String getMD5(String source) {
		byte[] sByte = new byte[0];
		try {
			sByte = source.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		source = getMD5(sByte);
		return source;
	}

	/**
	 * String的字符串转换成unicode的String
	 */
	public static String stringToUnicode(String strText) throws Exception {
		char c;
		StringBuilder strRet = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128) {
				strRet.append("\\u");
			} else {
				// 低位在前面补00
				strRet.append("\\u00");
			}
			strRet.append(strHex);
		}
		return strRet.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 */
	public static String unicodeToString(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转

			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符

			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}

	public static String str2HexStr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		try {
			byte[] bs = str.getBytes("utf-8");
			int bit;
			for (int i = 0; i < bs.length; i++) {
				bit = (bs[i] & 0x0f0) >> 4;
				sb.append(chars[bit]);
				bit = bs[i] & 0x0f;
				sb.append(chars[bit]);
			}
		} catch (UnsupportedEncodingException e) {
		}
		return sb.toString();
	}

	// 16进制转为 byte 方法

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 用于接口加密方法
	 * 
	 * @param args
	 */

	private static String byte2Hex(byte[] byteArray) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] >= 0 && byteArray[i] < 16) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));
		}
		return strBuf.toString();
	}

	/**
	 * 新的md5签名，首尾放secret。
	 * 
	 * @param params
	 *            传给服务器的参数
	 * @param secret
	 *            分配给您的APP_SECRET
	 */
	public static String md5Sign(Map<String, String> params, String secret) {
		String result = null;
		StringBuffer orgin = beforeSign(params, new StringBuffer(secret));
		if (orgin == null)
			return result;
		orgin.append(secret);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2Hex(md.digest(orgin.toString().getBytes("utf-8")));
		} catch (Exception e) {
			throw new RuntimeException("sign error !");
		}
		return result;
	}

	/**
	 * 添加参数的封装方法
	 * 
	 * @param params
	 * @param orgin
	 * @return
	 */
	private static StringBuffer beforeSign(Map<String, String> params,
			StringBuffer orgin) {
		if (params == null)
			return null;
		Iterator<Entry<String, String>> iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			orgin.append(entry.getValue());
		}
		return orgin;
	}
    
    
    

    public static void main(String[] args) {
        // String str = "sunny";
        // _Logger.info(md5Hash(str));
        // _Logger.info(md5Hash(md5Hash(str)));
        // _Logger.info(shaHash(str));
        // _Logger.info(toHex64((System.currentTimeMillis() - 946721219851L) / 4));

        // for (int i = 32767; i <= 65535; i++) {
        // _Logger.info(((short) i) + "   ：" + toNumFixed((short) i, null).toString());
        // }

        // int val = tar;// md ， 修复short 类型 15位后都为负数
        // if(tar < 0){
        // val = 0x7FFF - tar;
        // }


        _Logger.info(genRondomString(10, RANDOM_TABLE_NUMBER));



        // 533c5ba8368075db8f6ef201546bd71a
    }

}
