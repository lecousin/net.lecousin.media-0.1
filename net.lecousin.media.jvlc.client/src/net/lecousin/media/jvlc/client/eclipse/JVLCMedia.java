package net.lecousin.media.jvlc.client.eclipse;

import java.net.URI;
import java.rmi.RemoteException;

import net.lecousin.framework.log.Log;
import net.lecousin.framework.media.Media;
import net.lecousin.media.jvlc.server.IJVLCMedia;

public class JVLCMedia implements Media {

	public JVLCMedia(IJVLCMedia media, URI uri) {
		this.media = media;
		this.uri = uri;
	}
	
	IJVLCMedia media;
	URI uri;
	
	void reset(IJVLCMedia media) {
		this.media = media;
	}
	
	public long getDuration() {
		try { return media.getDuration(); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "Remote exception", e);
			return 0;
		}
	}

	public URI getURI() {
		/*
		try { return media.getURI(); }
		catch (RemoteException e) {
			if (Log.error(this))
				Log.error(this, "Remote exception", e);
			return null;
		}*/
		return uri;
	}
	
	boolean started = false;
	boolean paused = false;
	long time = 0;
	void _start() { started = true; paused = false; }
	void _started() { started = true; paused = false; }
	void _pause() { paused = true; }
	void _paused() { }
	void _ended() { started = false; time = 0; }
	void _stop() { started = false; time = 0; }
	void _stopped() { started = false; time = 0; }
	void _timeChanged(long time) { this.time = time; }
}
