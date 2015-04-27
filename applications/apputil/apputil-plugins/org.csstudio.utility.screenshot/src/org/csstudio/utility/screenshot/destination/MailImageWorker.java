
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
 */

package org.csstudio.utility.screenshot.destination;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.csstudio.utility.screenshot.IImageWorker;
import org.csstudio.utility.screenshot.ScreenshotPlugin;
import org.csstudio.utility.screenshot.dialog.MailSenderDialog;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.csstudio.utility.screenshot.preference.ScreenshotPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */

public class MailImageWorker implements IImageWorker {
    
    private static final Logger LOG = LoggerFactory.getLogger(MailImageWorker.class);
    
	/** The string that is used to build the screenshot plugin menu */
	private final String MENU_ITEM_ENTRY = "eMail";

	/**
	 * {@inheritDoc}
	 */
    public String getMenuItemEntry() {
        return MENU_ITEM_ENTRY;
    }
    
    /**
     * {@inheritDoc}
     */
    public void processImage(Shell parentShell, Image image) {
        
        InternetAddress addressFrom = null;
        InternetAddress carbonCopy = null;
        InternetAddress[] addressTo = null;
        BufferedImage bufferedImage = null;
        String workspaceLocation = null;
        String imageFilename = "capture.jpg";
        
        if(image == null) {
            
            MessageDialog.openInformation(parentShell, ScreenshotPlugin.getDefault().getNameAndVersion(), ScreenshotMessages.getString("MailImageWorker.NO_IMAGE"));
            return;
        }
        
        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if(workspaceLocation.endsWith("/") == false) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch(IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }
        
        bufferedImage = convertToBufferedImage(image.getImageData());
        
        MailSenderDialog dialog = new MailSenderDialog(parentShell);
        
        int value = dialog.open();
        if((value == Window.OK) && (dialog.getMailEntry() != null)) {
            
            try {
                // ImageIO.write(bufferedImage, "jpg", new File(ScreenshotPlugin.getInstalledFilePath("/") + imageFilename));
                ImageIO.write(bufferedImage, "jpg", new File(workspaceLocation + imageFilename));
                Properties props = new Properties();
                
                IPreferencesService pref = Platform.getPreferencesService();
                props.put("mail.smtp.host", pref.getString(ScreenshotPlugin.PLUGIN_ID, ScreenshotPreferenceConstants.MAIL_SERVER, "", null));
                props.put("mail.smtp.port", "25");
                
                Session session = Session.getDefaultInstance(props);
                
                Message msg = new MimeMessage(session);
                
                MimeMultipart content = new MimeMultipart("mixed");
    
                MimeBodyPart text = new MimeBodyPart(); 
                MimeBodyPart bild = new MimeBodyPart(); 

                text.setText(dialog.getMailEntry().getMailText());
                
                ScreenshotPlugin.getDefault().setMailEntry(dialog.getMailEntry());
                
                text.setHeader("MIME-Version" , "1.0"); 
                text.setHeader("Content-Type" , text.getContentType()); 
    
                // DataSource source = new FileDataSource(ScreenshotPlugin.getInstalledFilePath("/") + imageFilename);
                DataSource source = new FileDataSource(workspaceLocation + imageFilename);
                bild.setDataHandler(new DataHandler(source));
                bild.setFileName("Screenshot.jpg");
                
                content.addBodyPart(text); 
                content.addBodyPart(bild); 
    
                msg.setContent( content ); 
                msg.setHeader( "MIME-Version" , "1.0" ); 
                msg.setHeader( "Content-Type" , content.getContentType() ); 
                msg.setHeader( "X-Mailer", "Java-Mailer V 1.60217733" ); 
                msg.setSentDate( new Date() );
    
                try {
                    
                    addressFrom = new InternetAddress(dialog.getMailEntry().getMailFromAddress());
                    msg.setFrom(addressFrom);
                    addressTo = InternetAddress.parse(dialog.getMailEntry().getMailToAddress());
                    msg.setRecipients(Message.RecipientType.TO, addressTo);
                    
                    if(dialog.getMailEntry().copyToSender()) {
                        
                        carbonCopy = new InternetAddress(dialog.getMailEntry().getMailFromAddress());
                        msg.addRecipient(Message.RecipientType.CC, carbonCopy);
                    }

                    msg.setSubject(dialog.getMailEntry().getMailSubject());    
                    Transport.send(msg);
                    
                    MessageDialog.openInformation(parentShell, ScreenshotPlugin.getDefault().getNameAndVersion(), ScreenshotMessages.getString("MailImageWorker.MAIL_SENT"));
                } catch(MessagingException me) {
                    MessageDialog.openError(parentShell, ScreenshotPlugin.getDefault().getNameAndVersion(), "Not possible to send the mail.\n\nReason:\n" + me.getMessage() + "\n\nIf port 25 is blocked by the virus scanner, you have to allow java(w).exe to use it.");
                }
            } catch(MessagingException mee) {
                MessageDialog.openError(parentShell, ScreenshotPlugin.getDefault().getNameAndVersion(), mee.getMessage());
            } catch(IOException ioe) {
                MessageDialog.openInformation(parentShell, "Error", ioe.getMessage());
            }
        }
        
        dialog = null;
    }
    
