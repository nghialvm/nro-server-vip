package network.session;

import java.net.Socket;
import network.interfaces.ISession;

public class SessionFactory {

    private static SessionFactory instance;

    public static SessionFactory gI() {
        if (instance == null) {
            instance = new SessionFactory();
        }
        return instance;
    }

    public ISession cloneSession(Class clazz, Socket socket) throws Exception {
        return (ISession) clazz.getConstructor(Socket.class).newInstance(socket);
    }
}





