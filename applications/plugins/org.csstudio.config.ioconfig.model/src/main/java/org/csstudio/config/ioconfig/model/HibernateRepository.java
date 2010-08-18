package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModule;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototype;
import org.csstudio.platform.logging.CentralLogger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * Implementation for a Hibernate Repository.
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 03.06.2009
 */
public class HibernateRepository implements IRepository {

    /**
     * {@inheritDoc}
     */
    public GSDModule saveWithChildren(final GSDModule gsdModule) throws PersistenceException {
        try {
            HibernateManager.doInDevDBHibernate(new HibernateCallback() {

                private Session _session;

                public GSDModule execute(final Session session) {
                    _session = session;
                    Set<ModuleChannelPrototype> values = gsdModule.getModuleChannelPrototypeNH();
                    _session.saveOrUpdate(gsdModule);
                    if (values != null && values.size() > 0) {
                        saveChildren(values);
                    }
                    _session.flush();
                    return gsdModule;
                }

                private void saveChildren(Set<ModuleChannelPrototype> moduleChannelPrototypes) {
                    for (ModuleChannelPrototype prototype : moduleChannelPrototypes) {
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
    public <T extends DBClass> T saveOrUpdate(final T dbClass) throws PersistenceException {
        try {
            HibernateManager.doInDevDBHibernate(new HibernateCallback() {

                public T execute(final Session session) {
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
    public <T extends DBClass> T update(final T dbClass) {

        HibernateManager.doInDevDBHibernate(new HibernateCallback() {

            public T execute(final Session session) {
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
    public <T> List<T> load(final Class<T> clazz) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public List<T> execute(final Session session) {
                final Query query = session.createQuery("from " + clazz.getName());
                final List<T> nodes = query.list();

                if (nodes.isEmpty()) {
                    return null;
                }
                return nodes;
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T load(final Class<T> clazz, final Serializable id) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public T execute(final Session session) {
                final List<T> nodes = session.createQuery(
                        "select c from " + clazz.getName() + " c where c.id = :id").setString("id",
                        id.toString()).list();

                if (nodes.isEmpty()) {
                    return null;
                }

                return (T) nodes.get(0);
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends DBClass> void removeNode(final T dbClass) {
        HibernateManager.doInDevDBHibernate(new HibernateCallback() {

            public Object execute(final Session session) {
                session.delete(dbClass);
                return null;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public GSDFile save(final GSDFile gsdFile) {

        HibernateManager.doInDevDBHibernate(new HibernateCallback() {

            public GSDFile execute(final Session session) {
                session.saveOrUpdate(gsdFile);
                return gsdFile;
            }

        });
        return gsdFile;
    }

    /**
     * {@inheritDoc}
     */
    public void removeGSDFiles(final GSDFile gsdFile) {
        HibernateManager.doInDevDBHibernate(new HibernateCallback() {

            public Object execute(final Session session) {
                session.delete(gsdFile);
                return null;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public List<Document> loadDocument() {
        return HibernateManager.doInDevDBHibernate(new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public List<Document> execute(final Session session) {
                final Query query = session.createQuery("from " + Document.class.getName()
                        + " where length(image) > 0");
                final List<Document> nodes = query.list();
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
    public Document save(final Document document) {

        HibernateManager.doInDevDBHibernate(new HibernateCallback() {

            public Document execute(final Session session) {
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
    public Document update(final Document document) {

        HibernateManager.doInDevDBHibernate(new HibernateCallback() {

            public Document execute(final Session session) {
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
    public String getEpicsAddressString(final String ioName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public String execute(final Session session) {
                final Query query = session.createQuery("select channel.epicsAddressString from "
                        + Channel.class.getName() + " as channel where channel.ioName like ?");
                query.setString(0, ioName); // Zero-Based!

                final List<String> channels = query.list();
                if (channels.size() < 1) {
                    return "%%% IO-Name (" + ioName + ") NOT found! %%%";
                } else if (channels.size() > 1) {
                    StringBuilder sb = new StringBuilder("%%% IO-Name (");
                    sb.append(ioName);
                    sb.append(" NOT Unique! %%% ");
                    for (String string : channels) {
                        sb.append(" ,");
                        sb.append(string);
                    }
                    return sb.toString();
                }
                return channels.get(0);
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getIoNames() {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public List<String> execute(final Session session) {
                final Query query = session.createQuery("select channel.ioName from "
                        + Channel.class.getName() + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getIoNames(String iocName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public List<String> execute(final Session session) {
                final Query query = session.createQuery("select channel.ioName from "
                        + Channel.class.getName() + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    @Override
    public String getShortChannelDesc(final String ioName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public String execute(final Session session) {
                final Query query = session.createQuery("select channel.description from "
                        + Channel.class.getName() + " as channel where channel.ioName like ?");
                query.setString(0, ioName); // Zero-Based!

                final List<String> descList = query.list();
                if (descList == null || descList.isEmpty()) {
                    return "";
                }
                String string = descList.get(0);
                if (string == null || string.isEmpty()) {
                    return "";
                }
                String[] split = string.split("[\r\n]");
                if (split[0].length() > 40) {
                    return split[0].substring(0, 40);
                }
                return split[0];
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    public List<Sensors> loadSensors(final String ioName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public List<Sensors> execute(final Session session) {
                final Query query = session.createQuery("from " + Sensors.class.getName()
                        + " as sensors where sensors.ioName like ?");
                query.setString(0, ioName); // Zero-Based!

                final List<Sensors> sensors = query.list();
                return sensors;
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    public Sensors loadSensor(final String ioName, final String selection) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public Sensors execute(final Session session) {
                String statment = "select" + " s" + " from " + Sensors.class.getName() + " s"
                        + ", " + Channel.class.getName() + " c" + " where c.currentValue like s.id"
                        + " and c.ioName like '" + ioName + "'";
                final Query query = session.createQuery(statment);
                // query.setString(0, ioName); // Zero-Based!
                final List<Sensors> sensors = query.list();
                if (sensors == null || sensors.size() < 1) {
                    return null;
                }
                return sensors.get(0);
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    public List<Integer> getRootPath(final int id) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            public List<Integer> execute(final Session session) {
                int level = 0;
                int searchId = id;
                String statment = "select node.parent_Id" + " from ddb_node node"
                        + " where node.id like ?";
                final List<Integer> rootPath = new ArrayList<Integer>();
                rootPath.add(searchId);
                SQLQuery query = session.createSQLQuery(statment);
                while (searchId > 0) {
                    query.setInteger(0, searchId); // Zero-Based!
                    BigDecimal uniqueResult = (BigDecimal) query.uniqueResult();
                    if (uniqueResult == null || level++ > 10) {
                        break;
                    }
                    searchId = uniqueResult.intValue();
                    rootPath.add(searchId);
                }
                return rootPath;
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    @Override
    public Channel loadChannel(final String ioName) {
        HibernateCallback hibernateCallback = new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public Channel execute(final Session session) {
                Query createQuery = session.createQuery("select c from " + Channel.class.getName()
                        + " c where c.ioName like ?");
                createQuery.setString(0, ioName);
                final Channel nodes = (Channel) createQuery.uniqueResult();
                return nodes;
            }
        };
        return HibernateManager.doInDevDBHibernate(hibernateCallback);
    }

    @Override
    public void close() {
        HibernateManager.closeSession();
    }
}
