package org.csstudio.swt.widgets.natives;
/*
 * Table example snippet: insert a table column (at an index)
 *
 * For a list of all SWT example snippets see
 * http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-swt-home/dev.html#snippets
 */
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Snippet106 {

public static void main (String [] args) throws IOException {
     Display display = new Display ();
    InputStream stream = Snippet106.class.getResourceAsStream("checked.gif");
    final Image onImage = new Image(display, stream);
    stream.close();

  Shell shell = new Shell (display);
  shell.setLayout (new RowLayout (SWT.VERTICAL));
  final Table table = new Table (shell, SWT.BORDER | SWT.MULTI);
  table.setHeaderVisible (true);
  for (int i=0; i<4; i++) {
    TableColumn column = new TableColumn (table, SWT.NONE);
    column.setText ("Column " + i);
  }
  final TableColumn [] columns = table.getColumns ();
  for (int i=0; i<12; i++) {
    TableItem item = new TableItem (table, SWT.NONE);
    for (int j=0; j<columns.length; j++) {
      item.setText (j, "Item " + i);
      item.setImage(onImage);
    }
  }
  for (int i=0; i<columns.length; i++) columns [i].pack ();
  Button button = new Button (shell, SWT.PUSH);
  final int index = 0;
  button.setText ("Insert Column " + index + "a");
  button.addListener (SWT.Selection, new Listener () {
      @Override
    public void handleEvent (Event e) {
      TableColumn column = new TableColumn (table, SWT.NONE, index);
      column.setText ("Column " + index + "a");
      TableItem [] items = table.getItems ();
      for (int i=0; i<items.length; i++) {
        items [i].setText (index, "Item " + i + "a");
        items [i].setImage(onImage);
      }
      column.pack ();
    }
  });
  shell.pack ();
  shell.open ();
  while (!shell.isDisposed ()) {
    if (!display.readAndDispatch ()) display.sleep ();
  }
  display.dispose ();
}

}