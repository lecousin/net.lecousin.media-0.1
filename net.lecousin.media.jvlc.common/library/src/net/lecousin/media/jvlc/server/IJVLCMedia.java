package net.lecousin.media.jvlc.server;

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IJVLCMedia extends Remote {

	public long getID() throws RemoteException;
	
	public URI getURI() throws RemoteException;
	/** Return the duration in milliseconds */
	public long getDuration() throws RemoteException;
	
}
