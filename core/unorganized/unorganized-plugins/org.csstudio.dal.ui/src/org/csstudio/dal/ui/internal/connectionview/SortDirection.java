/**
 *
 */
package org.csstudio.dal.ui.internal.connectionview;

import java.util.Comparator;

import org.csstudio.platform.simpledal.IConnector;

enum SortDirection {
    BY_CONTROL_SYTEM(new Comparator<IConnector>() {
        public int compare(IConnector s1, IConnector s2) {
            String t1 = s1.getProcessVariableAddress().getControlSystem() != null ? s1
                    .getProcessVariableAddress().getControlSystem()
                    .toString()
                    : "";
            String t2 = s2.getProcessVariableAddress().getControlSystem() != null ? s2
                    .getProcessVariableAddress().getControlSystem()
                    .toString()
                    : "";
            return t1.compareTo(t2);
        }
    }),

    BY_NAME((new Comparator<IConnector>() {
        public int compare(IConnector s1, IConnector s2) {
            String t1 = s1.getProcessVariableAddress().getProperty() != null ? s1
                    .getProcessVariableAddress().getProperty()
                    : "";
            String t2 = s2.getProcessVariableAddress().getProperty() != null ? s2
                    .getProcessVariableAddress().getProperty()
                    : "";
            return t1.compareTo(t2);
        }
    })),

    BY_CONNECTION_STATE((new Comparator<IConnector>() {
        public int compare(IConnector s1, IConnector s2) {
            String t1 = s1.getLatestConnectionState() != null ? s1
                    .getLatestConnectionState().toString() : "";
            String t2 = s2.getLatestConnectionState() != null ? s2
                    .getLatestConnectionState().toString() : "";
            return t1.compareTo(t2);
        }
    })),

    BY_TYPE((new Comparator<IConnector>() {
        public int compare(IConnector s1, IConnector s2) {
            String t1 = s1.getValueType() != null ? s1.getValueType()
                    .toString() : "";
            String t2 = s2.getValueType() != null ? s2.getValueType()
                    .toString() : "";
            return t1.compareTo(t2);
        }
    })),

    BY_VALUE((new Comparator<IConnector>() {
        public int compare(IConnector s1, IConnector s2) {
            String t1 = s1.getLatestValue() != null ? s1.getLatestValue()
                    .toString() : "";
            String t2 = s2.getLatestValue() != null ? s2.getLatestValue()
                    .toString() : "";
            return t1.compareTo(t2);
        }
    })),

    BY_NR_OF_LISTENERS((new Comparator<IConnector>() {
        public int compare(IConnector s1, IConnector s2) {
            int t1 = s1.getListenerCount();
            int t2 = s2.getListenerCount();
            return t1 - t2;
        }
    })),

    BY_ERROR((new Comparator<IConnector>() {
        public int compare(IConnector s1, IConnector s2) {
            String t1 = s1.getLatestError() != null ? s1.getLatestError()
                    : "";
            String t2 = s2.getLatestError() != null ? s2.getLatestError()
                    : "";
            return t1.compareTo(t2);
        }
    }));

    private Comparator<IConnector> _comparator;

    private SortDirection(Comparator<IConnector> comparator) {
        assert comparator != null;
        _comparator = comparator;
    }

    public Comparator<IConnector> getComparator() {
        return _comparator;
    }
}