package de.desy.language.snl.diagram.ui;

import org.eclipse.draw2d.ConnectionAnchor;

import de.desy.language.snl.diagram.model.SNLModel;

public class WhenConnectionAnchors {

    /**
     * The source {@link ConnectionAnchor}.
     */
    private final SNLModel _source;
    /**
     * The target {@link ConnectionAnchor}.
     */
    private final SNLModel _target;

    private int _count;

    /**
     * Constructor.
     *
     * @param source
     *            The source {@link ConnectionAnchor}
     * @param target
     *            The Target {@link ConnectionAnchor}
     *
     * @requires source != null
     * @requires target != null
     */
    public WhenConnectionAnchors(final SNLModel source,
            final SNLModel target) {
        assert source != null : "source != null";
        assert target != null : "target != null";

        _source = source;
        _target = target;
        _count = 1;
    }

    public SNLModel getTarget() {
        return _target;
    }

    public SNLModel getSource() {
        return _source;
    }

    public void increseCount() {
        _count++;
    }

    public int getCount() {
        return _count;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_source == null) ? 0 : _source.hashCode());
        result = prime * result + ((_target == null) ? 0 : _target.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WhenConnectionAnchors other = (WhenConnectionAnchors) obj;
        if (_source == null) {
            if (other._source != null)
                return false;
        } else if (!_source.equals(other._source))
            return false;
        if (_target == null) {
            if (other._target != null)
                return false;
        } else if (!_target.equals(other._target))
            return false;
        return true;
    }

}
