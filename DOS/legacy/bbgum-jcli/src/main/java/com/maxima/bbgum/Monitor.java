package com.maxima.bbgum;

import java.util.logging.Level;
import java.util.logging.Logger;

final class Monitor {

    static final int TRANSACTION_NUM_START_VALUE = 1;
    static final int TRANSACTION_NUM_LAST_VALUE = 253;
    static final int TRANSACTION_NUM_INCREMENT =  2;
    static final int MAX_NODES = 255;

    private int nextNum;
    private Session session;
    private Transaction[] pool;
    private Object poolMutex;
    private EventBlackBox blackBox;
    private BasicFactory<Byte, EventController> factory;

    public Monitor(Session session, BasicFactory<Byte, EventController> factory) {
        this.session = session;
        this.factory = factory;
        {
            // Initialization of elements for transactions pool
            this.nextNum = Monitor.TRANSACTION_NUM_START_VALUE;
            this.poolMutex = new Object();
            this.pool = new Transaction[Monitor.MAX_NODES];

            int iter = 0;
            for (; iter < Monitor.MAX_NODES; iter++) this.pool[iter] = null;
        }
        this.blackBox = new EventBlackBox(this);
    }

    private int requestNextNum() throws SessionError {

        // The current function must only be called
        // by pushBuffer function.

        int index = this.nextNum;

        if ((this.pool[index] != null) &&
            (index == Monitor.TRANSACTION_NUM_LAST_VALUE )) {

            // From the first shelf we shall start
            // the quest of an available one if
            // next one was ocuppied and the last one.

            index = Monitor.TRANSACTION_NUM_START_VALUE;
        }

        if (this.pool[index] == null) {

            // When the shelf is available we shall return it
            // before we shall set nextNum variable up for
            // later calls to current function.

            if (index == Monitor.TRANSACTION_NUM_LAST_VALUE) {
                this.nextNum = Monitor.TRANSACTION_NUM_START_VALUE;
            } else {
                this.nextNum = index + Monitor.TRANSACTION_NUM_INCREMENT;
            }
            return index;
        }

        {
            // If you've reached this code block my brother, so...
            // you migth be in trouble soon. By the way you seem
            // a lucky folk and perhaps you would find a free
            // shelf by performing sequential search with awful
            // linear time. Otherwise the matter is fucked :(

            int i = 0;

            do {
                index += Monitor.TRANSACTION_NUM_INCREMENT;
                i++;
            } while ((this.pool[index] != null) && (i < Monitor.MAX_NODES));

            if (i == (Monitor.MAX_NODES - 1)) {
                String msg = "Poll of transactions to its maximum capacity";
                throw new SessionError(msg);
            }
            this.nextNum = index + Monitor.TRANSACTION_NUM_INCREMENT;
            return index;
        }
    }

    private boolean isServerTransaction(final int num) {
        return ((num % 2) == 0);
    }

    public void receive(Action a) throws SessionError {
        // Receives an action from upper layer

        if (!this.factory.isSupported(a.getArchetype()) &&
                !this.factory.isSupported((byte) (a.getArchetype() - 1))) {
            String msg = "The server side sent an invalid Action which is not registered yet!. " +
                    "It will be ignore";
            throw new SessionError(msg);
        }

        Transaction t = null;

        synchronized (poolMutex) {
            t = this.pool[a.getTransNum() & 0xff];
        }

        if (t == null) {
            if (this.isServerTransaction(a.getTransNum())) {
                try {
                    t = new Transaction(this.factory.incept(a.getArchetype()), false, true);
                } catch (Exception ex) {
                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                    throw new SessionError("Transaction could not be created");
                }
                synchronized (poolMutex) {
                    this.pool[a.getTransNum() & 0xff] = t;
                }
                this.blackBox.inComming(t.getController(), a);
            } else {
                String msg = "The transNum (" + a.getTransNum() +
                    ") of the Action is not a server transaction number. It will be ignore";
                throw new SessionError(msg);
            }
        } else this.blackBox.inComming(t.getController(), a);

        if (this.blackBox.isFlowTerm(t.getController())) {
            if (t.isBlockingMode()) t.wakeUp();
            else {

                //Destroy transaction
                synchronized (poolMutex) {
                    this.pool[a.getTransNum() & 0xff] = null;
                }
            }
        }
    }

    public void send(Action action) throws SessionError {
        // Sends action to upper layer
        this.session.deliver(action);
    }

    public ServerReply pushBuffer(final byte archetype, final byte[] buffer, final boolean block) throws SessionError {
        ServerReply reply = null;

        Action a = new Action();
        a.setArchetype(archetype);
        a.setBuffer(buffer);
        Transaction t = null;
        try {
            t = new Transaction(this.factory.incept(archetype), block, false);
        } catch (Exception ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            String msg = "Transaction could not be created";
            throw new SessionError(msg);
        }

        synchronized (poolMutex) {
            a.setTransNum((byte) this.requestNextNum());
            this.pool[a.getTransNum() & 0xff] = t;
        }

        this.blackBox.outComming(t.getController(), a);

        if (t.isBlockingMode()) {
            try {
                t.sleep();
            } catch (InterruptedException ex) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                String msg = "Transaction could not await";
                throw new SessionError(msg);
            }

            reply = this.blackBox.getReply(t.getController());

            //Destroy transaction
            synchronized (poolMutex) {
                this.pool[a.getTransNum() & 0xff] = null;
            }
        }

        return reply;
    }
}
