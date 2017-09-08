package com.ufinity.LDAP;

import static org.junit.Assert.fail;

import java.security.GeneralSecurityException;
import java.util.Set;

import javax.naming.NamingException;

import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OpenDJContextTest {
	private OpenDJContext context;
	@Before
	public void setUp() throws Exception {
		context = OpenDJContext.newPlainSASLBind("ldaps://10.42.8.61:1636", "openam_config", "1q2w3e4r");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOpenDJContext() throws LdapException, GeneralSecurityException {
		context = new OpenDJContext("ldaps://10.42.8.60:1636", "uid=openam_config,ou=Service Accounts,dc=openam,dc=forgerock,dc=org", "1q2w3e4r");
	}

	@Test
	public void testGetAttributeStringString() throws LdapException {
		String dn = "ou=dm-Mon-Sat-08002000,ou=pop,ou=SGXservices,dc=openam,dc=forgerock,dc=org";
		String attr = "sunKeyValue";
		Attribute attr1 = context.getAttribute(dn, attr);
		System.out.println(attr1.firstValueAsString());
	}

	@Test
	public void testGetAttributeStringStringString() throws LdapException {
		String dn = "uid=user.1006,ou=People,o=sgx";
		String attr = "objectClass";
		Attribute attr1 = context.getAttribute(dn, attr,attr + "=" + "inetUser");
		System.out.println(attr1.isEmpty());
	}
	
	@Test
	public void testGetMultiValueAttribute() throws LdapException {
		String dn = "uid=user.1006,ou=People,o=sgx";
		String attr = "objectClass";
		Set<String> attr1 = context.getMultiValueAttribute(dn, attr);
		System.out.println(attr1.contains("inetUser"));
	}

	@Test
	public void testCreateOU() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAttribute() throws LdapException {
		String attr = "twofa-defaultotp";
		String value = "1";
		context.setAttribute("uid=user.1006,ou=People,o=sgx", attr , value );
	}

	@Test
	public void testAddAttribute() throws LdapException {
		String attr = "mobile";
		String value = "yunze.xu@ufinity.com1";
		context.addAttribute("uid=Piao,ou=People,o=sgx",  attr , value );
	}
	
	@Test
	public void testList() throws LdapException, SearchResultReferenceIOException {
		String dn = "ou=Policy,o=sgx";
		System.out.println(context.list(dn));
		
	}
	
	@Test
	public void testSearch() throws LdapException, SearchResultReferenceIOException, NamingException {
		String filter = "uid=Piao";
		System.out.println(context.search("ou=People, o=sgx", filter));
		
	}


	@Test
	public void testRemoveAttribute() throws LdapException {
		String attr = "mobile";
		context.removeAttribute("uid=user.1006,ou=People,o=sgx", attr);
	}
	
	@Test
	public void testRemoveAttributeVlue() throws LdapException {
		String attr = "sunKeyValue";
		String value = "policy=Extract_scd-dmsd-sam5_109_FATCA.csv5_scddmsd_BaseServlet_PATH_MAIN_FEATURE_DECL_FGN_TAX_OPTION_VIEW_TAX_,realm=/sgxprimedev.access.sgx";
		context.removeAttribute("ou=dm-Mon-Sat-08002000,ou=pop,ou=SGXservices,dc=openam,dc=forgerock,dc=org", attr ,value);
	}

	@Test
	public void testDestroyNode() throws LdapException {
		context.destroyNode("uid=Piao1,ou=People,o=sgx");
	}

	@Test
	public void testClose() {
		fail("Not yet implemented");
	}

}
