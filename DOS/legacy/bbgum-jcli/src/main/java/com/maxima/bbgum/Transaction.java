package com.maxima.bbgum;


class Transaction {

    private boolean blockingMode;
    private boolean modeServer;
    private EventController c;
    private Object wakeUpMutex = new Object();

    public Transaction(EventController c, boolean block, boolean mode) {
        this.c = c;
        this.blockingMode = block;
        this.modeServer = mode;
    }
    
    public void sleep() throws InterruptedException{
        synchronized (wakeUpMutex) {
            wakeUpMutex.wait();
        }
    }

    public void wakeUp(){
        synchronized (wakeUpMutex) {
            wakeUpMutex.notify();
        }        
    }

    public EventController getController() {
        return c;
    }

    public boolean isModeServer() {
        return modeServer;
    }

    public boolean isBlockingMode() {
        return blockingMode;
    }
}
