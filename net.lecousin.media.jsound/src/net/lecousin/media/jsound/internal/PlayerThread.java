package net.lecousin.media.jsound.internal;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import net.lecousin.framework.Pair;
import net.lecousin.media.jsound.JavaSoundMedia;
import net.lecousin.media.jsound.JavaSoundMediaPlayerPlugin;

public class PlayerThread extends Thread {

	public PlayerThread(DecoderThread decoder, JavaSoundMedia media, JavaSoundMediaPlayerPlugin plugin) {
		super("net.lecousin.media.jsound - Player");
		this.decoder = decoder;
		this.plugin = plugin;
		this.media = media;
	}

	private JavaSoundMediaPlayerPlugin plugin;
	private JavaSoundMedia media;
	private DecoderThread decoder;
	private SourceDataLine source = null;
	private AudioFormat format = null;
	private boolean stopped = false;
	private boolean paused = false;
	private double lastTime = 0;
	private boolean ended = false;
	
	@Override
	public void run() {
		plugin.started.fire(media);
		do {
			if (paused && !stopped) {
				plugin.paused.fire(media);
				while (paused && !stopped) {
					try { Thread.sleep(100); } catch (InterruptedException e) { break; }
				}
			}
			if (stopped) break;
			Pair<AudioFormat,byte[]> sample = decoder.nextSample();
			if (sample == null)
				break;
			byte[] data = sample.getValue2();
			if (data == null || data.length == 0) continue;
			if (source == null || !format.matches(sample.getValue1())) {
				format = sample.getValue1();
				try {
					SourceDataLine prevSource = source;
					source = (SourceDataLine)AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
					source.open(format);
					source.start();
					if (prevSource != null) {
						prevSource.drain();
						prevSource.close();
					}
				} catch (LineUnavailableException e) {
					source = null;
				}
			}
			if (source != null)
				source.write(data, 0, data.length);
			double time = decoder.getTime();
			if (((long)lastTime) != ((long)time)) {
				plugin.timeChanged(media, (long)time);
				lastTime = time;
			}
		} while (!stopped);
		if (stopped)
			plugin.stopped.fire(media);
		else
			plugin.ended.fire(media);
		stopped = true;
		if (source != null)
			source.close();
		ended = true;
	}
	
	public boolean pausePlayer() {
		return (paused = !paused);
	}
	
	public void stopPlayer() {
		stopped = true;
		while (!ended)
			try { Thread.sleep(10); } catch (InterruptedException e) {}
	}
	
	public long getTime() {
		return (long)decoder.getTime();
	}
	public void setTime(long time) {
		decoder.seekTime(time);
		if (source != null)
			source.flush();
	}
	
}
