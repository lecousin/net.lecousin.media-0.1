package net.lecousin.media.jvlc.server;

import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.videolan.jvlc.JVLC;
import org.videolan.jvlc.MediaDescriptor;

public class JVLCMediaManager extends UnicastRemoteObject implements IJVLCMediaManager {

	private static final long serialVersionUID = -5323320586127440749L;

	public JVLCMediaManager(String path) throws RemoteException {
		if (JVLCServer.jvlc == null) {
			String[] args = new String[] {
					"--plugin-path="+path+"\\plugins",
					"-vvv",
					//"--extraintf=logger",
					"--no-osd", // pour enlever le nom du fichier au debut
			};
			JVLCServer.jvlc = new JVLC(args);
		}
	}
	/*
	private Event<IJVLCMedia> started = new Event<IJVLCMedia>();
	private Event<IJVLCMedia> paused = new Event<IJVLCMedia>();
	private Event<IJVLCMedia> ended = new Event<IJVLCMedia>();
	private Event<IJVLCMedia> stopped = new Event<IJVLCMedia>();
	private Event<IJVLCMedia> positionChanged = new Event<IJVLCMedia>();
	private Event<Pair<IJVLCMedia,Long>> timeChanged = new Event<Pair<IJVLCMedia,Long>>();
	*/
	public IJVLCMedia newMedia(URI uri) throws RemoteException {
		debug("newMedia");
		String str = uri.toASCIIString().replace("+", "%2B");
		if (str.startsWith("file:/") && !str.startsWith("file:///"))
			str = "file:///" + str.substring(6);
		long id = JVLCServer.idManager.allocate();
		debug("newMedia: create MediaStore");
		MediaStore store = new MediaStore(id, new MediaDescriptor(JVLCServer.jvlc, str), new JVLCMedia(id));
		JVLCServer.medias.put(store);
		debug("newMedia: add Listener to Player");
		return store.media;
	}
	private static MediaStore media(IJVLCMedia media) throws RemoteException { 
		debug("retrieve media");
		return JVLCServer.medias.get(media.getID()); 
	}
	
	public void freeMedia(IJVLCMedia media) throws RemoteException {
		debug("freeMedia");
		MediaStore store = media(media);
		if (store == null) return;
		debug("freeMedia: release");
		store.release();
		debug("freeMedia: free from server");
		JVLCServer.medias.remove(store);
		JVLCServer.idManager.free(store.id);
	}
	
	public void start(IJVLCMedia media) throws RemoteException {
		debug("start");
		MediaStore store = media(media);
		if (store.paused)
			store.pause();
		else
			store.play(new JVLCMediaListener(media(media).media));
	}
	public void pause(IJVLCMedia media) throws RemoteException {
		debug("pause");
		media(media).pause();
	}
	public void stop(IJVLCMedia media) throws RemoteException {
		debug("stop");
		media(media).stop();
	}
	
	public double getPosition(IJVLCMedia media) throws RemoteException {
		debug("getPosition");
		return media(media).getPosition();
	}
	public void setPosition(IJVLCMedia media, double pos) throws RemoteException {
		debug("setPosition: " + pos);
		media(media).setPosition(pos);
	}
	public long getTime(IJVLCMedia media) throws RemoteException {
		debug("getTime");
		return media(media).getTime();
	}
	public void setTime(IJVLCMedia media, long time) throws RemoteException {
		debug("setTime: " + time);
		media(media).setTime(time);
	}

	public void setVideoOutput(long id) {
		debug("setVideoOutput");
		JVLCServer.jvlc.setVideoOutput(id);
	}
	public void outputClosed() throws RemoteException {
		debug("outputClosed");
		for (MediaStore store : JVLCServer.medias)
			store.invalidatePlayer();
	}
	
	public double getVolume() {
		debug("getVolume");
		return (double)JVLCServer.jvlc.getAudio().getVolume();
	}
	public void setVolume(double volume) {
		debug("setVolume");
		JVLCServer.jvlc.getAudio().setVolume((int)(volume));
	}
	private Boolean mute = null;
	public boolean getMute() {
		if (mute == null) {
			debug("getMute");
			mute = JVLCServer.jvlc.getAudio().getMute();
		}
		return mute;
	}
	public void setMute(boolean value) {
		if (mute != null && mute == value) return;
		debug("setMute");
		JVLCServer.jvlc.getAudio().setMute(value);
		mute = value;
	}
	
	public void free() {
		debug("free");
		//JVLCServer.jvlc.getMediaList().clear();
		//JVLCServer.jvlc.release();
		//JVLCServer.jvlc = null;
	}
	/*
	public Event<IJVLCMedia> started() { return started; }
	public Event<IJVLCMedia> paused() { return paused; }
	public Event<IJVLCMedia> ended() { return ended; }
	public Event<IJVLCMedia> stopped() { return stopped; }
	public Event<IJVLCMedia> positionChanged() { return positionChanged; }
	public Event<Pair<IJVLCMedia,Long>> timeChanged() { return timeChanged; }
	*/
	
	private static void debug(String message) {
//		if (Log.debug(JVLCMediaManager.class))
//			Log.debug(JVLCMediaManager.class, message);
		System.out.println("DEBUG: " + System.currentTimeMillis() + " MediaManager: " + message);
	}
}
