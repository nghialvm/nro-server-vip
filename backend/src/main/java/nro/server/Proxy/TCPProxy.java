package nro.server.Proxy;

import Utils.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ADMIN
 */

public class TCPProxy implements Runnable {
    private final String targetIp;
    private final int targetPort;
    private final int listenPort;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final Thread thread;

    public TCPProxy(String targetIp, int targetPort, int listenPort) {
        this.targetIp = targetIp;
        this.targetPort = targetPort;
        this.listenPort = listenPort;
        this.thread = new Thread(this);
    }

    public void start() {
        this.thread.start();
    }

    public void stop() {
        this.running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Logger.error("Lỗi khi đóng firewall server socket trên cổng " + listenPort + ": " + e.getMessage());
        }
        thread.interrupt();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(listenPort);
            Logger.log("FireWall On Connect: " + listenPort + " -> " + targetIp + ":" + targetPort + "\n");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ProxyTask(clientSocket)).start();
                } catch (IOException e) {
                    if (running) {
                        Logger.error("Lỗi chấp nhận kết nối trên cổng " + listenPort + ": " + e.getMessage());
                    } else {
                        Logger.log("FireWall on port " + listenPort + " stop all.\n");
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("Không thể khởi động firewall trên cổng " + listenPort + ". Cổng có thể đang được sử dụng.");
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    public int getListenPort() {
        return listenPort;
    }
    
    public String getTargetIp() {
        return targetIp;
    }

    public int getTargetPort() {
        return targetPort;
    }


    private class ProxyTask implements Runnable {
        private final Socket clientSocket;

        public ProxyTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (Socket targetSocket = new Socket(targetIp, targetPort)) {
                Thread clientToServer = new Thread(() -> forwardStream(clientSocket, targetSocket));
                Thread serverToClient = new Thread(() -> forwardStream(targetSocket, clientSocket));

                clientToServer.start();
                serverToClient.start();
                clientToServer.join();
                serverToClient.join();

            } catch (IOException | InterruptedException e) {
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                }
            }
        }

        private void forwardStream(Socket inputSocket, Socket outputSocket) {
            try {
                InputStream inputStream = inputSocket.getInputStream();
                OutputStream outputStream = outputSocket.getOutputStream();
                byte[] buffer = new byte[4096];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                    outputStream.flush();
                }
            } catch (IOException e) {
            } finally {
                 try {
                    inputSocket.close();
                    outputSocket.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}