/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hunter.Misc;

import hunter.form.ExtractDomain;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 *
 * @author xblux
 */
public class CekPort extends Thread {

    String ip;
    ExtractDomain edm;

    public CekPort(String ip, ExtractDomain edm) {
        this.ip = ip;
        this.edm = edm;
    }

    public boolean serverListening(String host, int port) {
        Socket s = null;
        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create your socket
            Socket socket = new Socket();
            // Connect with 10 s timeout
            socket.connect(sockaddr, 2000);

         //  System.out.println("Connected " + host + " " + port);
            return true;
        } catch (IOException e) {
            // System.out.println("Error " + ip +" reason "+ e.getMessage());
            return false;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void run() {
        Utils utils = new Utils();

        if (serverListening(ip, 443)) {
            this.edm.appendResult(ip + " -> 443 ");
           // utils.CekWaf("https://" + ip);
        } else if (serverListening(ip, 80)) {
            this.edm.appendResult(ip + " -> 80 ");

            //utils.CekWaf("http://" + ip);
        } else if (serverListening(ip, 8080)) {

                this.edm.appendResult(ip + " -> 8080 ");
               // utils.CekWaf("http://" + ip + ":8080");
            }
        else {

              System.out.println("Not Active " + ip + " " );
        }

    }
}
