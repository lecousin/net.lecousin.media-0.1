package net.lecousin.media.jsound.wav;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.lecousin.framework.Triple;
import net.lecousin.framework.io.LCMovableInputStream;
import net.lecousin.framework.io.LCPartialBufferedInputStream;
import net.lecousin.framework.log.Log;
import net.lecousin.media.jsound.AudioDecoder;

public class WAV_Misc_Decoder implements AudioDecoder {

	public WAV_Misc_Decoder() {
	}
	
	private AudioInputStream stream = null;
	private LCMovableInputStream initialStream = null;
	
	public void init(LCPartialBufferedInputStream stream) {
		this.initialStream = stream;
		reset();
	}

	public Triple<AudioFormat, byte[], Double> decodeSample() {
		if (stream == null) return null;
		AudioFormat fmt = stream.getFormat();
		float sr = fmt.getSampleRate();
		int ss = fmt.getSampleSizeInBits();
		int ch = fmt.getChannels();
		// buffer for 1s.
		byte[] buffer = new byte[(int)(sr*ch*ss/8)];
		try {
			int nb = stream.read(buffer);
			if (nb <= 0) return null;
			byte[] buf;
			if (nb == buffer.length)
				buf = buffer;
			else {
				buf = new byte[nb];
				System.arraycopy(buffer, 0, buf, 0, nb);
			}
			double time = ((double)(nb*1000*8))/(double)(sr*ss*ch);
			return new Triple<AudioFormat, byte[], Double>(fmt, buf, time);
		} catch (IOException e) {
			if (Log.error(this))
				Log.error(this, "Error while reading WAV", e);
			return null;
		}
	}
	
	public void close() {
		if (stream != null) 
			try { stream.close(); }
			catch (IOException e) {}
	}

	public void reset() {
		try { 
			initialStream.move(0);
			this.stream = AudioSystem.getAudioInputStream(initialStream); 
		}
		catch (IOException e) {
			if (Log.error(this))
				Log.error(this, "Unable to get AudioInputStream from WAV", e);
		} catch (UnsupportedAudioFileException e) {
			if (Log.error(this))
				Log.error(this, "Unsupported WAV format", e);
		}
	}

	public double skipSample() {
		AudioFormat fmt = stream.getFormat();
		try {
			stream.skip((long)(fmt.getSampleRate()*fmt.getSampleSizeInBits()*fmt.getChannels()/8));
		} catch (IOException e) {
		}
		return 1000;
	}

}
