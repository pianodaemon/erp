package com.maxima.bbgum;

interface EventElem {
    void inComming(EventController v, Action action) throws SessionError;
    void outComming(EventController v, Action action) throws SessionError;
    void timeOut(EventController v, Action action);
    boolean isFlowTerm(EventController v);
    ServerReply getReply(EventController v);
}
