package net.lecousin.media.jvlc.client.eclipse;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.lecousin.framework.Pair;
import net.lecousin.framework.application.Application;
import net.lecousin.framework.event.Event;
import net.lecousin.framework.event.Event.Listener;
import net.lecousin.framework.log.Log;
import net.lecousin.framework.media.Media;
import net.lecousin.framework.media.MediaPlayerPlugin;
import net.lecousin.media.jvlc.client.JVLCClient;
import net.lecousin.media.jvlc.server.IJVLCMedia;
import net.lecousin.media.jvlc.server.IJVLCMediaManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.sun.jna.Native;

public class JVLCMediaManager implements MediaPlayerPlugin {

	public JVLCMediaManager() throws Throwable {
		super();
		initJVLC();
		JVLCClient.started.addListener(new Started());
		JVLCClient.paused.addListener(new Paused());
		JVLCClient.ended.addListener(new Ended());
		JVLCClient.stopped.addListener(new Stopped());
		JVLCClient.positionChanged.addListener(new PositionChanged());
		JVLCClient.timeChanged.addListener(new TimeChanged());
	}

	IJVLCMediaManager jvlc;
	private Event<Media> started = new Event<Media>();
	private Event<Media> paused = new Event<Media>();
	private Event<Media> ended = new Event<Media>();
	private Event<Media> stopped = new Event<Media>();
	private Event<Media> positionChanged = new Event<Media>();
	private Event<Pair<Media,Long>> timeChanged = new Event<Pair<Media,Long>>();
	private List<JVLCMedia> medias = new LinkedList<JVLCMedia>();
	private Canvas canvas = null;
	
	private void initJVLC() throws Throwable {
		File extrasPath = new File(Application.deployPath, "extras");
		jvlc = JVLCClient.get(5100, extrasPath.getAbsolutePath(), new Listener<Integer>() {
			public void fire(Integer event) {
				if (jvlc == null) return;
				try { restart(null); }
				catch (Throwable t) {
					if (Log.error(this))
						Log.error(this, "Unable to restart JVLC", t);
				}
				restarting = false;
			}
		});
	}
	
	private boolean restarting = false;
	private boolean restart(RemoteException e) {
		if (restarting) {
			if (e != null && Log.error(this))
				Log.error(this, "Persistent remote error", e);
			restarting = false;
			return false;
		}
		if (e != null && Log.warning(this))
			Log.warning(this, "Remote error. Try to restart and re-execute command.", e);
		restarting = true;
		try { initJVLC(); }
		catch (Throwable t) {
			if (Log.error(this)) Log.error(this, "Unable to restart JVLC", t);
			restarting = false;
			return false;
		}
		if (canvas != null && canvas.isDisplayable())
			try { jvlc.setVideoOutput(Native.getComponentID(canvas)); }
			catch (RemoteException e2) { if (Log.error(this)) Log.error(this, "Unable to set video output after restart", e2); return false; }
		for (JVLCMedia media : medias) {
			try { 
				IJVLCMedia m = jvlc.newMedia(media.uri); 
				media.reset(m);
				if (media.started) {
					jvlc.start(m);
					if (media.time != 0)
						jvlc.setTime(m, media.time);
					if (media.paused)
						jvlc.pause(m);
				}
			}
			catch (RemoteException e2) { if (Log.error(this)) Log.error(this, "Unable to reset media " + media.uri + " after restart, this media will probably not work."); }
		}
		return true;
	}
	private void success() {
		restarting = false;
	}
	
	public Media newMedia(URI uri) {
		JVLCClient.startTransaction();
		try {
			JVLCMedia media = new JVLCMedia(jvlc.newMedia(uri), uri);
			medias.add(media);
			success();
			return media;
		}
		catch (RemoteException e) { if (restart(e)) return newMedia(uri);  }
		finally { JVLCClient.endTransaction(); }
		return null;
	}
	public void freeMedia(Media media) {
		JVLCClient.startTransaction();
		try { jvlc.freeMedia(((JVLCMedia)media).media); success(); }
		catch (RemoteException e) { if (restart(e)) { freeMedia(media); return; } }
		finally { JVLCClient.endTransactionStop(); }
		medias.remove((JVLCMedia)media);
	}
	
	public void start(Media media) {
		JVLCClient.startTransaction();
		try { jvlc.start(((JVLCMedia)media).media); success(); ((JVLCMedia)media)._start(); }
		catch (RemoteException e) { if (restart(e)) { start(media); return; } }
		finally { JVLCClient.endTransaction(); }
	}
	public void pause(Media media) {
		JVLCClient.startTransaction();
		try { jvlc.pause(((JVLCMedia)media).media); success(); ((JVLCMedia)media)._pause();}
		catch (RemoteException e) { if (restart(e)) { pause(media); return; } }
		finally { JVLCClient.endTransaction(); }
	}
	public void stop(Media media) {
		JVLCClient.startTransaction();
		try { jvlc.stop(((JVLCMedia)media).media); success(); ((JVLCMedia)media)._stop(); }
		catch (RemoteException e) { if (restart(e)) { stop(media); return; } }
		finally { JVLCClient.endTransactionStop(); }
	}
	
