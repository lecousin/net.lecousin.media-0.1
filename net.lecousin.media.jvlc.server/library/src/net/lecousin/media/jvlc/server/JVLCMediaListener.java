package net.lecousin.media.jvlc.server;

import java.rmi.RemoteException;

import net.lecousin.framework.log.Log;

import org.videolan.jvlc.MediaPlayer;
import org.videolan.jvlc.event.MediaPlayerListener;

public class JVLCMediaListener implements MediaPlayerListener {

	public JVLCMediaListener(JVLCMedia media) {
		this.media = media;
	}
	private JVLCMedia media;
	
	public void playing(MediaPlayer player) {
		debug("playing");
		try { JVLCServer.listener.started(media); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "The listener thrown an exception", e);
		}
	}
	public void paused(MediaPlayer player) {
		debug("paused");
		try { JVLCServer.listener.paused(media); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "The listener thrown an exception", e);
		}
	}
	public void endReached(MediaPlayer player) {
		debug("endReached");
		try { JVLCServer.listener.ended(media); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "The listener thrown an exception", e);
		}
	}
	public void stopped(MediaPlayer player) {
		debug("stopped");
		try { JVLCServer.listener.stopped(media); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "The listener thrown an exception", e);
		}
	}
	public void positionChanged(MediaPlayer player) {
		debug("positionChanged");
		try { JVLCServer.listener.positionChanged(media); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "The listener thrown an exception", e);
		}
	}
	public void timeChanged(MediaPlayer player, long time) {
		debug("timeChanged:" + time);
		try { JVLCServer.listener.timeChanged(media, time); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "The listener thrown an exception", e);
		}
	}
	public void errorOccurred(MediaPlayer player) {
		debug("errorOccured");
	}
	
	private void debug(String message) {
		System.out.println("DEBUG: " + System.currentTimeMillis() + " Listener: " + message);
	}
}
