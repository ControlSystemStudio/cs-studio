package org.csstudio.alarm.table;

import javax.jms.Message;

import org.eclipse.swt.widgets.Shell;

public class ViewForUILoadTest_IntegrationTestComponent extends ViewLog {
    @Override
    public void initializeJMSConnection(Shell ps, String primCtxFactory, String primURL,
            String secCtxFactory, String secURL, String topic) {

        new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    Message message = null;

                    // baue Nachrichten...
                    ViewForUILoadTest_IntegrationTestComponent.this.onMessage(message);
                }
            }
        }).start();
    }
}
