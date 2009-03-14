package net.lecousin.media.jvlc.server;

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IJVLCMediaManager extends Remote {

	public IJVLCMedia newMedia(URI uri) throws RemoteException;
	public void freeMedia(IJVLCMedia media) throws RemoteException;

	public void start(IJVLCMedia media) throws RemoteException;
	public void pause(IJVLCMedia media) throws RemoteException;
	public void stop(IJVLCMedia media) throws RemoteException;
	
	public double getPosition(IJVLCMedia media) throws RemoteException;
	public void setPosition(IJVLCMedia media, double pos) throws RemoteException;
	public long getTime(IJVLCMedia media) throws RemoteException;
	public void setTime(IJVLCMedia media, long time) throws RemoteException;

	public void setVideoOutput(long id) throws RemoteException;
	public void outputClosed() throws RemoteException;
	
	public double getVolume() throws RemoteException;
	public void setVolume(double volume) throws RemoteException;
	public boolean getMute() throws RemoteException;
	public void setMute(boolean value) throws RemoteException;
	
	public void free() throws RemoteException;
	
	
	/*
	public Event<IJVLCMedia> started() throws RemoteException;
	public Event<IJVLCMedia> paused() throws RemoteException;
	public Event<IJVLCMedia> ended() throws RemoteException;
	public Event<IJVLCMedia> stopped() throws RemoteException;
	public Event<IJVLCMedia> positionChanged() throws RemoteException;
	public Event<Pair<IJVLCMedia,Long>> timeChanged() throws RemoteException;*/
	
}
