package net.lecousin.media.jsound;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import net.lecousin.framework.Pair;
import net.lecousin.framework.event.Event;
import net.lecousin.framework.media.Media;
import net.lecousin.framework.media.MediaPlayerPlugin;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class JavaSoundMediaPlayerPlugin implements MediaPlayerPlugin {

	public JavaSoundMediaPlayerPlugin() {
		threadEvents.start();
	}
	
	private List<JavaSoundMedia> medias = new LinkedList<JavaSoundMedia>();
	
	public Event<Media> started = new Event<Media>();
	public Event<Media> paused = new Event<Media>();
	public Event<Media> stopped = new Event<Media>();
	public Event<Media> ended = new Event<Media>();
	public Event<Media> positionChanged = new Event<Media>();
	public Event<Pair<Media, Long>> timeChanged = new Event<Pair<Media,Long>>();
	private ThreadEvents threadEvents = new ThreadEvents();
	
	public Media newMedia(URI uri) {
		JavaSoundMedia media = new JavaSoundMedia(uri);
		medias.add(media);
		return media;
	}
	public void freeMedia(Media media) {
		((JavaSoundMedia)media).close();
		medias.remove(media);
	}
	public void free() {
		threadEvents.quit = true;
		for (JavaSoundMedia m : medias)
			m.close();
		medias.clear();
	}

	
	public void start(Media media) {
		((JavaSoundMedia)media).start(this);
	}
	public void pause(Media media) {
		((JavaSoundMedia)media).pause();
	}
	public void stop(Media media) {
		((JavaSoundMedia)media).stop();
	}
	
	public double getPosition(Media media) {
		return ((JavaSoundMedia)media).getPosition();
	}
	public void setPosition(Media media, double pos) {
		((JavaSoundMedia)media).setPosition(pos);
	}

	public long getTime(Media media) {
		return ((JavaSoundMedia)media).getTime();
	}
	public void setTime(Media media, long time) {
		((JavaSoundMedia)media).setTime(time);
	}
	
	
	public double getVolume() {
		return 0; // TODO
	}
	public void setVolume(double volume) {
		// TODO Auto-generated method stub
	}
	public boolean getMute() {
		// TODO Auto-generated method stub
		return false;
	}
	public void setMute(boolean value) {
		// TODO Auto-generated method stub
	}



	public Event<Media> started() { return started; }
	public Event<Media> paused() { return paused; }
	public Event<Media> stopped() { return stopped; }
	public Event<Media> ended() { return ended; }
	public Event<Media> positionChanged() { return positionChanged; }
	public Event<Pair<Media, Long>> timeChanged() { return timeChanged; }

	public Control createVisual(Composite parent) {
		return null;
	}
	
	public void timeChanged(JavaSoundMedia media, long time) {
		synchronized (threadEvents.timeChanged) {
			threadEvents.timeChanged.add(new Pair<Media,Long>(media,time));
		}
	}
	
	private class ThreadEvents extends Thread {
		public ThreadEvents() {
			super("JavaSoundMedia - Events Sender");
		}
		LinkedList<Pair<Media,Long>> timeChanged = new LinkedList<Pair<Media,Long>>();
		boolean quit = false;
		@Override
		public void run() {
			do {
				boolean didSomething = false;
				Pair<Media,Long> time = null;
				synchronized (timeChanged) {
					if (!timeChanged.isEmpty())
						time = timeChanged.removeFirst();
				}
				if (time != null) {
					JavaSoundMediaPlayerPlugin.this.timeChanged.fire(time);
					didSomething = true;
				}
				if (!didSomething)
					try { Thread.sleep(100); } catch (InterruptedException e) { break; }
			} while (!quit);
		}
	}
}
