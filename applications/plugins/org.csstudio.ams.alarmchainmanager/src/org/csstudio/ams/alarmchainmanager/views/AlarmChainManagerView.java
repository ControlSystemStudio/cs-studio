
/* 
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.ams.alarmchainmanager.views;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.csstudio.ams.AMSException;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.Utils;
import org.csstudio.ams.alarmchainmanager.AlarmChainManagerPlugin;
import org.csstudio.ams.alarmchainmanager.internal.localization.Messages;
import org.csstudio.ams.dbAccess.configdb.MessageChainDAO;
import org.csstudio.ams.dbAccess.configdb.MessageChainTObject;
import org.csstudio.ams.dbAccess.configdb.UserDAO;
import org.csstudio.ams.dbAccess.configdb.UserTObject;
import org.csstudio.ams.gui.CommandButton;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Markus Moeller
 *
 */

public class AlarmChainManagerView extends ViewPart implements AmsConstants, SelectionListener, ISelectionChangedListener
{
    public static final String ID = AlarmChainManagerView.class.getName();
    
    /**  */
    private ArrayList<MessageChainTObject> chainList = new ArrayList<MessageChainTObject>();

    /**  */
    private MessageChainTObject currentChain = null;

    /**  */
    private Table tblChain = null;
    
    /**  */
    private TableViewer tblChainViewer = null; 
    
    /**  */
    private Composite mainComposite = null;
    
    /**  */
    private Group filterComposite = null;
    
    /**  */
    private CommandButton cmdRefresh = null;
    
    /**  */
    private CommandButton cmdDelete = null;
    
    private Context amsSenderContext    = null;
    private ConnectionFactory amsSenderFactory    = null;
    private Connection amsSenderConnection = null;
    private Session amsSenderSession    = null;
    
    private MessageProducer amsPublisherReply   = null;
    
    /**
     * 
     */
    public AlarmChainManagerView() {
        // Nothing to do here?
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent)
    {
        ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        
        mainComposite = new Composite(sc, SWT.BORDER);
        mainComposite.setLayout(new GridLayout(1,false));

        // Set the child as the scrolled content of the ScrolledComposite
        sc.setContent(mainComposite);

        // Set the minimum size
        sc.setMinSize(600, 300);

        // Expand both horizontally and vertically
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        
        createTable();
        createTableViewer();
    }
    