	public double getPosition(Media media) {
		JVLCClient.startTransaction();
		try { double pos = jvlc.getPosition(((JVLCMedia)media).media); success(); return pos; }
		catch (RemoteException e) { if (restart(e)) return getPosition(media); }
		finally { JVLCClient.endTransaction(); }
		return 0;
	}
	public void setPosition(Media media, double pos) {
		JVLCClient.startTransaction();
		try { jvlc.setPosition(((JVLCMedia)media).media, pos); success(); }
		catch (RemoteException e) { if (restart(e)) { setPosition(media, pos); return; } }
		finally { JVLCClient.endTransaction(); }
	}
	public long getTime(Media media) {
		JVLCClient.startTransaction();
		try { long time = jvlc.getTime(((JVLCMedia)media).media); success(); return time; }
		catch (RemoteException e) { if (restart(e)) return getTime(media); }
		finally { JVLCClient.endTransaction(); }
		return 0;
	}
	public void setTime(Media media, long time) {
		JVLCClient.startTransaction();
		try { jvlc.setTime(((JVLCMedia)media).media, time); success(); }
		catch (RemoteException e) { if (restart(e)) { setTime(media, time); return; } }
		finally { JVLCClient.endTransaction(); }
	}

	public Control createVisual(Composite parent) {
		Composite panel = new Composite(parent, SWT.EMBEDDED);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.spacing = 0;
		panel.setLayout(layout);
		Frame frame = SWT_AWT.new_Frame(panel);
		GridLayout lm = new GridLayout(1, 1, 0, 0); 
		frame.setLayout(lm);
		canvas = new Canvas();
		frame.add(canvas);
		JVLCClient.startTransaction();
		try { 
			jvlc.setVideoOutput(Native.getComponentID(canvas)); 
			success();
			panel.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (jvlc == null) return;
					try { jvlc.outputClosed(); }
					catch (RemoteException ex) {}
				}
			});
		}
		catch (RemoteException e) { if (restart(e)) return createVisual(parent); }
		finally { JVLCClient.endTransaction(); }
		return panel;
	}
	
	public double getVolume() {
		JVLCClient.startTransaction();
		try { double vol = jvlc.getVolume(); success(); return vol; }
		catch (RemoteException e) { if (restart(e)) return getVolume(); }
		finally { JVLCClient.endTransaction(); }
		return 0;
	}
	public void setVolume(double volume) {
		JVLCClient.startTransaction();
		try { jvlc.setVolume(volume); success(); }
		catch (RemoteException e) { if (restart(e)) { setVolume(volume); return; } }
		finally { JVLCClient.endTransaction(); }
	}
	public boolean getMute() {
		JVLCClient.startTransaction();
		try { return jvlc.getMute(); }
		catch (RemoteException e) { if (Log.error(this)) Log.error(this, "Remote exception", e); }
		finally { JVLCClient.endTransaction(); }
		return true;
	}
	public void setMute(boolean value) {
		JVLCClient.startTransaction();
		try { jvlc.setMute(value); success(); }
		catch (RemoteException e) { if (restart(e)) { setMute(value); return; } }
		finally { JVLCClient.endTransaction(); }
	}
	
	public void free() {
		JVLCClient.startTransaction();
		class Freer extends Thread {
			boolean done = false;
			@Override
			public void run() {
				try { jvlc.free(); }
				catch (RemoteException e) { if (Log.error(this)) Log.error(this, "Remote exception", e); }
				done = true;
			}
		};
		Freer t = new Freer();
		t.run();
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < 10000 && !t.done)
			try { Thread.sleep(100); } catch (InterruptedException e) { break; }
		if (!t.done) {
			jvlc = null;
			JVLCClient.kill();
		} else {
			JVLCClient.free(jvlc);
			jvlc = null;
		}
		JVLCClient.endTransaction();
	}
	
	private Media getMedia(IJVLCMedia m) {
		for (JVLCMedia jm : new ArrayList<JVLCMedia>(medias))
			try { if (jm.media.getID() == m.getID()) return jm; } catch (RemoteException e) {}
		return null;
	}
	
	public Event<Media> started() { return started; }
	public Event<Media> paused() { return paused; }
	public Event<Media> ended() { return ended; }
	public Event<Media> stopped() { return stopped; }
	public Event<Media> positionChanged() { return positionChanged; }
	public Event<Pair<Media,Long>> timeChanged() { return timeChanged; }
	
	private class Started implements Listener<IJVLCMedia> {
		public void fire(IJVLCMedia event) { Media m = getMedia(event); if (m != null) { started.fire(m); ((JVLCMedia)m)._started(); } }
	}
	private class Paused implements Listener<IJVLCMedia> {
		public void fire(IJVLCMedia event) { Media m = getMedia(event); if (m != null) { paused.fire(m); ((JVLCMedia)m)._paused(); } }
	}
	private class Ended implements Listener<IJVLCMedia> {
		public void fire(IJVLCMedia event) { Media m = getMedia(event); if (m != null) { ended.fire(m); ((JVLCMedia)m)._ended(); } }
	}
	private class Stopped implements Listener<IJVLCMedia> {
		public void fire(IJVLCMedia event) { Media m = getMedia(event); if (m != null) { stopped.fire(m); ((JVLCMedia)m)._stopped(); } }
	}
	private class PositionChanged implements Listener<IJVLCMedia> {
		public void fire(IJVLCMedia event) { Media m = getMedia(event); if (m != null) { positionChanged.fire(m); } }
	}
	private class TimeChanged implements Listener<Pair<IJVLCMedia,Long>> {
		public void fire(Pair<IJVLCMedia,Long> event) { Media m = getMedia(event.getValue1()); if (m != null) { timeChanged.fire(new Pair<Media,Long>(m,event.getValue2()/1000)); ((JVLCMedia)m)._timeChanged(event.getValue2()/1000); } }
	}
}
