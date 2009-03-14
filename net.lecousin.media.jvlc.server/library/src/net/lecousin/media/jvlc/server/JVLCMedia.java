package net.lecousin.media.jvlc.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.lecousin.media.jvlc.server.IJVLCMedia;

public class JVLCMedia extends UnicastRemoteObject implements IJVLCMedia {

	private static final long serialVersionUID = -1783906301239255618L;

	public JVLCMedia(long id) throws RemoteException {
		this.id = id;
	}
	
	long id;
	
	public long getID() throws RemoteException {
		return id;
	}
	
	public long getDuration() {
		return JVLCServer.medias.get(id).getLength();
	}

	public URI getURI() {
		try {
			return new URI(JVLCServer.medias.get(id).descriptor.getMrl());
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
