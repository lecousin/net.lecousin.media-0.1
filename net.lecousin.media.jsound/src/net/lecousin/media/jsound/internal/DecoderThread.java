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
	
	@Override
	public void run() {
		do {
			while (!stopped && samples.size() >= NB_BUFFERED_SAMPLES) {
				try { Thread.sleep(100); } catch (InterruptedException e) { break; }
			}
			Pair<AudioFormat,byte[]> sample = decoder.decodeSample();
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
		do {
			synchronized (samples) {
				if (!samples.isEmpty())
					return samples.removeFirst();
			}
			if (stopped) return null;
			try { Thread.sleep(1); } catch (InterruptedException e) { return null; }
			if (stopped) return null;
		} while (true);
	}
}
