package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.guava.GuavaMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;

public class App {
	public static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void init() {
		FlowMgr.getInstance().init();
		GuavaMgr.getInstance().init();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
            try {
                init();
				new AutomaticFlowmeter(
					"eno1",
				"192.168.100.29",
				"29092",
					"netflowmeter"
				);
            } catch (Exception e) {
				logger.debug(e.getMessage());
            }
        });
	}
}
