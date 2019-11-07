/*
 * Java Port Listener
 *
 * By Matteo Baccan
 * www - http://www.baccan.it
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA (or visit
 * their web site at http://www.gnu.org/).
 *
 */
package it.baccan.javaportlistener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matteo Baccan
 */
public class portListen extends Thread {

    /**
     *
     * @param argv
     */
    static public void main(String argv[]) {
        String cPortFrom;
        String cPortDesc;

        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("| TCP/IP Port Listener                                         Version 1.00 |");
        System.out.println("| by Matteo Baccan                                    https://www.baccan.it |");
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("\nOpening portListen.ini ...\n");

        try (RandomAccessFile RAF = new RandomAccessFile("portListen.ini", "r")) {

            String cIPFrom = "127.0.0.1";
            if (argv.length > 0) {
                cIPFrom = argv[0];
            }

            String cLine = "";
            cLine = RAF.readLine();
            while (cLine != null) {

                StringTokenizer token = new StringTokenizer(cLine, ",");

                cPortFrom = "";
                cPortDesc = "";

                if (token.hasMoreTokens()) {
                    cPortFrom = token.nextToken();
                }
                if (token.hasMoreTokens()) {
                    cPortDesc = token.nextToken();
                }

                if (cPortFrom.length() > 0) {
                    portListen server = new portListen(cIPFrom, cPortFrom, cPortDesc);
                    server.start();
                }
                cLine = RAF.readLine();
            }

            System.out.println("\nAll system ready");

        } catch (FileNotFoundException ex) {
            System.out.println("\nError during opening of portListen.ini " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("\nError during reading of portListen.ini " + ex.getMessage());
        }
    }

    private final int nPortFrom;
    private final String cInServer;
    private final String cPortDesc;

    /**
     *
     * @param cIPFrom
     * @param inPort
     * @param cPDesc
     */
    public portListen(String cIPFrom, String inPort, String cPDesc) {
        cInServer = cIPFrom;
        nPortFrom = Integer.parseInt(inPort);
        cPortDesc = cPDesc;
        System.out.println("   listen [" + cInServer + ":" + nPortFrom + ":" + cPortDesc + "] ");
    }

    @Override
    public void run() {
        try (ServerSocket sock = new ServerSocket(nPortFrom, 5, InetAddress.getByName(cInServer))) {                                  // port, maxrequest, address
            while (true) {
                try (Socket socket = sock.accept()) {
                    //System.out.println( " User at [" +socket.getLocalPort() +":" +cPortDesc +"] retreive information" );
                    System.out.println(" New user from [" + socket.getInetAddress() + ":" + socket.getPort() + "] to [" + socket.getLocalAddress() + ":" + socket.getLocalPort() + ":" + cPortDesc + "]");
                }
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.out.println("Error in listen [" + nPortFrom + "] ");
        }
    }
}
