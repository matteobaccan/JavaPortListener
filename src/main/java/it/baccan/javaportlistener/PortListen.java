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
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Matteo Baccan
 */
public class PortListen extends Thread {

    private static final Logger LOG = LogManager.getLogger(PortListen.class);

    /**
     *
     * @param argv
     */
    public static void main(String[] argv) {

        LOG.info("+---------------------------------------------------------------------------+");
        LOG.info("| TCP/IP Port Listener                                         Version 1.02 |");
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
                    String reverseDNS = getHostName(socket.getInetAddress().getHostAddress());
                    LOG.info(" New connection from [{}:{}] HostName [{}] -> [{}:{}} - [{}]", socket.getInetAddress().getHostAddress(), socket.getPort(), reverseDNS, socket.getLocalAddress().getHostAddress(), socket.getLocalPort(), cPortDesc);
                }
            }
        } catch (BindException be) {
            LOG.error("Error binding port [{}]", nPortFrom);
        } catch (Throwable e) {
            LOG.error(e.getMessage());
            LOG.error("Error in listening [{}]", nPortFrom);
        }
    }

    /**
     * Do a reverse DNS lookup to find the host name associated with an IP
     * address. Gets results more often than
     * {@link java.net.InetAddress#getCanonicalHostName()}, but also tries the
     * Inet implementation if reverse DNS does not work.
     *
     * Based on code found at
     * http://www.codingforums.com/showpost.php?p=892349&postcount=5
     *
     * @param ip The IP address to look up
     * @return The host name, if one could be found, or the IP address
     */
    private static String getHostName(final String ip) {
        String retVal = null;
        final String[] bytes = ip.split("\\.");
        if (bytes.length == 4) {
            try {
                final Hashtable<String, String> env = new Hashtable<>();
                env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
                final DirContext ctx = new InitialDirContext(env);
                final String reverseDnsDomain = bytes[3] + "." + bytes[2] + "." + bytes[1] + "." + bytes[0] + ".in-addr.arpa";
                final Attributes attrs = ctx.getAttributes(reverseDnsDomain, new String[]{
                    "PTR",});
                for (final NamingEnumeration<? extends Attribute> ae = attrs.getAll(); ae.hasMoreElements();) {
                    final Attribute attr = ae.next();
                    final String attrId = attr.getID();
                    for (final Enumeration<?> vals = attr.getAll(); vals.hasMoreElements();) {
                        String value = vals.nextElement().toString();

                        if ("PTR".equals(attrId)) {
                            final int len = value.length();
                            if (value.charAt(len - 1) == '.') {
                                // Strip out trailing period
                                value = value.substring(0, len - 1);
                            }
                            retVal = value;
                        }
                    }
                }
                ctx.close();
            } catch (final NamingException e) {
                // No reverse DNS that we could find, try with InetAddress
                LOG.info(""); // NO-OP
            }
        }

        if (null == retVal) {
            try {
                retVal = InetAddress.getByName(ip).getCanonicalHostName();
            } catch (final UnknownHostException e1) {
                retVal = ip;
            }
        }

        return retVal;
    }
}
