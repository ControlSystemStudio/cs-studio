package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;

/**
 * If an AssociableCommandListener is registered at the appropriate CommandStack, 
 * TimestampedSetPropertyCommands are associated for Undo and Redo actions when they affected 
 * the same Property and were executed successively within the defined Association Time.
 * 
 * @see AssociableCommandListener
 * 
 * @author Christian Zoller
 */
public class TimestampedSetPropertyCommand extends SetPropertyCommand implements AssociableCommand {
    
    private boolean _skip = false;
    
    private final String _propertyName;
    private final int _associationTime;
    private final long _timestamp;
    
    public TimestampedSetPropertyCommand(AbstractWidgetModel widget,
                                         String propertyName,
                                         Object value,
                                         int associationTime) {
        super(widget, propertyName, value);
        _propertyName = propertyName;
        _associationTime = associationTime;
        _timestamp = System.currentTimeMillis();
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.sds.ui.internal.commands.AssociableCommand#execute()
     */
    @Override
    public void execute() {
        super.execute();
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.sds.ui.internal.commands.AssociableCommand#isAssociated(org.csstudio.sds.ui.internal.commands.TimestampedSetPropertyCommand)
     */
    @Override
    public boolean isAssociated(AssociableCommand command) {
        if (!(command instanceof TimestampedSetPropertyCommand)) {
            return false;
        }
        
        return affectsSameProperty((TimestampedSetPropertyCommand) command)
                && hasMatchingTimestamp((TimestampedSetPropertyCommand) command);
    }
    
    private boolean affectsSameProperty(TimestampedSetPropertyCommand command) {
        if (command._propertyName == null) {
            return false;
        }
        
        return command._propertyName.equals(this._propertyName);
    }
    
    private boolean hasMatchingTimestamp(TimestampedSetPropertyCommand command) {
        int associationTime = Math.min(command._associationTime, this._associationTime);
        long delay = Math.abs(command._timestamp - this._timestamp);
        
        return delay <= associationTime;
    }
    
    @Override
    public void skipNextStackAction() {
        _skip = true;
    }
    
    @Override
    public void undo() {
        if (!_skip) {
            super.undo();
        }
        _skip = false;
    }
    
    @Override
    public void redo() {
        if (!_skip) {
            super.redo();
        }
        _skip = false;
    }
}
