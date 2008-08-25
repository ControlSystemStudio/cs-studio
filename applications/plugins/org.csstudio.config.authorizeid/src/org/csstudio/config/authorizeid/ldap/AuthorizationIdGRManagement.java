package org.csstudio.config.authorizeid.ldap;

import javax.naming.NameAlreadyBoundException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;

/**
 * {@code AuthorizationIdGRManagement} manages AuthorizationIdGR groups (eaig, eair). It can create and 
 * delete them.
 * @author Rok Povsic
 */
public class AuthorizationIdGRManagement {

	private static DirContext _directory;
	
	/**
	 * The logger that is used by this class.
	 */
	private static final CentralLogger LOG = CentralLogger.getInstance();
	
	static{
		_directory = Engine.getInstance().getLdapDirContext();
	}
	
	/**
	 * Inserts new directory to LDAP.
	 * @param _name name of the directory
	 * @param objectClass the object class
	 */
	public void insertNewData(String _name, String _group, ObjectClass2 objectClassGr,
			String _eair, String _eaig) {
		String _fullname = fullName(_name, _group, _eaig, _eair);
		Attributes attrs = attributesForEntry(objectClassGr, _eair, _name, _eaig);
		try{
			_directory.bind(_fullname, null, attrs);
		} catch(NameAlreadyBoundException f) {
			LOG.error(AuthorizationIdManagement.class, "Eaig and eair with that names already exist "+
					"in eain \""+ _name +"\".");
		} catch(Exception f){
			f.printStackTrace();
		}
	}
	
	/**
	 * Gets attributes that are committed when creating new directory.
	 * @param objectClass the object class
	 * @param name name of the directory
	 * @return the result (attributes)
	 */
	private static Attributes attributesForEntry(
			final ObjectClass2 objectClass2, final String eair, final String name, final String eaig) {
		BasicAttributes result = new BasicAttributes();
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
	 */
	public void deleteData(String _name, String _eair, String _eaig, String _group) {
		String _fullname = fullName(_name, _group, _eaig, _eair);
		try{
			_directory.unbind(_fullname);
		} catch(Exception f) {
			f.printStackTrace();
		}
	}
	
	/**
	 * Returns full name of given name and group.
	 * @param _name the name of AuthorizeId
	 * @param _group the group AuthorizeId is in
	 * @return full name
	 */
	private String fullName(String _name, String _group, String _eaig, String _eair) {
		String _fullname = "eair="+ _eair +"+eain="+ _name +"+eaig="+ _eaig +",eain="+ _name +
		",ou="+ _group +",ou=EpicsAuthorizeID";
		return _fullname;
	}
}
