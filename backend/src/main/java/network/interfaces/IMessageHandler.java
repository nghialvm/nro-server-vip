package network.interfaces;

import network.io.Message;

public interface IMessageHandler {

    void onMessage(final ISession p0, final Message p1) throws Exception;
}





