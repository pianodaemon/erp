package com.maxima.bbgum;

public interface EventController {
    boolean handlerIsFlowTerm(EventBlackBox v);
    void handlerOutComming(EventBlackBox v, Action action) throws SessionError;
    void handlerInComming(EventBlackBox v, Action action) throws SessionError;
    void handlerTimeOut(EventBlackBox v, Action action);
    ServerReply handlerGetReply(EventBlackBox v);
}
