package org.csstudio.opibuilder.widgets.detailpanel;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.gef.commands.Command;

// A command object that changes the location of a row in the table
public class DetailPanelChangeRowIndexCommand extends Command {
    public class Item {
        public int oldIndex;
        public int newIndex;
        public DetailPanelModelRow.Mode oldMode;
        public DetailPanelModelRow.Mode newMode;
        public Item(int o, int n, DetailPanelModelRow.Mode om, DetailPanelModelRow.Mode nm) {
            oldIndex = o;
            newIndex = n;
            oldMode = om;
            newMode = nm;
        }
    }

    private DetailPanelEditpart editpart;
    private LinkedList<Item> items;

    public DetailPanelChangeRowIndexCommand(DetailPanelEditpart editpart, int oldIndex, int newIndex) {
        items = new LinkedList<Item>();
        this.editpart = editpart;
        setLabel("Change row location");
        this.editpart.determineRowMove(oldIndex, newIndex, this);
    }

    public void addItem(int o, int n, DetailPanelModelRow.Mode om, DetailPanelModelRow.Mode nm) {
        this.items.addLast(new Item(o, n, om, nm));
    }

    @Override
    public void execute() {
        for(Item item: items) {
            editpart.moveRow(item.oldIndex, item.newIndex, item.newMode);
        }
        editpart.setAllGroupCollapse(false);
        editpart.getFigure().setAllRowsVisibility();
        editpart.getFigure().layout();
    }

    @Override
    public void undo() {
        Iterator<Item> pos = items.descendingIterator();
        while(pos.hasNext()) {
            Item item = pos.next();
            if(item.oldIndex == item.newIndex) {
                editpart.moveRow(item.newIndex, item.oldIndex, item.oldMode);
            }
            else if(item.oldIndex < item.newIndex) {
                editpart.moveRow(item.newIndex-1, item.oldIndex, item.oldMode);
            } else {
                editpart.moveRow(item.newIndex, item.oldIndex+1, item.oldMode);
            }
        }
        editpart.setAllGroupCollapse(false);
        editpart.getFigure().setAllRowsVisibility();
        editpart.getFigure().layout();
    }
}
