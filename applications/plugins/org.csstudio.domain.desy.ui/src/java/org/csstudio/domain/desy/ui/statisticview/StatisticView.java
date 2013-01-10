/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id$
 */
package org.csstudio.domain.desy.ui.statisticview;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.csstudio.domain.common.statistic.CollectorSupervisor;
import org.csstudio.domain.desy.ui.DomainDesyActivator;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.07.2007
 */
public class StatisticView extends ViewPart {
    /**
     *
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 19.07.2007
     */
    private class XMLCollectorApplication {
        public HashMap<String, String> propertie;
        public String application;
    }

    /**
     *
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 20.07.2007
     */
    private class XMLCollector {
        /** The host of the statistics. */
        private String _host;
        /** The User of the Host. */
        private String _user;
        /** The XMPP User of the Host. */
        private String _xmppUser;
        /** The statistic xml message. */
        private String _message;
        /** The cration Time. */
        private Date _date;

        /**
         * The Constructor.
         *
         * @param host
         *            The host wich was sending the statistics
         * @param user
         *            The user of the Host
         * @param xmppUser
         *            The xmppUser of the Host.
         * @param message
         */
        public XMLCollector(final String host, final String user, final String xmppUser,
                final String message) {
            setHost(host);
            setUser(user);
            setXmppUser(xmppUser);
            setMessage(message);
            setDateNow();
        }

        /** @return the Host */
        public final String getHost() {
            return _host;
        }

        /**
         * @param host
         *            Set the Host.
         */
        public final void setHost(final String host) {
            this._host = host;
        }

        /** @return the Host user. */
        public final String getUser() {
            return _user;
        }

        /**
         * @param user
         *            the user of the host.
         */
        public final void setUser(final String user) {
            this._user = user;
        }

        /** @return the Host xmppuser. */
        public final String getXmppUser() {
            return _xmppUser;
        }

        /**
         * @param xmppUser
         *            the xmPPuser of the host.
         */
        public final void setXmppUser(final String xmppUser) {
            _xmppUser = xmppUser;
        }

        /** @return the creation Time. */
        public final Date getDate() {
            return _date;
        }

        /** Set the CreationTime. */
        private void setDateNow() {
            _date = new Date();

        }

        /** @return the XML statistic message. */
        public final String getMessage() {
            return _message;
        }

        /**
         * @param message
         *            the XML statistic message.
         */
        public final void setMessage(final String message) {
            this._message = message;
        }

    }

    /** {@inheritDoc} */
    public class MyTableLabelProvider extends LabelProvider implements ITableLabelProvider {
        /** The values for the Columns alignment. */
        private final int[] _columnAlignment = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,
                SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,
                SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
        /** The values for the Columns width. */
        private static final int WIDTH = 75;
        /** The values for the Columns width. */
        private final int[] _columnWidth = new int[] { WIDTH, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH,
                WIDTH, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH };
        /** The names for the Columns. */
        private final String[] _columnNames = new DefaultScope().getNode(DomainDesyActivator.PLUGIN_ID).get(
                PreferenceConstants.STATISTICVIEW_COLUMNS, "").split(",");

        /** {@inheritDoc} */
        public final int getColumnCount() {
            return _columnNames.length;
        }

        /** {@inheritDoc} */
        public final String[] getColumnNames() {
            return _columnNames;
        }

        /** {@inheritDoc} */
        public final int[] getColumnWidth() {
            return _columnWidth;
        }

        /** {@inheritDoc} */
        public final int[] getColumnAlignment() {
            return _columnAlignment;
        }

        /** {@inheritDoc} */
        @Override
        public final String getColumnText(final Object obj, final int columnIndex) {
            if (obj instanceof XMLCollectorApplication) {
                final XMLCollectorApplication element = (XMLCollectorApplication) obj;
                final String tmp = element.propertie.get(_columnNames[columnIndex]);
                if (tmp == null) {
                    return "N/A";
                }
                return tmp;

            }
            return "";
        }

