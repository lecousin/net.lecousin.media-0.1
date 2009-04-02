package net.lecousin.media.jsound.wav;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.lecousin.framework.Triple;
import net.lecousin.framework.io.LCPartialBufferedInputStream;
import net.lecousin.framework.log.Log;
import net.lecousin.media.jsound.AudioDecoder;

public class WAV_Misc_Decoder implements AudioDecoder {

	public WAV_Misc_Decoder() {
	}
	
	private AudioInputStream stream = null;
	
	public void init(LCPartialBufferedInputStream stream) {
		try { this.stream = AudioSystem.getAudioInputStream(stream); }
		catch (IOException e) {
			if (Log.error(this))
				Log.error(this, "Unable to get AudioInputStream from WAV", e);
		} catch (UnsupportedAudioFileException e) {
			if (Log.error(this))
				Log.error(this, "Unsupported WAV format", e);
		}
	}

	private byte[] buffer = new byte[65536];
	public Triple<AudioFormat, byte[], Double> decodeSample() {
		if (stream == null) return null;
		try {
			int nb = stream.read(buffer);
			if (nb <= 0) return null;
			byte[] buf = new byte[nb];
			System.arraycopy(buffer, 0, buf, 0, nb);
			return new Triple<AudioFormat, byte[], Double>(stream.getFormat(), buf, new Double(1));
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
		// TODO Auto-generated method stub

	}

	public double skipSample() {
		// TODO Auto-generated method stub
		return 10000;
	}

}
