package org.csstudio.config.ioconfig.editorparts;

import javax.annotation.Nonnull;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ModifyListener that set the save button enable to store the changes.
 * Works with {@link Text}, {@link Combo} and {@link Spinner}.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @since 03.06.2009
 *
 * @param <T> TODO
 */
final class NodeConfigModifyListener<T> implements ModifyListener {

    private static final Logger LOG = LoggerFactory.getLogger(NodeConfigModifyListener.class);
    private final AbstractNodeEditor<?> _abstractNodeEditor;

    /**
     *   Default Constructor.
     */
    public NodeConfigModifyListener(@Nonnull final AbstractNodeEditor<?> abstractNodeEditor) {
        _abstractNodeEditor = abstractNodeEditor;
    }

    @Override
    public void modifyText(@Nonnull final ModifyEvent e) {
        if (e.widget instanceof Text) {
            final Text text = (Text) e.widget;
            _abstractNodeEditor.setSavebuttonEnabled("ModifyListenerText:" + text.hashCode(), !text.getText()
                                 .equals(text.getData()));
        } else if (e.widget instanceof Combo) {
            final Combo combo = (Combo) e.widget;
            if (combo.getData() instanceof Integer) {
                final Integer value = (Integer) combo.getData();
                if (value == null) {
                    _abstractNodeEditor.setSavebuttonEnabled("ModifyListenerCombo" + combo.hashCode(), false);
                } else {
                    _abstractNodeEditor.setSavebuttonEnabled("ModifyListenerCombo" + combo.hashCode(),
                                         value != combo.getSelectionIndex());
                }
            }
        } else if (e.widget instanceof Spinner) {
            final Spinner spinner = (Spinner) e.widget;
            try {
                _abstractNodeEditor.setSavebuttonEnabled("ModifyListenerCombo" + spinner.hashCode(),
                                     (Short) spinner.getData() != spinner.getSelection());
            } catch (final ClassCastException cce) {
                LOG.error(spinner.toString(), cce);
            }
        }
    }
}
