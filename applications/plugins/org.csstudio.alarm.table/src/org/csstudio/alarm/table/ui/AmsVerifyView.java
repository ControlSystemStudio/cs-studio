/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, Member of the Helmholtz Association,
 * (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.ui;


import java.text.SimpleDateFormat;

import javax.jms.MapMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.jms.ISendMapMessage;
import org.csstudio.alarm.table.preferences.verifier.AmsVerifyViewPreferenceConstants;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.desy.startuphelper.CSSPlatformInfo;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View to verify functionality of AMS. Add to {@link LogView} some buttons to send test messages.
 *
 * @see LogView
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class AmsVerifyView extends LogView {

    private static final Logger LOG = LoggerFactory.getLogger(AmsVerifyView.class);
    
    public static final String AMS_VERIFY_VIEW_ID = AmsVerifyView.class.getName();

    private static final String JMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"; //$NON-NLS-1$

    /**
     * Creates the view for the alarm log table.
     *
     * @param parent
     */
    @Override
    public void createPartControl(final Composite parent) {
        _parent = parent;

        // Read column names and JMS topic settings from preferences
        setTopicSetColumnService(JmsLogsPlugin.getDefault().getTopicSetColumnServiceForVerifyViews());
        setTopicSetService(JmsLogsPlugin.getDefault().getTopicsetServiceForLogViews());
        defineCurrentTopicSetName();

        final GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        parent.setLayout(grid);

        createMessageArea(_parent);

        final Composite logTableManagementComposite = new Composite(parent, SWT.NONE);

        final RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 15;
        logTableManagementComposite.setLayout(layout);

        addJmsTopicItems(logTableManagementComposite);
        addVerifyItems(logTableManagementComposite);
        addRunningSinceGroup(logTableManagementComposite);
        
        initializeMessageTable();
        
        new ControlSystemDragSource(_tableViewer.getTable()) {
        	
        	@Override
        	public Object getSelection() {
        		final Object[] o = ((IStructuredSelection) _tableViewer.getSelection()).toArray();
        		final ProcessVariable[] pv = new ProcessVariable[o.length];
        		for (int i=0; i<pv.length; ++i) {
        			pv[i] = new ProcessVariable(((BasicMessage) o[i]).getName());
        		}
        		return pv;
        	}
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeMessageTable() {

        // Initialize JMS message list
        if (_columnMapping != null) {
            _columnMapping.saveColumn(AmsVerifyViewPreferenceConstants.P_STRING,
                                      AmsVerifyViewPreferenceConstants.TOPIC_SET);
        }

        // is there already a MessageTable delete it and the message list.
        if (_messageTable != null) {
            _messageTable.disposeMessageTable();
            _tableViewer = null;
            _messageTable = null;
            // _messageList = null;
        }

        if (_tableComposite != null) {
            _tableComposite.dispose();
            _tableComposite = null;
        }
        _tableComposite = new Composite(_parent, SWT.NONE);
        final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        _tableComposite.setLayoutData(gridData);
        final GridLayout grid2 = new GridLayout();
        grid2.numColumns = 1;
        _tableComposite.setLayout(grid2);

        // _messageList = new LogMessageList(100);
        // setup message table with context menu etc.
        _tableViewer = new TableViewer(_tableComposite, SWT.MULTI | SWT.FULL_SELECTION);

        // get the font for the selected topic set. If there was no font defined
        // in preferences set no font.
        final Font font = getTopicSetColumnService().getFont(getCurrentTopicSetName());
        if (font != null) {
            _tableViewer.getTable().setFont(font);
        }
        final GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true, true);
        _tableViewer.getTable().setLayoutData(gridData2);

        final AbstractMessageList messageList = getOrCreateCurrentMessageList(); // Uses LogMessageList
        _messageTable = new MessageTable(_tableViewer, getTopicSetColumnService()
                                         .getColumnSet(getCurrentTopicSetName()), messageList);
        _messageTable.makeContextMenu(getSite());

        _columnMapping = new ExchangeableColumnWidthPreferenceMapping(_tableViewer,
                                                                      getCurrentTopicSetName());
        setCurrentTimeToRunningSince(messageList.getStartTime());
        getSite().setSelectionProvider(_tableViewer);
        createAndRegisterActions();
        _parent.layout();

    }

    private void addVerifyItems(final Composite logTableManagementComposite) {

        final Group verifyItemGroup = new Group(logTableManagementComposite, SWT.NONE);

        verifyItemGroup.setText(Messages.AmsVerifyView_AmsActionsPruefen);

        final RowLayout layout = new RowLayout();
        verifyItemGroup.setLayout(layout);

        final Button verify1Button = new Button(verifyItemGroup, SWT.PUSH);
        verify1Button.setLayoutData(new RowData(60, 21));
        verify1Button.setText("SMS"); //$NON-NLS-1$

        verify1Button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                sendVerifyMessage("#MODEMTEST#"); //$NON-NLS-1$
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
        });

        final Button verify2Button = new Button(verifyItemGroup, SWT.PUSH);
        verify2Button.setLayoutData(new RowData(60, 21));
        verify2Button.setText("Voice Mail"); //$NON-NLS-1$

        verify2Button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                sendVerifyMessage("#VOICEMAILTEST#"); //$NON-NLS-1$
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
        });

        final Button verify3Button = new Button(verifyItemGroup, SWT.PUSH);
        verify3Button.setLayoutData(new RowData(60, 21));
        verify3Button.setText("E-Mail"); //$NON-NLS-1$

        verify3Button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                sendVerifyMessage("#MAILTEST#"); //$NON-NLS-1$
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
        });

        final Button verify4Button = new Button(verifyItemGroup, SWT.PUSH);
        verify4Button.setLayoutData(new RowData(60, 21));
        verify4Button.setText("JMS Topic"); //$NON-NLS-1$

        verify4Button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                sendVerifyMessage("#JMSCONNECTORTEST#"); //$NON-NLS-1$
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
        });
    }

    private void sendVerifyMessage(final String textPropertyValue) {
        try {
            final ISendMapMessage sender = JmsLogsPlugin.getDefault().getSendMapMessage();

            final SimpleDateFormat sdf = new SimpleDateFormat(JMS_DATE_FORMAT);
            final java.util.Date currentDate = new java.util.Date();
            final String time = sdf.format(currentDate);

            final MapMessage mapMessage = sender.getSessionMessageObject("ALARM"); //$NON-NLS-1$

            // Add username and host to acknowledge message.
            final User user = SecurityFacade.getInstance().getCurrentUser();
            if (user != null) {
                mapMessage.setString("USER", user.getUsername()); //$NON-NLS-1$
            } else {
                mapMessage.setString("USER", "NULL"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            final String host = CSSPlatformInfo.getInstance().getQualifiedHostname();
            if (host != null) {
                mapMessage.setString("HOST", host); //$NON-NLS-1$
            } else {
                mapMessage.setString("HOST", "NULL"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            mapMessage.setString("EVENTTIME", time); //$NON-NLS-1$
            mapMessage.setString("TEXT", textPropertyValue); //$NON-NLS-1$
            mapMessage.setString("NAME", "AMSCOMMONTEST"); //$NON-NLS-1$ //$NON-NLS-2$

            JmsLogsPlugin.logInfo("Verify Ams system with " //$NON-NLS-1$
                                  + textPropertyValue); //$NON-NLS-2$
            sender.sendMessage("ALARM"); //$NON-NLS-1$
        } catch (final Exception e) {
            LOG.error("Send message error: ", e); //$NON-NLS-1$
        }
    }
}
