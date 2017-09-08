package com.ufinity.LDAP;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class PasswordPolicyTest {

	OpenDJPasswordPolicy defaultPolicy;
	@Before
	public void setUp() throws Exception {

		OpenDJContext context = new OpenDJContext("ldaps://10.42.8.60:1636", "uid=openam_user,ou=admins,o=sgx", "1q2w3e4r");

		defaultPolicy = new OpenDJPasswordPolicy(context,"cn=StargatePasswordPolicy,ou=Policy,o=sgx");
	}

	@Test
	public void testGetPwdMinLength() {
		int expected = 6;
		System.out.println(defaultPolicy.getPwdMinLength());
		assertEquals(expected, defaultPolicy.getPwdMinLength());
	}
	
	@Test
	public void testGetStrengthCheckInJSRegex() {
		System.out.println(defaultPolicy.getStrengthCheckInJSRegex());
	}

	@Test
	public void testGetMaxPasswordAge() {
		long expected = 5; //days
		assertEquals(expected, defaultPolicy.getMaxPasswordAge()/3600/24);
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

}
