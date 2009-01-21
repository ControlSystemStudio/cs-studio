package org.csstudio.alarm.table.logTable;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.utility.MailSenderDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class SendMessagePropertiesAction extends Action {

	private JMSLogTableViewer table;

    InternetAddress     addressFrom     = null;
    InternetAddress     carbonCopy      = null;
    InternetAddress[]   addressTo       = null;

	
	public SendMessagePropertiesAction(final JMSLogTableViewer table) {
		this.table = table;
		setText("Send Properties");
		setToolTipText("Send properties of selected message");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setEnabled(false);
		// Conditionally enable this action
		table.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						if (!event.getSelection().isEmpty()) {
							boolean anything = true;
							JMSMessage entries[] = table.getSelectedEntries();
							if ((entries != null) && (entries.length == 1)) {
								anything = true;
							} else {
								anything = false;
							}
							setEnabled(anything);
						}
					}
				});
	}

	@Override
	public void run() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
//					String[] aas = { "array", "of", "String" };
//
//					MessageDialog ms = new MessageDialog(Display.getCurrent().getActiveShell(),"test", null,"hello", MessageDialog.NONE, aas, 1);

	        JMSMessage entries[] = table.getSelectedEntries();
			if (entries == null)
				return;
//			for (int i = 0; i < entries.length; i++)
//				table.getTableModel().removeJMSMessage(entries[i]);
//			table.refresh();

	        MailSenderDialog dialog = new MailSenderDialog(Display.getCurrent().getActiveShell());
	        
	        int value = dialog.open();
	                
	        if((value == Dialog.OK) && (dialog.getMailEntry() != null))
	        {
	            try
	            {
//	                ImageIO.write(bufferedImage, "jpg", new File(ScreenshotPlugin.getInstalledFilePath("/") + imageFilename));

	                Properties props = new Properties();
	                
	                props.put("mail.smtp.host", "smtp.desy.de");
//	                props.put("mail.smtp.host", ScreenshotPlugin.getDefault().getPluginPreferences().getString(ScreenshotPreferenceConstants.MAIL_SERVER));
	                props.put("mail.smtp.port", "25");
	                
	                Session session = Session.getDefaultInstance(props);
	                
	                Message msg = new MimeMessage(session);
	                
	                MimeMultipart content = new MimeMultipart("mixed");
	    
	                MimeBodyPart text = new MimeBodyPart(); 
	                MimeBodyPart bild = new MimeBodyPart(); 

	                text.setText(dialog.getMailEntry().getMailText());
	                
//	                ScreenshotPlugin.getDefault().setMailEntry(dialog.getMailEntry());
	                
	                text.setHeader("MIME-Version" , "1.0"); 
	                text.setHeader("Content-Type" , text.getContentType()); 
	    text.setText("hallo");
//	                DataSource source = new FileDataSource(ScreenshotPlugin.getInstalledFilePath("/") + imageFilename);
//	                bild.setDataHandler(new DataHandler(source));
//	                bild.setFileName("Screenshot.jpg");
	                
	                content.addBodyPart(text); 
	                content.addBodyPart(bild); 
	    
	                msg.setContent( content ); 
	                msg.setHeader( "MIME-Version" , "1.0" ); 
	                msg.setHeader( "Content-Type" , content.getContentType() ); 
	                msg.setHeader( "X-Mailer", "Java-Mailer V 1.60217733" ); 
	                msg.setSentDate( new Date() );
	    
	                try
	                {
	                    addressFrom = new InternetAddress(dialog.getMailEntry().getMailFromAddress());

	                    msg.setFrom(addressFrom);
	                    
	                    addressTo = InternetAddress.parse(dialog.getMailEntry().getMailToAddress());

	                    msg.setRecipients(Message.RecipientType.TO, addressTo);
	                    
	                    if(dialog.getMailEntry().copyToSender())
	                    {
	                        carbonCopy = new InternetAddress(dialog.getMailEntry().getMailFromAddress());
	                        
	                        msg.addRecipient(Message.RecipientType.CC, carbonCopy);
	                    }

	                    msg.setSubject(dialog.getMailEntry().getMailSubject());    
	                    
	                    Transport.send(msg);
	                    
	                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "", "Mail Sent");
	                }
	                catch(MessagingException me)
	                {
	                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "", "Not possible to send the mail.\n\nReason:\n" + me.getMessage() + "\n\nIf port 25 is blocked by the virus scanner, you have to allow java(w).exe to use it.");
	                }
	            }
	            catch(MessagingException mee)
	            {
	                MessageDialog.openError(Display.getCurrent().getActiveShell(), "", mee.getMessage());
	            }
		       
	}    
		
		dialog = null;
				} catch (Exception e) {
	                e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$

				}
			}
			});


}
}