        /** {@inheritDoc} */
        public final void createColumns(final Table table) {
            for (int i = 0; i < getColumnCount(); i++) {
                final TableColumn column = new TableColumn(table, getColumnAlignment()[i], i);
                column.setText(getColumnNames()[i]);
                column.setWidth(getColumnWidth()[i]);
            }
        }

        /** {@inheritDoc} */
        @Override
        public final Image getColumnImage(final Object element, final int columnIndex) {
            return null;
        }
    }

    /** The view´s ID. */
    public static final String VIEW_ID = "org.csstudio.domain.desy.ui.StatisticView";
    /** The Statistic Table. */
    private TableViewer _statistcTableViewer;
    /** The parent Compposite. */
    private XMLCollector _statistic;

    /** Constructor. */
    public StatisticView() {
        // TODO Auto-generated constructor stub
    }

    /** {@inheritDoc} */
    @Override
    public final void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(6, false));
        GridData layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
        new Label(parent, SWT.NONE).setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false,
                1, 1));
        final Button clearButton = new Button(parent, SWT.PUSH);
        layoutData = new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1);
        layoutData.minimumWidth = 50;
        layoutData.widthHint = 50;
        clearButton.setLayoutData(layoutData);
        final Button applyButton = new Button(parent, SWT.PUSH);
        clearButton.setText("C&lear");
        clearButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _statistcTableViewer.getTable().removeAll();
            }

        });
        layoutData = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
        layoutData.minimumWidth = 50;
        layoutData.widthHint = 50;
        applyButton.setLayoutData(layoutData);
        applyButton.setText("&Local");
        applyButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
                setLocalStatistic();
            }

        });
        _statistcTableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.BORDER);
        final MyTableLabelProvider lp = new MyTableLabelProvider();
        _statistcTableViewer.setLabelProvider(lp);
        _statistcTableViewer.setColumnProperties(lp._columnNames);
        lp.createColumns(_statistcTableViewer.getTable());
        _statistcTableViewer.getTable().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
        _statistcTableViewer.getTable().setHeaderVisible(true);
        _statistcTableViewer.getTable().setLinesVisible(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    /** Fill the Table with the local statistics. */
    private void setLocalStatistic() {
        final CollectorSupervisor cs = CollectorSupervisor.getInstance();
        setMessage(cs.getCollectionAsXMLString(), "horst", "user", "xmppUser");
    }

    /** Fill the Table with the remote statistics. */
    private void fillTable() {
        final String attName = "Name";
        final SAXBuilder saxB = new SAXBuilder();
        final StringReader sr = new StringReader(_statistic.getMessage());
//        CentralLogger.getInstance().debug(this, _statistic.getMessage());
        try {
            final Document document = saxB.build(sr);
            final Element root = document.getRootElement(); // <StatisticProtocol Version="0.1">
            if (root.getAttribute("Version").getDoubleValue() != 0.1) {
                return;
            }
            _statistcTableViewer.add(_statistic);
            final Element supervisor = root.getChild("CollectionSupervisor"); // <CollectionSupervisor
                                                                        // size="7">

            final List<Element> collectors = supervisor.getChildren(); // <Collector>
            for (final Element collector : collectors) {
                final List<Element> column = collector.getChildren();
                final XMLCollectorApplication xmlC = new XMLCollectorApplication();
                xmlC.propertie = new HashMap<String, String>();
                for (final Element element : column) {
                    final String prop = element.getAttributeValue(attName);
                    String value = element.getValue();
//                    CentralLogger.getInstance().debug(this,"Prop: " + prop + "\t\t\tValue:" + value + "\t\t"
//                            + element.getText());
                    if (prop != null) {
                        if (value == null) {
                            value = "";
                        }
                        xmlC.propertie.put(prop, value);
                        if (prop.equals("Application")) {
                            xmlC.application = element.getTextTrim();
                        }
                    }
                }
                _statistcTableViewer.add(xmlC);
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @param message
     *            The message string with statistics
     * @param host
     *            The host wich send the statistics
     * @param user
     *            The local user
     * @param xmppUser
     *            The remote user
     */
    public final void setMessage(final String message, final String host, final String user,
            final String xmppUser) {
        _statistic = new XMLCollector(host, user, xmppUser, message);
        fillTable();
    }

}
