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
import java.io.RandomAccessFile;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matteo Baccan
 */
public class PortListen extends Thread {
    
    private static final Logger LOG = LoggerFactory.getLogger(PortListen.class);

    /**
     *
     * @param argv
     */
    static public void main(String argv[]) {
        PropertyConfigurator.configure("log4j.properties");
        
        LOG.info("+---------------------------------------------------------------------------+");
        LOG.info("| TCP/IP Port Listener                                         Version 1.01 |");
        LOG.info("| by Matteo Baccan                                    https://www.baccan.it |");
        LOG.info("+---------------------------------------------------------------------------+");
        LOG.info("Opening portListen.ini ...");
        
        try (RandomAccessFile portListenerIni = new RandomAccessFile("portListen.ini", "rw")) {
            
            String ip = "0.0.0.0";
            if (argv.length > 0) {
                ip = argv[0];
            }
            
            String line = portListenerIni.readLine();
            
            while (line != null) {
                
                StringTokenizer token = new StringTokenizer(line, ",");
                
                String type = "";
                String portFrom = "";
                String portDesc = "";
                
                if (token.hasMoreTokens()) {
                    type = token.nextToken();
                    type = type.replace("\"", "");
                }
                if (token.hasMoreTokens()) {
                    portFrom = token.nextToken();
                }
                if (token.hasMoreTokens()) {
                    portDesc = token.nextToken();
                    portDesc = portDesc.replace("\"", "");
                }
                
                if ("TCP".equals(type) && portFrom.length() > 0) {
                    PortListen server = new PortListen(ip, Integer.parseInt(portFrom), portDesc);
                    server.start();
                } else {
                    LOG.info("Skip like [{}]", line);
                }
                line = portListenerIni.readLine();
            }
            
            LOG.info("All system ready");
            
        } catch (FileNotFoundException ex) {
            LOG.error("Error during opening of portListen.ini " + ex.getMessage());
        } catch (IOException ex) {
            LOG.error("Error during reading of portListen.ini " + ex.getMessage());
        }
    }
    
    private final int nPortFrom;
    private final String cInServer;
    private final String cPortDesc;

    /**
     *
     * @param ip
     * @param port
     * @param desc
     */
    public PortListen(String ip, int port, String desc) {
        cInServer = ip;
        nPortFrom = port;
        cPortDesc = desc;
        LOG.info(" listen [" + cInServer + ":" + nPortFrom + ":" + cPortDesc + "] ");
    }
    
    @Override
    public void run() {
        try (ServerSocket sock = new ServerSocket(nPortFrom, 5, InetAddress.getByName(cInServer))) {                                  // port, maxrequest, address
            while (true) {
                try (Socket socket = sock.accept()) {
                    LOG.info(" New connection from [{}:{} ({})({})] -> [{}:{}} - [{}]", socket.getInetAddress().getHostAddress(), socket.getPort(), socket.getInetAddress().getHostName(), socket.getInetAddress().getCanonicalHostName(), socket.getLocalAddress().getHostAddress(), socket.getLocalPort(), cPortDesc);
                }
            }
        } catch (BindException be) {
            LOG.error("Error binding port [{}]", nPortFrom);
        } catch (Throwable e) {
            LOG.error(e.getMessage());
            LOG.error("Error in listening [{}]", nPortFrom);
        }
    }
}
