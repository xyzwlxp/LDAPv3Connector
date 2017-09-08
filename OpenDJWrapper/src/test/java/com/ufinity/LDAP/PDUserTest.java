package com.ufinity.LDAP;

import javax.naming.NamingException;

import org.junit.BeforeClass;
import org.junit.Test;

public class PDUserTest {
	
	static PDContext pdContext;
	static PDUser pdUser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pdContext = new PDContext("uid=openam_user,ou=admins,o=sgx","1q2w3e4r", "ldap://10.68.4.15:1389", "o=sgx");
		
	}

	@Test
	public void testGetPasswordPolicy() throws NamingException, PDException {
		PDUser xyz = new PDUser(pdContext,"user.1006");
		System.out.println(xyz.getPasswordPolicy());
	}

	@Test
	public void testSetPassword() throws NamingException, PDException {
		for(int i=0;i<400;i++){
			PDUser user = new PDUser(pdContext,"citi.user" + (100+ i) + "@sgx.com");
			user.setPassword("123456");
		}
		
		
	}
}
