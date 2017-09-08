package com.ufinity.LDAP;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;
import javax.net.ssl.SSLContext;

import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.ConnectionFactory;
import org.forgerock.opendj.ldap.Connections;
import org.forgerock.opendj.ldap.Entries;
import org.forgerock.opendj.ldap.Entry;
import org.forgerock.opendj.ldap.LDAPConnectionFactory;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.LinkedAttribute;
import org.forgerock.opendj.ldap.LinkedHashMapEntry;
import org.forgerock.opendj.ldap.SSLContextBuilder;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.TreeMapEntry;
import org.forgerock.opendj.ldap.TrustManagers;
import org.forgerock.opendj.ldap.requests.BindRequest;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;
import org.forgerock.util.Options;

public class OpenDJContext implements AutoCloseable {

	private Connection connection;

	/**
	 * 
	 * @param urls
	 *            LDAP URL in format of ldap(s)://ldap_url:ldap_port, if there
	 *            are multiple urls presented, separate with a comma.
	 * @param bindDN
	 * @param bindPassword
	 * @throws LdapException
	 * @throws GeneralSecurityException
	 */
	public OpenDJContext(String urls, String bindDN, String bindPassword)
			throws LdapException, GeneralSecurityException {
		final List<ConnectionFactory> factories = new LinkedList<ConnectionFactory>();

		for (String ldapURL : urls.split(",")) {
			String[] php = ldapURL.split(":");
			Options options = Options.defaultOptions();

			if (php[0].equalsIgnoreCase("ldaps")) {
				SSLContext sslContext = new SSLContextBuilder().setTrustManager(TrustManagers.trustAll())
						.getSSLContext();
				options.set(org.forgerock.opendj.ldap.LDAPConnectionFactory.SSL_CONTEXT, sslContext);
				options.set(org.forgerock.opendj.ldap.LDAPConnectionFactory.SSL_USE_STARTTLS, false);
			}
			// if ("ldaps".equalsIgnoreCase(php[0])) {
			// SSLContext sslContext = new SSLContextBuilder()
			// .setTrustManager(TrustManagers.trustAll()).getSSLContext();
			// options.set(SSLContext.getDefault());
			// }

			factories.add(new LDAPConnectionFactory(php[1].replace("/", ""), Integer.parseInt(php[2]), options));
		}
		// final LoadBalancingAlgorithm algorithm
		// = new FailoverLoadBalancingAlgorithm(factories);
		// final ConnectionFactory factory =
		// Connections.newLoadBalancer(algorithm);

		final ConnectionFactory factory = Connections.newFailoverLoadBalancer(factories, Options.defaultOptions());
		connection = factory.getConnection();
		connection.bind(bindDN, bindPassword.toCharArray());
	}

	private OpenDJContext(Connection connection) {
		this.connection = connection;
	}

	/**
	 * 
	 * @param urls
	 *            LDAP URL in format of ldap(s)://ldap_url:ldap_port, if there
	 *            are multiple urls presented, separate with a comma.
	 * @param bindDN
	 * @param bindPassword
	 * @throws LdapException
	 * @throws GeneralSecurityException
	 */
	public static OpenDJContext newPlainSASLBind(String urls, String bindDN, String bindPassword)
			throws LdapException, GeneralSecurityException {
		final List<ConnectionFactory> factories = new LinkedList<ConnectionFactory>();

		for (String ldapURL : urls.split(",")) {
			String[] php = ldapURL.split(":");
			Options options = Options.defaultOptions();

			if ("ldaps".equalsIgnoreCase(php[0])) {
				SSLContext sslContext = new SSLContextBuilder().setTrustManager(TrustManagers.trustAll())
						.getSSLContext();
				options.set(org.forgerock.opendj.ldap.LDAPConnectionFactory.SSL_CONTEXT, sslContext);
				options.set(org.forgerock.opendj.ldap.LDAPConnectionFactory.SSL_USE_STARTTLS, false);
			}

			factories.add(new LDAPConnectionFactory(php[1].replace("/", ""), Integer.parseInt(php[2]), options));
		}
		final ConnectionFactory factory = Connections.newFailoverLoadBalancer(factories, Options.defaultOptions());
		Connection connection = factory.getConnection();

		BindRequest b = Requests.newPlainSASLBindRequest(bindDN, bindPassword.toCharArray());
		connection.bind(b);
		return new OpenDJContext(connection);
	}

	public Attribute getAttribute(String dn, String attrID) throws LdapException {

		String[] attrIds = { attrID };
		// System.out.println(dn+":"+attrID);
		SearchResultEntry entry = connection.searchSingleEntry(dn, SearchScope.BASE_OBJECT, "objectClass=*", attrIds);

		if (entry.getAttribute(attrID) == null) {
			return new LinkedAttribute(attrID);
		} else {
			return entry.getAttribute(attrID);
		}
	}

