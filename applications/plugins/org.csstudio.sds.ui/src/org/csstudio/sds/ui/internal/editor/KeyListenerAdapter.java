package org.csstudio.sds.ui.internal.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class KeyListenerAdapter implements KeyListener {

    private List<Integer> _pressedKeys;

    public KeyListenerAdapter() {
        _pressedKeys = new LinkedList<Integer>();
    }


    public void keyPressed(KeyEvent e) {
        Integer keyCode = new Integer(e.keyCode);
        if (!_pressedKeys.contains(keyCode)) {
            _pressedKeys.add(keyCode);
        }
    }

    public void keyReleased(KeyEvent e) {
        Integer keyCode = new Integer(e.keyCode);
        _pressedKeys.remove(keyCode);
    }

    public List<Integer> getPressedKeys() {
        return _pressedKeys;
    }

}
