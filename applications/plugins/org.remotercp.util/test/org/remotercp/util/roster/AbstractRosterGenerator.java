package org.remotercp.util.roster;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.Presence;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.RosterEntry;

public class AbstractRosterGenerator {

	public IRosterEntry createRosterEntry(final String rosterName,
			final IRosterGroup group, final IPresence.Type presencetype) {
		final TestNameSpace namespace = new TestNameSpace();
		ID userID = null;

		try {
			userID = namespace.createInstance(new String[] { rosterName });
		} catch (final IDCreateException e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull(userID);

		final IUser user = new User(userID, rosterName);

		final IPresence presence = new Presence(presencetype);

		final RosterEntry rosterEntry = new RosterEntry(group, user, presence);

		return rosterEntry;
	}

	public ID createUserID(final String userName) {
		final TestNameSpace namespace = new TestNameSpace();
		ID userID = null;
		try {
			userID = namespace.createInstance(new String[] { userName });
		} catch (final IDCreateException e) {
			e.printStackTrace();
			fail();
		}

		return userID;
	}

	private class TestID extends BaseID {

		private static final long serialVersionUID = 1L;

		private final String username;

		public TestID(final TestNameSpace namespace, final String username) {
			super(namespace);
			this.username = username;
		}

		@Override
		protected int namespaceCompareTo(final BaseID o) {
			return 0;
		}

		@Override
		protected boolean namespaceEquals(final BaseID o) {
			return o.getName().equals(username);
		}

		@Override
		protected String namespaceGetName() {
			return username;
		}

		@Override
		protected int namespaceHashCode() {
			return username.hashCode();
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof TestID) {
				final TestID id = (TestID) o;
				return username.equals(id.username);
			}
            fail();
            return false;
		}

		@Override
		public int compareTo(final Object o) {
			// TODO Auto-generated method stub
			final TestID compareID = (TestID) o;
			return this.getName().compareTo(compareID.getName());
		}
	}

	private class TestNameSpace extends Namespace {

		private static final long serialVersionUID = 1L;

		@Override
		public ID createInstance(final Object[] parameters) throws IDCreateException {
			final String username = (String) parameters[0];
			return new TestID(this, username);
		}

		@Override
		public String getScheme() {
			return null;
		}

	}
}
