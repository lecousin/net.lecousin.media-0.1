package net.lecousin.media.jsound.internal;

import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;

import net.lecousin.framework.Pair;
import net.lecousin.media.jsound.AudioDecoder;

public class DecoderThread extends Thread {

	public DecoderThread(AudioDecoder decoder) {
		super("net.lecousin.media.jsound - Decoder");
		this.decoder = decoder;
	}
	
	AudioDecoder decoder;
	private static long NB_BUFFERED_SAMPLES = 100;
	private LinkedList<Pair<AudioFormat,byte[]>> samples = new LinkedList<Pair<AudioFormat,byte[]>>();
	private boolean stopped = false;
	private double curTime = 0;
	
	@Override
	public void run() {
		do {
			while (!stopped && samples.size() >= NB_BUFFERED_SAMPLES) {
				try { Thread.sleep(250); } catch (InterruptedException e) { break; }
			}
			Pair<AudioFormat,byte[]> sample;
			synchronized (decoder) {
				sample = decoder.decodeSample();
			}
			if (sample == null)
				break;
			synchronized (sample) {
				samples.add(sample);
			}
		} while (!stopped);
		stopped = true;
		decoder.close();
	}
	
	public void stopDecoder() {
		stopped = true;
	}
	
	public Pair<AudioFormat,byte[]> nextSample() {
		synchronized (this) {
			do {
				Pair<AudioFormat,byte[]> p = null;
				synchronized (samples) {
					if (!samples.isEmpty())
						p = samples.removeFirst();
				}
				if (p != null) {
					curTime += 1000*((double)1000/(double)p.getValue1().getSampleRate());
					return p;
				}
				if (stopped) return null;
				try { Thread.sleep(1); } catch (InterruptedException e) { return null; }
				if (stopped) return null;
			} while (true);
		}
	}
	
	public void seekTime(long time) {
		seekTime(time, false);
	}
	public void seekTime(long time, boolean reset) {
		if (time < 0) return;
		synchronized (this) {
			if (time > curTime) {
				while (nextSample() != null && curTime < time);
				return;
			}
			if (reset) return;
			synchronized (decoder) {
				synchronized (samples) {
					samples.clear();
					decoder.reset();
					curTime = 0;
				}
			}
			seekTime(time, true);
		}
	}
	
	public double getTime() { return curTime; }
}
