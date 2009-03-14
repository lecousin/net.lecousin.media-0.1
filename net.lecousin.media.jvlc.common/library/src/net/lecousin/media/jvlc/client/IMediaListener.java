package net.lecousin.media.jvlc.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.lecousin.media.jvlc.server.IJVLCMedia;

public interface IMediaListener extends Remote {

	public void started(IJVLCMedia media) throws RemoteException;
	public void paused(IJVLCMedia media) throws RemoteException;
	public void ended(IJVLCMedia media) throws RemoteException;
	public void stopped(IJVLCMedia media) throws RemoteException;
	public void positionChanged(IJVLCMedia media) throws RemoteException;
	public void timeChanged(IJVLCMedia media, long time) throws RemoteException;
	
}
