package cic.cs.unb.ca.guava;

import com.google.common.eventbus.EventBus;

public class GuavaMgr {
    private EventBus mEventBus;

    private static GuavaMgr Instance = new GuavaMgr();
    public GuavaMgr() { }
    public static GuavaMgr getInstance() {
        return Instance;
    }
    public void init(){
        mEventBus = new EventBus("CICFlowMeter");
    }

    public EventBus getEventBus() {
        return mEventBus;
    }
}
