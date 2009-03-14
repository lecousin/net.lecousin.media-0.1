package net.lecousin.media.jvlc.server;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.Registry;

import net.lecousin.framework.IDManager;
import net.lecousin.framework.collections.SelfMapUniqueLong;
import net.lecousin.media.jvlc.client.IMediaListener;

import org.videolan.jvlc.JVLC;

public class JVLCServer {

	public static void main(String[] args) {
		try {
//			for (Object key : System.getProperties().keySet())
//			System.out.println("Property " + key.toString() + "=" + System.getProperty((String)key));
//		for (String key : System.getenv().keySet())
//			System.out.println("Env " + key + "=" + System.getenv(key));
			
			String path = "C:\\PROGRA~1\\VideoLAN\\VLC";
			int port = Registry.REGISTRY_PORT;
			for (int i = 0; i < args.length - 1; ++i)
				if (args[i].equalsIgnoreCase("-vlc"))
					path = args[++i];
				else if (args[i].equalsIgnoreCase("-port"))
					port = Integer.parseInt(args[++i]);
			listener = (IMediaListener)Naming.lookup("//localhost:" + port + "/JVLCMediaListener");
			manager = new JVLCMediaManager(path);
			Naming.rebind("//localhost:" + port + "/JVLCMediaManager", manager);
			System.out.println("JVLCServer ready.");
			try { System.in.read(); }
			catch (IOException e) {
				
			}
			manager.free();
			jvlc.release();
			System.out.println("JVLCServer exited.");
			System.exit(0);
		}
		catch (Throwable t) {
			t.printStackTrace(System.err);
			System.exit(2);
			return;
		}
	}
	
	static IMediaListener listener;
	static JVLCMediaManager manager;
	static JVLC jvlc;
	static IDManager idManager = new IDManager();
	static SelfMapUniqueLong<MediaStore> medias = new SelfMapUniqueLong<MediaStore>();
	
}
