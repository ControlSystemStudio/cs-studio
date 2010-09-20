package org.csstudio.config.authorizeid.ldap;

import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;

import org.csstudio.config.authorizeid.AuthorizeIdActivator;
import org.csstudio.utility.ldap.service.ILdapService;

/**
 * {@code AuthorizationIdGRManagement} manages AuthorizationIdGR groups (eaig, eair). It can create and
 * delete them.
 * @author Rok Povsic
 */
public class AuthorizationIdGRManagement {

	/**
	 * Inserts new directory to LDAP.
	 * @param _name name of the directory
	 * @param objectClass the object class
	 * @throws InvalidNameException
	 * @throws ServiceUnavailableException
	 */
	public void insertNewData(final String _name, final String _group, final ObjectClass2 objectClassGr,
			final String _eair, final String _eaig) throws InvalidNameException, ServiceUnavailableException {
		final String _fullname = fullName(_name, _group, _eaig, _eair);
		final Attributes attrs = attributesForEntry(objectClassGr, _eair, _name, _eaig);
		final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
		if (service == null) {
		    throw new ServiceUnavailableException("LDAP service not available. Insertion of new data failed.");
		}
		service.createComponent(new LdapName(_fullname), attrs);
	}

	/**
	 * Gets attributes that are committed when creating new directory.
	 * @param objectClass the object class
	 * @param name name of the directory
	 * @return the result (attributes)
	 */
	private static Attributes attributesForEntry(
			final ObjectClass2 objectClass2, final String eair, final String name, final String eaig) {
		final BasicAttributes result = new BasicAttributes();
		result.put("objectClass", objectClass2.getObjectClassName());
		result.put("objectClass", objectClass2.getObjectClassGrName());
		result.put(objectClass2.getEair(), eair);
		result.put(objectClass2.getEain(), name);
		result.put(objectClass2.getEaig(), eaig);
		return result;
	}

	/**
	 * Deletes AuthorizeId from LDAP.
	 * @param _name the name of AuthorizeId
	 * @param _group the group AuthorizeId is in.
	 * @throws ServiceUnavailableException
	 * @throws InvalidNameException
	 */
	public void deleteData(final String _name, final String _eair, final String _eaig, final String _group) throws ServiceUnavailableException, InvalidNameException {
		final String _fullname = fullName(_name, _group, _eaig, _eair);
		final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
		if (service == null) {
		    throw new ServiceUnavailableException("LDAP service not available. Deletion of data failed.");
		}
		service.removeLeafComponent(new LdapName(_fullname));
	}

	/**
	 * Returns full name of given name and group.
	 * @param _name the name of AuthorizeId
	 * @param _group the group AuthorizeId is in
	 * @return full name
	 */
	private String fullName(final String _name, final String _group, final String _eaig, final String _eair) {
		final String _fullname = "eair="+ _eair +"+eain="+ _name +"+eaig="+ _eaig +",eain="+ _name +
		",ou="+ _group +",ou=EpicsAuthorizeID";
		return _fullname;
	}
}
