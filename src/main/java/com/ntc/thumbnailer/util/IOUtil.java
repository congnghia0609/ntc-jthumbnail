package com.ntc.thumbnailer.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.ZipFile;

public class IOUtil {
	/**
	 * Close, ignoring IOExceptions
	 * @param stream	Stream to be closed. May be null (in this case, nothing is done).
	 * @see Apache I/O Utils
	 */
	public static void quietlyClose(Closeable stream)
	{
		try
		{
			if (stream != null)
				stream.close();
		}
		catch (IOException e)
		{
			// Ignore
		}
	}

	public static void quietlyClose(ZipFile zipFile) {
		try
		{
			if (zipFile != null)
				zipFile.close();
		}
		catch (IOException e)
		{
			// Ignore
		}
	}
	
	public static void deleteQuietlyForce(File file)
	{
		if (file != null)
		{
			if(!file.delete())
			{
				if (file.exists())
					file.deleteOnExit();
			}
		}
	}
	
	// More difficult than I thought. See http://www.java2s.com/Code/Java/File-Input-Output/Getrelativepath.htm and http://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls
	/**
	 * Simplistic version: return the substring after the base
	 */
	public static String getRelativeFilename(File base, File target) {
		return getRelativeFilename(base.getAbsolutePath(), target.getAbsolutePath());
	}

	public static String getRelativeFilename(String sBase, String sTarget) {
		if (sTarget.startsWith(sBase))
		{
			if (sBase.endsWith("/") || sBase.endsWith("\\") || sTarget.length() == sBase.length())
				return sTarget.substring(sBase.length());
			else
				return sTarget.substring(sBase.length() + 1);
		}
		else
			return sTarget; // Leave absolute
	}
    
    public static int nextFreePort() {
        int from = 10000;
        int to = 30000;
        int port = ThreadLocalRandom.current().nextInt(from, to);
        while (true) {
            if (isLocalPortFree(port)) {
                return port;
            } else {
                port = ThreadLocalRandom.current().nextInt(from, to);
            }
        }
    }
    
//    public static void main(String[] args) {
//        int from = 10000;
//        int to = 30000;
//        int port = ThreadLocalRandom.current().nextInt(from, to);
//        System.out.println("port: " + port);
//    }

    public static boolean isLocalPortFree(int port) {
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
//			socket.setReuseAddress(true);
			int port = socket.getLocalPort();
			try {
				socket.close();
			} catch (IOException e) {
				// Ignore IOException on close()
			}
			return port;
		} catch (IOException e) { 
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		throw new IllegalStateException("Could not find a free TCP/IP port to start on...");
	}
}
