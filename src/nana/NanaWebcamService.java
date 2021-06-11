package nana;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import processing.core.PImage;

public class NanaWebcamService extends Thread implements Runnable {

    int port = 8887;
    ServerSocket server;
    Socket client;
    NanaWebcamClient webcam;
    ExecutorService networkThread = Executors.newSingleThreadExecutor();

    public long targetRate, lastTime;
    Future<String> future;
    boolean allowSkip = false;

    public void handleImage(PImage image) {
        if (webcam != null) {
            if (future != null && allowSkip) {
                long lastTake = System.currentTimeMillis() - lastTime;
                try {
                    if (lastTake < targetRate) {
                        future.get(targetRate - lastTake, TimeUnit.MILLISECONDS);
                    } else {
                        System.out.println("frameRate: " + targetRate + " takeTime:" + lastTake);
                        System.out.println("video stream timeout (capture), frame skiped.");

                        System.out.println("frame lost by camera");
                        future.cancel(true);
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    System.out.println("targetRate: " + targetRate + " lastTake:" + lastTake + " allowTime: " + (targetRate - lastTake));
                    System.out.println("video stream timeout (socket), frame skiped.");
                    System.out.println("frame lost by network");
                    future.cancel(true);
                }
                future = null;
            }
            webcam.setImage(image);
            if (webcam == null) {
                return;
            }
            future = networkThread.submit(webcam);
            lastTime = System.currentTimeMillis();
        }
    }
    
    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("init failed");
            return;
        }
        System.out.println("server on port " + port);
        running = true;
        doUnlock();
        while (!this.isInterrupted()) {
            try {
                System.out.println("wait client");
                client = server.accept();
                lastTime = System.currentTimeMillis();
                webcam = new NanaWebcamClient(client);
                webcam.exit = false;
                while (!client.isClosed()) {
                    synchronized (this) {
                        wait(1000);
                    }
                    if (webcam.exit) {
                        webcam = null;
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("server error");
                if (server == null) {
                    break;
                } else {
                    continue;
                }
            }
        }
        System.out.println("server shutdown");
        if (!stopping) {
            stopService();
        }
    }

    public boolean running = false;
    private boolean stopping = false;

    public void startService() {
        if (running) {
            System.err.println("already start service");
            return;
        }
        this.start();
    }

    public void stopService() {
        if (!running) {
            System.err.println("already close service");
            return;
        }
        webcam = null;
        stopping = true;
        this.interrupt();
        try {
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.close();
            }
            System.out.println("server closed");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("service error");
        }
        client = null;
        server = null;
        stopping = false;
        running = false;
    }

    public String getServerDesc() {
        if (server == null) {
            doLock();
        }
        return "http://" + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort();
    }

    Object asyncLock = new Object();

    private void doLock() {
        synchronized (asyncLock) {
            try {
                asyncLock.wait(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doUnlock() {
        synchronized (asyncLock) {
            asyncLock.notifyAll();
        }
    }

}