	public Attribute getAttribute(String dn, String attrID, String filter) throws LdapException {

		String[] attrIds = { attrID };
		// System.out.println(dn+":"+attrID);
		SearchResultEntry entry = connection.searchSingleEntry(dn, SearchScope.BASE_OBJECT, filter, attrIds);

		if (entry.getAttribute(attrID) == null) {
			return new LinkedAttribute(attrID);
		} else {
			return entry.getAttribute(attrID);
		}
	}
	
	public Set<String> getMultiValueAttribute(String dn, String attrID) throws LdapException {

		String[] attrIds = { attrID };
		// System.out.println(dn+":"+attrID);
		SearchResultEntry entry = connection.searchSingleEntry(dn, SearchScope.BASE_OBJECT, "objectClass=*", attrIds);

		return entry.parseAttribute(attrID).asSetOfString("");
	}

	public void createSunservice(String ou, String containerDN) throws LdapException {
		Entry entry = new LinkedHashMapEntry("ou=" + ou + "," + containerDN).addAttribute("objectclass", "sunservice")
				.addAttribute("objectclass", "top").addAttribute("ou", ou);

		connection.add(entry);
	}

	public void createOU(String ou, String containerDN) throws LdapException {
		Entry entry = new LinkedHashMapEntry("ou=" + ou + "," + containerDN)
				.addAttribute("objectclass", "organizationalUnit").addAttribute("objectclass", "top")
				.addAttribute("ou", ou);

		connection.add(entry);
	}

	public void setAttribute(String dn, String attrID, String value) throws LdapException {
		Entry entry = connection.searchSingleEntry(dn, SearchScope.BASE_OBJECT, "objectClass=*");
		Entry old = TreeMapEntry.deepCopyOfEntry(entry);
		entry.replaceAttribute(attrID, value);
		ModifyRequest request = Entries.diffEntries(old, entry);

		if (!request.getModifications().isEmpty()) {
			connection.modify(request);
		}

	}

	public void addAttribute(String dn, String attrID, String value) throws LdapException {
		Entry entry = connection.searchSingleEntry(dn, SearchScope.BASE_OBJECT, "objectClass=*");
		Entry old = TreeMapEntry.deepCopyOfEntry(entry);
		entry = entry.addAttribute(attrID, value);
		ModifyRequest request = Entries.diffEntries(old, entry);

		if (!request.getModifications().isEmpty()) {
			connection.modify(request);
		}

	}

	public void removeAttribute(String dn, String attrID) throws LdapException {
		Entry entry = connection.searchSingleEntry(dn, SearchScope.BASE_OBJECT, "objectClass=*");
		Entry old = TreeMapEntry.deepCopyOfEntry(entry);
		entry = entry.removeAttribute(attrID);
		ModifyRequest request = Entries.diffEntries(old, entry);

		if (!request.getModifications().isEmpty()) {
			connection.modify(request);
		}

	}

	public void removeAttribute(String dn, String attrID, String value) throws LdapException {
		Entry entry = connection.searchSingleEntry(dn, SearchScope.BASE_OBJECT, "objectClass=*");
		Entry old = TreeMapEntry.deepCopyOfEntry(entry);
		entry = entry.removeAttribute(attrID, value);
		ModifyRequest request = Entries.diffEntries(old, entry);

		if (!request.getModifications().isEmpty()) {
			connection.modify(request);
		}

	}

	public List<String> list(String dn) throws LdapException, SearchResultReferenceIOException {
		ConnectionEntryReader entry = connection.search(dn, SearchScope.SINGLE_LEVEL, "objectClass=*");
		List<String> result = new ArrayList<String>();
		while (entry.hasNext()) {
			result.add(entry.readEntry().getName().toString());
		}
		return result;
	}

	/**
	 * 
	 * @param filter
	 *            use uid=sb or cn=* alike.
	 * @param maxReturn
	 * @return
	 * @throws NamingException
	 * @throws SearchResultReferenceIOException
	 * @throws LdapException
	 */
	public List<String> search(String baseDN, String filter)
			throws NamingException, LdapException, SearchResultReferenceIOException {

		ConnectionEntryReader entry = connection.search(baseDN, SearchScope.WHOLE_SUBTREE, filter);
		List<String> result = new ArrayList<String>();
		while (entry.hasNext()) {
			result.add(entry.readEntry().getName().toString());
		}
		return result;
	}

	public List<String> searchSub(String baseDN, String filter)
			throws NamingException, LdapException, SearchResultReferenceIOException {

		ConnectionEntryReader entry = connection.search(baseDN, SearchScope.SUBORDINATES, filter);
		List<String> result = new ArrayList<String>();
		while (entry.hasNext()) {
			result.add(entry.readEntry().getName().toString());
		}
		return result;
	}

	public void destroyNode(String dn) throws LdapException {

		connection.delete(dn);
	}

	@Override
	public void close() {
		if (connection != null) {
			connection.close();
		}
		connection = null;
	}

}
