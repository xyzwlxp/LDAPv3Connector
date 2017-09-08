package com.ufinity.LDAP;

import org.forgerock.opendj.ldap.Attribute;

public class OpenDJPasswordPolicy {
	public static final long _60_DAYS = 5184000L;

	//public static final String OPENDJ_DEFAULT_PASSWORD_POLICY = "cn=Default Password Policy,cn=Password Policies,cn=config";

	private String dn;

	private OpenDJContext context;

//	public static OpenDJPasswordPolicy getGlobalPasswordPolicy(OpenDJContext context) {
//		return new OpenDJPasswordPolicy(context, OPENDJ_DEFAULT_PASSWORD_POLICY);
//	}

	public OpenDJPasswordPolicy(OpenDJContext context, String dn) {
		this.context = context;
		this.dn = dn;
	}

	/**
	 * 
	 * @return the Minimum Password length if configured, otherwise 0.
	 */
	public int getPwdMinLength() {

		try {
			String validatorDN = context.getAttribute(dn, "ds-cfg-password-validator").firstValueAsString();
			if (!context.getAttribute(validatorDN, "ds-cfg-min-password-length").isEmpty()) {
				return Integer
						.parseInt(context.getAttribute(validatorDN, "ds-cfg-min-password-length").firstValueAsString());
			}
			Attribute a = context.getAttribute(dn, "pwdMinLength");
			return Integer.parseInt(a.firstValueAsString());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("pwdMinLength or ds-cfg-password-validator is not present in " + dn);
			return 0;
		}
	}

	/**
	 * This method will return a regex pattern which can be used to test
	 * password complexity.
	 * 
	 * In JavaScript, use code like below to check password strength: var
	 * patt1=new RegExp(regex); patt1.test(password); // return true if passed
	 * strength check.
	 * 
	 * Besides checking the password length, this regex also check that the
	 * password should passed 3 out of the 4 below constraints: At Least One
	 * Lowercase Character At Least One Uppercase Character At Least One
	 * Numerical Character At Least One Special Character
	 * 
	 * 
	 * @return A string represent the password strength RegEx pattern.
	 * 
	 *         maybe improved with checking whether password policy is type
	 *         2(require to check password strength)
	 */
	public String getStrengthCheckInJSRegex() {
		// Note here uer //// to eventuall output a single / in javascript
		String regex = "(?=.{" + getPwdMinLength()
				+ "})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\\\\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\\\\W]))|((?=.*[\\\\W])(?=.*[A-Z])(?=.*[\\\\d]))|((?=.*[a-z])(?=.*[\\\\W])(?=.*[\\\\d])))";
		return regex;
	}

	/**
	 * 
	 * @return the Max age in seconds of a password, 0 if not set.
	 */
	public long getPwdMaxAge() {
		try {
			Attribute a = context.getAttribute(dn, "pwdMaxAge");
			return Long.parseLong(a.firstValueAsString());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Quote from "IT Security Policy v8.0.pdf" 6. Maximum password age � users
	 * must be forced to change passwords every 60 days, except for privilege
	 * IDs and service accounts
	 * 
	 * @return # of seconds after which since password creation time that user
	 *         must change password. or 5184000 (60days) as default
	 */
	public long getMaxPasswordAge() {
		try {
			Attribute a = context.getAttribute(dn, "ds-cfg-password-expiration-warning-interval");
			String[] duration = a.firstValueAsString().split("\\s");
			if ("s".equals(duration[1])) {
				return Long.parseLong(duration[0]);
			} else {
				return _60_DAYS;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return _60_DAYS;
		}
	}

	/**
	 * 
	 * @return true - After admin reset, user need to change password false -
	 *         otherwise
	 */
	public boolean getPwdMustChange() {
		try {
			Attribute a = context.getAttribute(dn, "pwdMustChange");
			// System.out.println(a);
			return "TRUE".equalsIgnoreCase(a.firstValueAsString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String toString() {
		return "<PasswordPolicy><dn>" + dn + "</dn><PwdMinLength>" + getPwdMinLength()
				+ "</PwdMinLength></PasswordPolicy>";
	}
}
