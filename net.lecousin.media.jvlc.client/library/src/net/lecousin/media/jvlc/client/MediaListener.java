package net.lecousin.media.jvlc.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.lecousin.framework.Pair;
import net.lecousin.media.jvlc.server.IJVLCMedia;
import net.lecousin.media.jvlc.client.IMediaListener;

public class MediaListener  extends UnicastRemoteObject implements IMediaListener {

	private static final long serialVersionUID = 6810342778903736301L;

	public MediaListener() throws RemoteException {
		
	}
	
	public void started(IJVLCMedia media) throws RemoteException {
		JVLCClient.started(media);
	}
	public void paused(IJVLCMedia media) throws RemoteException {
		JVLCClient.paused(media);
	}
	public void ended(IJVLCMedia media) throws RemoteException {
		JVLCClient.ended(media);
	}
	public void stopped(IJVLCMedia media) throws RemoteException {
		JVLCClient.stopped(media);
	}
	public void positionChanged(IJVLCMedia media) throws RemoteException {
		JVLCClient.positionChanged(media);
	}
	public void timeChanged(IJVLCMedia media, long time) throws RemoteException {
		JVLCClient.timeChanged(new Pair<IJVLCMedia,Long>(media, time));
	}
}
