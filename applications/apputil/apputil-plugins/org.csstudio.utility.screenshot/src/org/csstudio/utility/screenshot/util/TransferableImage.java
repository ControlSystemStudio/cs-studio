
package org.csstudio.utility.screenshot.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * @author Herbert Max Straub
 *
 */

public class TransferableImage implements Transferable, ClipboardOwner
{
   private BufferedImage bufImg;

   public TransferableImage(ImageIcon ic)
   {
      this( ic.getImage() );
   }

   public TransferableImage(Image img)
   {
      int w = img.getWidth(null); // es muß keinen ImageObserver geben
      int h = img.getHeight(null);

      bufImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      // einfachster Fall : BufferedImage.TYPE_INT_RGB
      // mit drawImage das Image auf ein BufferedImage zeichnen
      Graphics g = bufImg.createGraphics();
      g.drawImage(img, 0, 0, null);
      g.dispose();
   }

   public TransferableImage(BufferedImage bImg)
   {
       bufImg = bImg;
   }

   //Returns an object which represents the data to be transferred.
   @Override
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
   {
      if( flavor.equals(DataFlavor.imageFlavor) )
         return bufImg;

      throw new UnsupportedFlavorException(flavor);
   }

   //Returns an array of DataFlavor objects indicating the flavors
   //the data can be provided in.
   @Override
   public DataFlavor[] getTransferDataFlavors()
   {
      return new DataFlavor[] {DataFlavor.imageFlavor} ;
   }

   //Returns whether or not the specified data flavor is supported
   //for this object.
   @Override
   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      return flavor.equals(DataFlavor.imageFlavor) ;
   }

   // Implementierung des Interfaces ClipboardOwner
   @Override
   public void lostOwnership(Clipboard clipboard, Transferable contents)
   {
   }
}

