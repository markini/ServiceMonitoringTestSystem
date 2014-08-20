package marki.at.servicemonitoring;

import android.test.AndroidTestCase;

/**
 * Created by marki on 02.01.14.
 */
public class TestNonStickyMonitors extends AndroidTestCase {

    public static int observeThisCounter;
    public static int handleEventCounter;

    @Override
    protected void setUp() throws Exception {
        observeThisCounter = 0;
        handleEventCounter = 0;
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNonStickyMonitor1() throws InterruptedException {
        MonitorExecutorService1 monitor = new MonitorExecutorService1();
        monitor.executeMonitoring(getContext(), false, 0.1f); //0.1 minutes = 6 seconds
        //in 1 second starts the monitor
        assertEquals("Not expected counter value - monitor should not have been run already", 0, observeThisCounter);
        assertEquals("Not expected counter value for event - this monitor is not calling the handleEvent", 0, handleEventCounter);
        Thread.sleep(1200);
        assertEquals("Not expected counter value - should have run once", 1, observeThisCounter);
        assertEquals("Not expected counter value for event - this monitor is not calling the handleEvent", 0, handleEventCounter);
        Thread.sleep(6000);
        assertEquals("Not expected counter value - should have run twice", 2, observeThisCounter);
        assertEquals("Not expected counter value for event - this monitor is not calling the handleEvent", 0, handleEventCounter);
        Thread.sleep(6000);
        assertEquals("Not expected counter value - should have run three times", 3, observeThisCounter);
        assertEquals("Not expected counter value for event - this monitor is not calling the handleEvent", 0, handleEventCounter);
        monitor.stopMonitoring(getContext());
        Thread.sleep(6000);
        assertEquals("Not expected counter value - should have three times", 3, observeThisCounter);
        assertEquals("Not expected counter value for event - this monitor is not calling the handleEvent", 0, handleEventCounter);
    }

    public void testNonStickyMonitor2() throws InterruptedException {
        MonitorExecutorService2 monitor = new MonitorExecutorService2();
        monitor.executeMonitoring(getContext(), false, 0.1f);
        //in 10 seconds starts the monitor
        assertEquals("Not expected counter value - monitor should not have been run already",0,observeThisCounter);
        assertEquals("Not expected counter value for event - monitor should not have been run already",0,handleEventCounter);
        Thread.sleep(1200);
        assertEquals("Not expected counter value - should have run once",1,observeThisCounter);
        assertEquals("Not expected counter value for event - should have run once",1,handleEventCounter);
        Thread.sleep(6000);
        assertEquals("Not expected counter value - should have run twice",2,observeThisCounter);
        assertEquals("Not expected counter value for event - should have run twice",2,handleEventCounter);
        monitor.stopMonitoring(getContext());
        Thread.sleep(6000);
        assertEquals("Not expected counter value - should have run twice",2,observeThisCounter);
        assertEquals("Not expected counter value for event - should have run twice",2,handleEventCounter);
    }
}
