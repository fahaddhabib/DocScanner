package com.psychoutilities.camscan.webserver;

import static com.loopj.android.http.AsyncHttpClient.log;
import static com.psychoutilities.camscan.utils.AppSettings.getPortNumber;
import static com.psychoutilities.camscan.utils.AppSettings.setClientIp;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.psychoutilities.camscan.utils.AppSettings;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer extends Thread {
    private Context context = null;
    private static final String SERVER_NAME = "TransfileWebServer";
    private NotificationManager notifyManager = null;
    private int serverPort = 0;
    private ServerSocket httpServerSocket;


    public WebServer(Context context, NotificationManager notifyManager) {
        super(SERVER_NAME);
        this.setContext(context);
        this.setNotifyManager(notifyManager);
        serverPort = getPortNumber(context);
    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        Socket socket = null;
        try {
            httpServerSocket = new ServerSocket();
            httpServerSocket.setReuseAddress(true);
            httpServerSocket.bind(new InetSocketAddress(serverPort));

            AppSettings.setServiceStarted(getContext(),true);
            while (true) {
                socket = httpServerSocket.accept();
                log.v("socketIp1", "run: "+socket.getLocalAddress()+":"+socket.getPort());

                HttpResponse httpResponse = new HttpResponse(socket, getContext(), getNotifyManager());
                httpResponse.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Looper.loop();
    }

    public synchronized void startThread() {
        super.start();
    }

    public synchronized void stopThread() {
        try {
            if(httpServerSocket!=null)
            httpServerSocket.close();
            setClientIp(getContext(), false);
            AppSettings.setServiceStarted(getContext(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setNotifyManager(NotificationManager notifyManager) {
        this.notifyManager = notifyManager;
    }

    public NotificationManager getNotifyManager() {
        return notifyManager;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
