package br.com.clnrp.eqpi;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
public class TCPClient {
    private String data;
    private OnMessageReceived messageListener = null;
    private boolean run = false;
    private PrintWriter bufferOut;
    private BufferedReader bufferIn;

    public TCPClient(OnMessageReceived listener) {
        messageListener = listener;
        run = false;
    }

    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (bufferOut != null && !bufferOut.checkError()) {
                    bufferOut.print(message);
                    bufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

     public void disconnect() {
        if (bufferOut != null) {
            bufferOut.flush();
            bufferOut.close();
        }

        run = false;
        bufferIn = null;
        bufferOut = null;
        data = null;
    }

    public void connect(final String ip, final int port) {

        Runnable runnable = new Runnable() {
            public void run() {

                try {
                    Log.e("TCP Client", "Connecting...");
                    InetAddress address = InetAddress.getByName(ip);
                    Socket socket = new Socket(address, port);

                    bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    run = true;
                } catch (Exception e) {
                    Log.e("TCP", "Error", e);
                }

                while(run){
                    synchronized (this) {
                        try {
                            data = bufferIn.readLine();
                            if (data != null && messageListener != null) {
                                messageListener.messageReceived(data);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Boolean isConnected() {
        return run;
    }
}
