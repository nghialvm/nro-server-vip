package network;

import Utils.Logger;
import network.interfaces.IServerClose;
import java.net.Socket;
import java.io.IOException;
import java.net.InetSocketAddress;
import network.interfaces.ISession;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.util.logging.Level;
import network.interfaces.ISessionAcceptHandler;
import network.interfaces.INetwork;
import network.session.Session;
import network.session.SessionFactory;
import network.session.SessionManager;

public class Network implements INetwork, Runnable {

    private static Network instance;
    private int port;
    private ServerSocketChannel serverSocketChannel;
    private Class sessionClone;
    private boolean start;
    private IServerClose serverClose;
    private ISessionAcceptHandler acceptHandler;
    private Thread loopServer;
    private Selector selector;
    private Socket socket;

    public static Network gI() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    private Network() {
        this.port = -1;
        this.sessionClone = Session.class;
    }

    @Override
    public INetwork init() {
        try {
            this.selector = Selector.open();
        } catch (IOException ex) {
            Logger.errorln(ex.toString());
        }
        this.loopServer = new Thread(this, "Network");
        return this;
    }

    @Override
    public INetwork start(final int port) throws Exception {
        if (port < 0) {
            throw new Exception("Please initialize the server port!");
        }
        if (this.acceptHandler == null) {
            throw new Exception("AcceptHandler has not been initialized!");
        }
        if (!ISession.class.isAssignableFrom(this.sessionClone)) {
            throw new Exception("The type 'session clone' is invalid!");
        }
        try {
            this.port = port;
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
            this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            Logger.error("Error initializing server at port " + port + "\n");
            System.exit(0);
        }
        this.start = true;
        this.loopServer.start();
        Logger.success("Server đang chạy tại port " + this.port + "\n");
        return this;
    }

    @Override
    public INetwork close() {
        this.start = false;
        if (this.serverSocketChannel != null) {
            try {
                this.serverSocketChannel.close();
            } catch (IOException ex) {
            }
        }
        if (this.serverClose != null) {
            this.serverClose.serverClose();
        }
        if (this.socket != null) {
            try {
                String ip = socket.getInetAddress().getHostAddress();
                this.socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this;
    }

    @Override
    public INetwork dispose() {
        this.acceptHandler = null;
        this.loopServer = null;
        this.serverSocketChannel = null;
        this.socket = null;
        return this;
    }

    @Override
    public INetwork setAcceptHandler(final ISessionAcceptHandler handler) {
        this.acceptHandler = handler;
        return this;
    }

    @Override
    public void run() {
        while (start) {
            try {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        Socket socket2 = server.accept().socket();
                        String ip = socket2.getInetAddress().getHostAddress();
                        final ISession session = SessionFactory.gI().cloneSession(this.sessionClone, socket2);
                        this.acceptHandler.sessionInit(session);
                        SessionManager.gI().putSession(session);
                    }
                }
                selector.selectedKeys().clear();
            } catch (IOException ex) {
            } catch (Exception ex2) {
                Logger.errorln(ex2.toString());
            }
        }
    }

    @Override
    public INetwork setDoSomeThingWhenClose(final IServerClose serverClose) {
        this.serverClose = serverClose;
        return this;
    }

    @Override
    public INetwork setTypeSessionClone(final Class clazz) throws Exception {
        this.sessionClone = clazz;
        return this;
    }

    @Override
    public ISessionAcceptHandler getAcceptHandler() throws Exception {
        if (this.acceptHandler == null) {
            throw new Exception("AcceptHandler has not been initialized!");
        }
        return this.acceptHandler;
    }

    @Override
    public void stopConnect() {
        this.start = false;
    }
}