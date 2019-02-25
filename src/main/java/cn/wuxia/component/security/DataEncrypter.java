package cn.wuxia.component.security;

public interface DataEncrypter {
	public String encrypt(String source) throws Exception;
	
	public String decrypt(String source) throws Exception;
}