    /**
     * Converts a SWT image to a AWT buffered image.
     * 
     * @param data
     * @return
     */
    public BufferedImage convertToBufferedImage(ImageData data) {
        
        ColorModel colorModel = null;
        PaletteData palette = data.palette;
                
        if(palette.isDirect) {
            
            colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
            
            BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
            
            for (int y = 0; y < data.height; y++) {
                
                for (int x = 0; x < data.width; x++) {
                    int pixel = data.getPixel(x, y);
                    RGB rgb = palette.getRGB(pixel);
                    bufferedImage.setRGB(x, y,  rgb.red << 16 | rgb.green << 8 | rgb.blue);
                }
            }
            
            return bufferedImage;
        } else {
            
            RGB[] rgbs = palette.getRGBs();
            
            byte[] red   = new byte[rgbs.length];
            byte[] green = new byte[rgbs.length];
            byte[] blue  = new byte[rgbs.length];
            
            for (int i = 0; i < rgbs.length; i++) {
                
                RGB rgb = rgbs[i];
                
                red[i]   = (byte) rgb.red;
                green[i] = (byte) rgb.green;
                blue[i]  = (byte) rgb.blue;
            }
            
            if (data.transparentPixel != -1) {
                colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
            } else {
                colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
            }

            BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];

            for(int y = 0; y < data.height; y++) {
                
                for(int x = 0; x < data.width; x++) {
                    
                    int pixel = data.getPixel(x, y);
                    pixelArray[0] = pixel;
                    raster.setPixel(x, y, pixelArray);
                }
            }

            return bufferedImage;
        }
    }
    
    /*
    MimeMultipart content = new MimeMultipart( "alternative" ); 
    MimeBodyPart text = new MimeBodyPart(); 
    MimeBodyPart html = new MimeBodyPart(); 
    text.setText( "Text als normaler String" ); 
    text.setHeader( "MIME-Version" , "1.0" ); 
    text.setHeader( "Content-Type" , text.getContentType() ); 
    html.setContent( "<html>Text als <b>HTML</b></html>", "text/html"); 
    html.setHeader( "MIME-Version" , "1.0" ); 
    html.setHeader( "Content-Type" , html.getContentType() ); 
    content.addBodyPart( text ); 
    content.addBodyPart( html ); 
    Message msg = ... 
    msg.setContent( content ); 
    msg.setHeader( "MIME-Version" , "1.0" ); 
    msg.setHeader( "Content-Type" , content.getContentType() ); 
    msg.setHeader( "X-Mailer", "Java-Mailer V 1.60217733" ); 
    msg.setSentDate( new Date() );
 */
}
