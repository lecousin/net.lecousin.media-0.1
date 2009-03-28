package net.lecousin.media.jsound.internal;

import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;

import net.lecousin.framework.Pair;
import net.lecousin.framework.Triple;
import net.lecousin.media.jsound.AudioDecoder;

public class DecoderThread extends Thread {

	public DecoderThread(AudioDecoder decoder) {
		super("net.lecousin.media.jsound - Decoder");
		this.decoder = decoder;
	}
	
	AudioDecoder decoder;
	private static long NB_BUFFERED_SAMPLES = 100;
	private LinkedList<Triple<AudioFormat,byte[],Double>> samples = new LinkedList<Triple<AudioFormat,byte[],Double>>();
	private boolean stopped = false;
	private double curTime = 0;
	
	@Override
	public void run() {
		do {
			while (!stopped && samples.size() >= NB_BUFFERED_SAMPLES) {
				try { Thread.sleep(250); } catch (InterruptedException e) { break; }
			}
			Triple<AudioFormat,byte[],Double> sample;
			synchronized (decoder) {
				sample = decoder.decodeSample();
				if (sample == null)
					break;
				synchronized (samples) {
					samples.add(sample);
				}
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
				Triple<AudioFormat,byte[],Double> p = null;
				synchronized (samples) {
					if (!samples.isEmpty())
						p = samples.removeFirst();
				}
				if (p != null) {
					curTime += p.getValue3();
					return new Pair<AudioFormat,byte[]>(p.getValue1(),p.getValue2());
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
			synchronized (decoder) {
				synchronized (samples) {
					if (time > getTime()) {
						while (!samples.isEmpty() && getTime() < time) {
							Triple<AudioFormat,byte[],Double> p = samples.removeFirst();
							curTime += p.getValue3();
						}
						while (time > getTime()) {
							double skipped = decoder.skipSample();
							if (skipped < 0) break;
							curTime += skipped;
						}
						return;
					}
					if (reset) return;
					samples.clear();
					decoder.reset();
					curTime = 0;
					seekTime(time, true);
				}
			}
		}
	}
	
	public double getTime() {
		return curTime;
	}
}
