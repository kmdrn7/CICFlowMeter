package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.flow.FlowMonitor;
import cic.cs.unb.ca.guava.GuavaMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * @author ryuka
 */
public class AutomaticFlowmeter {

    public static final Logger logger = LoggerFactory.getLogger(AutomaticFlowmeter.class);
    private static final long serialVersionUID = 7419600803861028585L;
    private FlowMonitor monitor;

    public AutomaticFlowmeter(String netint, String kafka_host, String kafka_port, String topic) {
        logger.info("Starting Proccess....");
        monitor = new FlowMonitor(netint, kafka_host, kafka_port, topic);

        GuavaMgr.getInstance().getEventBus().register(this);
        startListening();
    }

    public void startListening() {
        Timer t = new Timer(0, null);
        t.addActionListener(e -> monitor.startTrafficFlow());
        t.setRepeats(true);
        t.setDelay(500);
        t.start();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        GuavaMgr.getInstance().getEventBus().unregister(this);
    }
}
