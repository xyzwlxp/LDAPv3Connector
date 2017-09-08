package com.ufinity.LDAP;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.junit.BeforeClass;
import org.junit.Test;

public class PDGroupTest {

	static PDContext pdContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// pdContext = new PDContext("cn=Directory Manager","Changeme@123",
		// "ldap://devdj:1389", "o=sgx");
		pdContext = new PDContext("cn=Directory Manager", "!1QazxsW2@", "ldap://10.42.5.82:7389", "o=sgx");
	}

	@Test
	public void testPDGroupPDContextStringBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testPDGroupPDContextString() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddMember() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreatePDContextStringStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreatePDContextStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDN() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDescription() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroupMembers() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindGroups() throws NamingException {
		PDUser u = new PDUser(pdContext, "sxspbi09");
		String[] groups = { "SC-BNKA-V1", "SC-BUYS-P4", "SC-CPMI-CDPENQ-V1", "SC-CAS-EXRA-P4", "SC-CAS-SECA-V2",
				"SC-CAS-SETI-V1",
				"SC-PYDT-V1",
				"SC-DMSD-SAM-A1",
				"SC-DMSD-SAM-B1",
				"SC-DMSD-WROS-D1",
				"SC-DMSD-WROS-E1" };
		Arrays.stream(groups).forEach(g -> {
			//System.out.println(g);
			List<String> result = null;
			try {
				result = PDGroup.findGroups(pdContext, "(cn="+g+")", 100);
				if(result.isEmpty()){
					System.out.println(g + " does not exists in LDAP");
				}else{
					PDGroup group = new PDGroup(pdContext, result.get(0));
					System.out.println("adding " + u.getDN() + " to " + group.getDN());
					group.addMember(u.getDN());
					System.out.println(group.getDN());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		});
		
	}

	@Test
	public void testSetDescription() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveMember() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBusinessCategory() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNestingGroups() {
		fail("Not yet implemented");
	}

}
