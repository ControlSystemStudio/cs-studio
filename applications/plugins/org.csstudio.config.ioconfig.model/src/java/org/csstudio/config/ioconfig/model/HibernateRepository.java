package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.platform.logging.CentralLogger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * Implementation for a Hibernate Repository.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.9 $
 * @since 03.06.2009
 */
public class HibernateRepository implements IRepository {

    protected static final Logger LOG = CentralLogger.getInstance()
            .getLogger(HibernateRepository.class);

    /**
     *
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.9 $
     * @since 08.04.2010
     */
    private final class EpicsAddressHibernateCallback implements HibernateCallback {
        private final String _ioName;

        protected EpicsAddressHibernateCallback(@Nonnull final String ioName) {
            _ioName = ioName;
        }

        @Override
        @Nonnull
		public String execute(@Nonnull final Session session) {
            final Query query = session
					.createQuery("select channel.epicsAddressString from "
							+ ChannelDBO.class.getName()
							+ "  as channel where channel.ioName like '"
							+ _ioName + "'");

            final List<String> channels = doQuery(query);
            if (channels.size() < 1) {
                return "%%% IO-Name (" + _ioName + ") NOT found! %%%";
            } else if (channels.size() > 1) {
                StringBuilder sb = new StringBuilder("%%% IO-Name (");
                sb.append(_ioName);
                sb.append(" NOT Unique! %%% ");
                for (String string : channels) {
                    sb.append(" ,");
                    sb.append(string);
                }
                return sb.toString();
            }
            return channels.get(0);
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        private List<String> doQuery(@Nonnull final Query query) {
            return query.list();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public GSDModuleDBO saveWithChildren(@Nonnull final GSDModuleDBO gsdModule) throws PersistenceException {
        try {
            HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {
                private Session _session;

                @Override
				public GSDModuleDBO execute(@Nonnull final Session session) {
                    _session = session;
                    Set<ModuleChannelPrototypeDBO> values = gsdModule.getModuleChannelPrototypeNH();
                    _session.saveOrUpdate(gsdModule);
                    if ( (values != null) && (values.size() > 0)) {
                        saveChildren(values);
                    }
                    _session.flush();
                    return gsdModule;
                }

                private void saveChildren(@Nonnull final Set<ModuleChannelPrototypeDBO> moduleChannelPrototypes) {
                    for (ModuleChannelPrototypeDBO prototype : moduleChannelPrototypes) {
                        _session.saveOrUpdate(prototype);
                    }
                }
            });
            return gsdModule;
        } catch (HibernateException he) {
            CentralLogger.getInstance().warn(Repository.class, he);
            throw new PersistenceException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <T extends DBClass> T saveOrUpdate(@Nonnull final T dbClass) throws PersistenceException {
        try {
            HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {

                @Override
				public T execute(@Nonnull final Session session) {
                    session.saveOrUpdate(dbClass);
                    return dbClass;
                }

            });
            return dbClass;
        } catch (HibernateException he) {
            CentralLogger.getInstance().warn(Repository.class, he);
            PersistenceException persistenceException = new PersistenceException();
            persistenceException.setStackTrace(he.getStackTrace());
            throw persistenceException;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <T extends DBClass> T update(@Nonnull final T dbClass) {

        HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {

            @Override
			public T execute(@Nonnull final Session session) {
                dbClass.setUpdatedOn(new Date());
                session.update(dbClass);
                session.flush();
                return dbClass;
            }

        });
        return dbClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <T> List<T> load(@Nonnull final Class<T> clazz) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public List<T> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + clazz.getName());
                final List<T> nodes = query.list();

                if (nodes.isEmpty()) {
                    return null;
                }
                return nodes;
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <T> T load(@Nonnull final Class<T> clazz,@Nonnull  final Serializable id) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public T execute(@Nonnull final Session session) {
                final List<T> nodes = session.createQuery("select c from " + clazz.getName()
                        + " c where c.id = :id").setString("id", id.toString()).list();

                if (nodes.isEmpty()) {
                    return null;
                }

                return nodes.get(0);
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <T extends DBClass> void removeNode(@Nonnull final T dbClass) {
        HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {

            @Override
			public Object execute(@Nonnull final Session session) {
                session.delete(dbClass);
                return null;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public GSDFileDBO save(@Nonnull final GSDFileDBO gsdFile) {

        HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {

            @Override
			public GSDFileDBO execute(@Nonnull final Session session) {
                session.saveOrUpdate(gsdFile);
                return gsdFile;
            }

        });
        return gsdFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void removeGSDFiles(@Nonnull final GSDFileDBO gsdFile) {
        HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {

            @Override
			public Object execute(@Nonnull final Session session) {
                session.delete(gsdFile);
                return null;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public List<DocumentDBO> loadDocument() {
        return HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public List<DocumentDBO> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + DocumentDBO.class.getName()
                        + " where length(image) > 0");
                final List<DocumentDBO> nodes = query.list();
                if (nodes.isEmpty()) {
                    return null;
                }
                return nodes;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public DocumentDBO save(@Nonnull final DocumentDBO document) {

        HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {

            @Override
			public DocumentDBO execute(@Nonnull final Session session) {
                session.saveOrUpdate(document);
                session.flush();
                return document;
            }

        });
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public DocumentDBO update(@Nonnull final DocumentDBO document) {

        HibernateManager.getInstance().doInDevDBHibernate(new HibernateCallback() {

            @Override
			public DocumentDBO execute(@Nonnull final Session session) {
                document.setUpdateDate(new Date());
                session.update(document);
                session.flush();
                return document;
            }

        });
        return document;
    }

    /**
     * Get the Epics Address string to an IO Name. It the name not found return the string '$$$
     * IO-Name NOT found! $$$'.
     *
     * @param ioName
     *            the IO-Name.
     * @return the Epics Adress for the given IO-Name.
     */
    @Override
	public String getEpicsAddressString(@Nonnull final String ioName) {
        HibernateCallback hibernateCallback = new EpicsAddressHibernateCallback(ioName);
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public List<String> getIoNames() {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public List<String> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("select channel.ioName from "
                        + ChannelDBO.class.getName() + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public List<String> getIoNames(@Nonnull final String iocName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public List<String> execute(@Nonnull final Session session) {
                // TODO: Der IOC name wird noch nicht mir abgefragt!
                final Query query = session.createQuery("select channel.ioName from "
                        + ChannelDBO.class.getName() + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    @Override
    public String getShortChannelDesc(@Nonnull final String ioName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public String execute(@Nonnull final Session session) {
                final Query query = session.createQuery("select channel.description from "
                        + ChannelDBO.class.getName() + " as channel where channel.ioName like ?");
                query.setString(0, ioName); // Zero-Based!

                final List<String> descList = query.list();
                if ( (descList == null) || descList.isEmpty()) {
                    return "";
                }
                String string = descList.get(0);
                if ( (string == null) || string.isEmpty()) {
                    return "";
                }
                String[] split = string.split("[\r\n]");
                if (split[0].length() > 40) {
                    return split[0].substring(0, 40);
                }
                return split[0];
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    @Override
	public List<SensorsDBO> loadSensors(@Nonnull final String ioName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public List<SensorsDBO> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + SensorsDBO.class.getName()
                        + " as sensors where sensors.ioName like ?");
                query.setString(0, ioName); // Zero-Based!

                final List<SensorsDBO> sensors = query.list();
                return sensors;
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    @Override
	public SensorsDBO loadSensor(@Nonnull final String ioName,@Nonnull  final String selection) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			@SuppressWarnings("unchecked")
            public SensorsDBO execute(@Nonnull final Session session) {
                String statment = "select" + " s" + " from " + SensorsDBO.class.getName() + " s"
                        + ", " + ChannelDBO.class.getName() + " c" + " where c.currentValue like s.id"
                        + " and c.ioName like '" + ioName + "'";
                final Query query = session.createQuery(statment);
                final List<SensorsDBO> sensors = query.list();
                if ( (sensors == null) || (sensors.size() < 1)) {
                    return null;
                }
                return sensors.get(0);
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    @CheckForNull
    public List<Integer> getRootPath(final int id) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			public List<Integer> execute(@Nonnull final Session session) {
                int level = 0;
                int searchId = id;
                String statment = "select node.parent_Id  from ddb_node node where node.id like ?";
                final List<Integer> rootPath = new ArrayList<Integer>();
                rootPath.add(searchId);
                SQLQuery query = session.createSQLQuery(statment);
                while (searchId > 0) {
                    query.setInteger(0, searchId); // Zero-Based!
                    BigDecimal uniqueResult = (BigDecimal) query.uniqueResult();
                    if ( (uniqueResult == null) || (level++ > 10)) {
                        break;
                    }
                    searchId = uniqueResult.intValue();
                    rootPath.add(searchId);
                }
                return rootPath;
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    @Override
    @CheckForNull
    public ChannelDBO loadChannel(@Nullable final String ioName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
			public ChannelDBO execute(@Nonnull final Session session) {
                if(ioName==null) {
                    return null;
                }
                Query createQuery = session.createQuery("select c from " + ChannelDBO.class.getName()
                        + " c where c.ioName like ?");
                createQuery.setString(0, ioName);
                final ChannelDBO nodes = (ChannelDBO) createQuery.uniqueResult();
                return nodes;
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public List<PV2IONameMatcherModelDBO> loadPV2IONameMatcher(@Nullable final Collection<String> pvName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @Override
            public List<PV2IONameMatcherModelDBO> execute(@Nonnull final Session session) {
                if ( (pvName == null) || (pvName.size() == 0)) {
                    return null;
                }
                StringBuilder statement = new StringBuilder("select pv from ")
                        .append(PV2IONameMatcherModelDBO.class.getName()).append(" pv where ");
                boolean notFirst = false;
                for (String string : pvName) {
                    if (notFirst) {
                        statement.append(" OR ");
                    }
                    statement.append("pv.epicsName = '").append(string).append("'");
                    notFirst = true;
                }
                try {
                    Query createQuery = session.createQuery(statement.toString());
                    return createQuery.list();
                } catch (HibernateException e) {
                    LOG.warn("Hibernate Statement: " + statement);
                    throw e;
                }
            }
        };
        return HibernateManager.getInstance().doInDevDBHibernate(hibernateCallback);
    }

    @Override
    public void close() {
        HibernateManager.getInstance().closeSession();
    }

}
