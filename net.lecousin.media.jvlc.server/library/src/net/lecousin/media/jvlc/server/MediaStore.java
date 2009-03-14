package net.lecousin.media.jvlc.server;

import java.util.ArrayList;
import java.util.LinkedList;

import net.lecousin.framework.collections.SelfMap;

import org.videolan.jvlc.MediaDescriptor;
import org.videolan.jvlc.MediaPlayer;
import org.videolan.jvlc.event.MediaPlayerListener;

public class MediaStore implements SelfMap.Entry<Long> {

	public MediaStore(long id, MediaDescriptor descriptor, JVLCMedia media) {
		this.id = id;
		this.descriptor = descriptor;
		this.media = media;
	}

	long id;
	MediaDescriptor descriptor;
	private MediaPlayer player = null;
	JVLCMedia media;
	Listener listener;
	boolean paused = false;
	boolean playing = false;
	
	public Long getHashObject() {
		return id;
	}
	
	void invalidatePlayer() {
		player = null;
	}
	
	void play(MediaPlayerListener listener) {
		playing = true;
		if (player == null) {
			player = descriptor.getMediaPlayer();
			this.listener = new Listener(listener);
			player.addListener(this.listener);
		}
		PlayWait wait = new PlayWait();
		this.listener.add(wait);
		player.play();
		wait(wait);
	}
	void pause() {
		if (player == null) return;
		Waiter wait;
		synchronized (this) {
			if (paused)
				wait = new PlayWait();
			else
				wait = new PauseWait();
			paused = !paused;
		}
		listener.add(wait);
		player.pause();
		wait(wait);
	}
	void stop() {
		if (player == null) return;
		if (playing) {
			StopWait wait = new StopWait();
			listener.add(wait);
			System.out.println("stop: stop player");
			if (playing) {
				player.stop();
				wait(wait);
			} else
				listener.remove(wait);
		}
		if (player != null) {
			//System.out.println("stop: release player");
			//player.release();
			player = null;
		}
	}
	
	long getLength() {
		if (player == null) return -1;
		return player.getLength();
	}
	
	double getPosition() {
		if (player == null) return 0;
		return player.getPosition();
	}
	void setPosition(double pos) {
		if (player == null) return;
		PositionWait wait = new PositionWait(pos);
		listener.add(wait);
		player.setPosition((float)pos);
		wait(wait);
	}
	
	long getTime() {
		if (player == null) return 0;
		return player.getTime();
	}
	void setTime(long time) {
		if (player == null) return;
		TimeWait wait = new TimeWait(time);
		listener.add(wait);
		player.setTime(time);
		wait(wait);
	}
	
	void release() {
		if (player != null)
			stop();
		System.out.println("release media: release descriptor");
		descriptor.release();
		descriptor = null;
	}
	
	private class Listener implements MediaPlayerListener {
		Listener(MediaPlayerListener listener) { listeners.add(listener); }
		private LinkedList<MediaPlayerListener> listeners = new LinkedList<MediaPlayerListener>();
		void add(MediaPlayerListener listener) { listeners.add(0, listener); }
		void remove(MediaPlayerListener listener) { listeners.remove(listener); }
		public void playing(MediaPlayer mediaPlayer) {
			for (MediaPlayerListener listener : new ArrayList<MediaPlayerListener>(listeners))
				listener.playing(mediaPlayer);
		}
		public void paused(MediaPlayer mediaPlayer) {
			for (MediaPlayerListener listener : new ArrayList<MediaPlayerListener>(listeners))
				listener.paused(mediaPlayer);
		}
		public void stopped(MediaPlayer mediaPlayer) {
			playing = false;
			for (MediaPlayerListener listener : new ArrayList<MediaPlayerListener>(listeners))
				listener.stopped(mediaPlayer);
		}
		public void positionChanged(MediaPlayer mediaPlayer) {
			for (MediaPlayerListener listener : new ArrayList<MediaPlayerListener>(listeners))
				listener.positionChanged(mediaPlayer);
		}
		public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
			for (MediaPlayerListener listener : new ArrayList<MediaPlayerListener>(listeners))
				listener.timeChanged(mediaPlayer, newTime);
		}
		public void endReached(MediaPlayer mediaPlayer) {
			playing = false;
			for (MediaPlayerListener listener : new ArrayList<MediaPlayerListener>(listeners))
				listener.endReached(mediaPlayer);
		}
		public void errorOccurred(MediaPlayer mediaPlayer) {
			playing = false;
			for (MediaPlayerListener listener : new ArrayList<MediaPlayerListener>(listeners))
				listener.errorOccurred(mediaPlayer);
		}
	}
	private abstract class Waiter implements MediaPlayerListener {
		boolean done = false;
		public void playing(MediaPlayer mediaPlayer) {}
		public void paused(MediaPlayer mediaPlayer) {}
		public void stopped(MediaPlayer mediaPlayer) {}
		public void endReached(MediaPlayer mediaPlayer) {}
		public void positionChanged(MediaPlayer mediaPlayer) {}
		public void timeChanged(MediaPlayer mediaPlayer, long newTime) {}
		public void errorOccurred(MediaPlayer mediaPlayer) { done = true; }
	}
	
	private void wait(Waiter wait) {
		long start = System.currentTimeMillis();
		while (!wait.done && System.currentTimeMillis() - start < 10000)
			try { Thread.sleep(10); } catch (InterruptedException e) { break; }
		this.listener.remove(wait);
	}
	
	private class PlayWait extends Waiter {
		@Override
		public void playing(MediaPlayer arg0) { done = true; }
	}
	private class PauseWait extends Waiter {
		@Override
		public void paused(MediaPlayer arg0) { done = true; }
	}
	private class StopWait extends Waiter {
		@Override
		public void stopped(MediaPlayer arg0) { done = true; }
		@Override
		public void endReached(MediaPlayer arg0) { done = true; }
	}
	private class PositionWait extends Waiter {
		PositionWait(double pos) { this.pos = pos; }
		double pos;
		@Override
		public void positionChanged(MediaPlayer arg0) { if (arg0.getPosition() >= pos) done = true; }
		@Override
		public void timeChanged(MediaPlayer arg0, long arg1) { if (arg1/1000 >= pos*arg0.getLength()) done = true; }
	}
	private class TimeWait extends Waiter {
		TimeWait(long time) { this.time = time; }
		long time;
		@Override
		public void timeChanged(MediaPlayer arg0, long time) { if (time/1000 == this.time) done = true; }
	}
}