    private void createTable() 
    {
        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

        filterComposite = new Group(mainComposite, SWT.NONE);
        filterComposite.setLayout(new GridLayout(7,false));
        filterComposite.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.FILL, SWT.FILL, true, false));
        filterComposite.setText(Messages.getString("AMSMessageManagerView_filterComposite"));

        cmdRefresh = new CommandButton(filterComposite, CommandButton.REFRESH);
        cmdRefresh.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING, SWT.CENTER, false, false));
        cmdRefresh.addSelectionListener(this);
        
        cmdDelete = new CommandButton(filterComposite, CommandButton.DELETE);
        cmdDelete.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING, SWT.CENTER, false, false));
        cmdDelete.addSelectionListener(this);
        cmdDelete.setEnabled(false);
        
        tblChain = new Table(mainComposite, style);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        tblChain.setLayoutData(gridData);

        tblChain.setLinesVisible(true);
        tblChain.setHeaderVisible(true);
        
        int idx = 0;
        TableColumn column = new TableColumn(tblChain, SWT.LEFT, idx++);
        column.setText(Messages.getString("ManagerTable_tbl_Colum" + idx));
        column.setWidth(80);
        // column.addSelectionListener(new SortSelectionAdapter(TableSorter.HISTORYID));

        column = new TableColumn(tblChain, SWT.LEFT, idx++);
        column.setText(Messages.getString("ManagerTable_tbl_Colum" + idx));
        column.setWidth(150);
        // column.addSelectionListener(new SortSelectionAdapter(TableSorter.TIMENEW));
        
        column = new TableColumn(tblChain, SWT.LEFT, idx++);
        column.setText(Messages.getString("ManagerTable_tbl_Colum" + idx));
        column.setWidth(150);
        // column.addSelectionListener(new SortSelectionAdapter(TableSorter.TYPE));
        
        column = new TableColumn(tblChain, SWT.LEFT, idx++);
        column.setText(Messages.getString("ManagerTable_tbl_Colum" + idx));
        column.setWidth(140);
        // column.addSelectionListener(new SortSelectionAdapter(TableSorter.MSGHOST));
        
        column = new TableColumn(tblChain, SWT.LEFT, idx++);
        column.setText(Messages.getString("ManagerTable_tbl_Colum" + idx));
        column.setWidth(280);
        // column.addSelectionListener(new SortSelectionAdapter(TableSorter.MSGNAME));
        
        column = new TableColumn(tblChain, SWT.LEFT, idx++);
        column.setText(Messages.getString("ManagerTable_tbl_Colum" + idx));
        column.setWidth(280);
        // column.addSelectionListener(new SortSelectionAdapter(TableSorter.MSGNAME));
    }
    
    public void createTableViewer()
    {       
        tblChainViewer = new TableViewer(tblChain);
        // tblChainViewer.setUseHashlookup(true);
        
        tblChainViewer.setContentProvider(new ArrayContentProvider());
        tblChainViewer.setLabelProvider(new ViewLabelProvider());
        tblChainViewer.setSorter(new NameSorter());
        
        tblChainViewer.addSelectionChangedListener(this);
        
        // The input for the table viewer
        tblChainViewer.setInput(chainList);
    }

    /**
     * 
     */
    @Override
    public void setFocus() {
     // Nothing to do here?
    }
    
    private String prepareMessageNumber(int iChainID, int iChainPos) throws Exception
    {
        int iRlen = ("" + iChainPos).length();
        if (iRlen > MSG_POS_LENGTH_FOR_MSGPROP)
        {
            throw new AMSException("MessageChain ReceiverPos=" + iChainPos
            + " has more chars than > MSG_POS_LENGTH_FOR_MSGPROP="
            + MSG_POS_LENGTH_FOR_MSGPROP);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(iChainID);
        while (iRlen++ < MSG_POS_LENGTH_FOR_MSGPROP)
            // until len == MSG_POS_LENGTH_FOR_MSGPROP
            sb.append('0'); // fill with leading zeros (1 -> 001)
        sb.append(iChainPos);

        return sb.toString();
    }

    /**
     * 
     */
    @Override
    public void widgetSelected(SelectionEvent event)
    {
        if(event.getSource() instanceof CommandButton)
        {
            CommandButton cb = (CommandButton)event.getSource();
            
            switch(cb.getType())
            {
                case CommandButton.REFRESH:

                    List<MessageChainTObject> data = getTableData();
                    
                    if(data != null)
                    {
                        if(data.isEmpty())
                        {
                            cmdDelete.setEnabled(false);
                        }
    
                        chainList.clear();
                        chainList.addAll(data);
                        tblChainViewer.refresh();
                    }
                    
                    break;
                
                case CommandButton.DELETE:
                    
                    UserTObject user = null;
                    String messageChainId = null;
                    String stateCode = null;
                    String phone = null;
                    
                    if(!MessageDialog.openConfirm(AlarmChainManagerPlugin.getDefault().getShell(),
                            Messages.AMSMessageManagerView_Name,
                            Messages.AMSMessageManagerView_Confirm))
                    {
                        break;
                    }
                    
                    if(currentChain != null)
                    {
                        phone = currentChain.getReceiverAdress();
                        
                        try
                        {
                            user = UserDAO.select(AlarmChainManagerPlugin.getConnection(), phone);
                            
                            if(user != null)
                            {
                                stateCode = user.getConfirmCode();
                                
                                messageChainId = prepareMessageNumber(
                                        currentChain.getMessageChainID(),
                                        currentChain.getReceiverPos());
                                
                                if(sendReplyMessage(messageChainId, stateCode, phone))
                                {
                                    MessageDialog.openInformation(AlarmChainManagerPlugin.getDefault().getShell(),
                                            Messages.AMSMessageManagerView_Name,
                                            Messages.AMSMessageManagerView_MsgChainStopped);
                                    
                                    data = getTableData();
                                    
                                    if(data != null)
                                    {
                                        if(data.isEmpty())
                                        {
                                            cmdDelete.setEnabled(false);
                                        }
                    
                                        chainList.clear();
                                        chainList.addAll(data);
                                        tblChainViewer.refresh();
                                    }
                                }
                                else
                                {
                                    MessageDialog.openError(AlarmChainManagerPlugin.getDefault().getShell(),
                                            Messages.AMSMessageManagerView_Name,
                                            Messages.AMSMessageManagerView_MsgChainNotStopped);
                                }                                
                            }
                        }
                        catch(SQLException sqle)
                        {
                            user = null;
                        }
                        catch(Exception ex)
                        {
                            MessageDialog.openError(AlarmChainManagerPlugin.getDefault().getShell(),
                                    Messages.AMSMessageManagerView_Name,
                                    Messages.AMSMessageManagerView_ChainIdNotPrepared + ex.getMessage());                            
                        }
                    }                  

                    break;
            }
        }
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event)
    {
        if(event.getSource() instanceof TableViewer)
        {
            StructuredSelection sel = (StructuredSelection)event.getSelection();
                        
            if(!sel.isEmpty())
            {
                currentChain = (MessageChainTObject)sel.getFirstElement();
            
                if(currentChain != null)
                {
                    cmdDelete.setEnabled(true);
                }
            }
            else
            {
                cmdDelete.setEnabled(false);
            }
        }   
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // Nothing to do here?
    }

    private List<MessageChainTObject> getTableData()
    {
        List<MessageChainTObject> data = null;
        
        try
        {
            data = MessageChainDAO.selectKeyListByState(AlarmChainManagerPlugin.getConnection(), (short)0);            
        }
        catch(SQLException sqle)
        {
            Log.log(Log.ERROR, "Cannot get the message chain list");
        }

        return data;
    }
    
    private boolean sendReplyMessage(String messageChainIdAndPos, String stateCode, String receiverAddress)
    {
        boolean result = false;
        MapMessage msg = null;
        
        if(!initJms())
        {
            MessageDialog.openError(AlarmChainManagerPlugin.getDefault().getShell(),
                    Messages.AMSMessageManagerView_Name,
                    Messages.AMSMessageManagerView_JmsError);
        
            return false;
        }    

        try
        {
            msg = amsSenderSession.createMapMessage();
            
            msg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_SMS);
            msg.setString(MSGPROP_MESSAGECHAINID_AND_POS, messageChainIdAndPos);
            msg.setString(MSGPROP_CONFIRMCODE, stateCode);
            msg.setString(MSGPROP_REPLY_ADRESS, receiverAddress);
            
            amsPublisherReply.send(msg);
            
            result = true;
        }
        catch(JMSException jmse)
        {
            Log.log(Log.ERROR, "*** JMSException *** : " + jmse.getMessage());
        }
        
        closeJms();
        
        return result;
    }
    
    private boolean initJms()
    {
        IPreferenceStore storeAct = AlarmChainManagerPlugin.getDefault().getPreferenceStore();
        Hashtable<String, String> properties = null;
        
        try
        {
            properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, 
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS));
            properties.put(Context.PROVIDER_URL, 
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL));
            amsSenderContext = new InitialContext(properties);
            
            amsSenderFactory = (ConnectionFactory) amsSenderContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY));
            amsSenderConnection = amsSenderFactory.createConnection();
            
            amsSenderConnection.setClientID("MessageManagerSenderInternal");
                        
            amsSenderSession = amsSenderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // CHANGED BY: Markus Moeller, 25.05.2007
            /*
            amsPublisherReply = amsSession.createProducer((Topic)amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_REPLY)));
            */
            
            amsPublisherReply = amsSenderSession.createProducer(amsSenderSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY)));
            if (amsPublisherReply == null)
            {
                Log.log(this, Log.FATAL, "could not create amsPublisherReply");
                
                return false;
            }

            amsSenderConnection.start();
            
            return true;
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not init internal Jms", e);
        }
        
        return false;
    }
    
    public void closeJms()
    {
        Log.log(this, Log.INFO, "exiting internal jms communication");
        
        if (amsPublisherReply != null){try{amsPublisherReply.close();amsPublisherReply=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}    
        if (amsSenderSession != null){try{amsSenderSession.close();amsSenderSession=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSenderConnection != null){try{amsSenderConnection.stop();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSenderConnection != null){try{amsSenderConnection.close();amsSenderConnection=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSenderContext != null){try{amsSenderContext.close();amsSenderContext=null;}
        catch (NamingException e){Log.log(this, Log.WARN, e);}}

        Log.log(this, Log.INFO, "jms internal communication closed");
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        DateFormat df = DateFormat.getDateTimeInstance();
        
        @Override
        public String getColumnText(Object obj, int index)
        {
            MessageChainTObject item = (MessageChainTObject)obj;
            
            switch (index) 
            {       
                case 0:
                    return Integer.toString(item.getMessageChainID());
                
                case 1:
                    return Integer.toString(item.getReceiverPos());
                    
                case 2:
                    return df.format(item.getSendTime());
                
                case 3:
                    return df.format(item.getNextActTime());
                
                case 4:
                    return (item.getChainState() == 0) ? Messages.AMSMessageManagerView_TextWorking : Messages.AMSMessageManagerView_TextFailed;
                
                case 5:
                    return item.getReceiverAdress();

                default :
                    return "?";
            }
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    class NameSorter extends ViewerSorter {
        // Nothing to do here?
    }
}
