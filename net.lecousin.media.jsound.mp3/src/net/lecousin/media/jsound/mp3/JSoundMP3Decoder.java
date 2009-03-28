package net.lecousin.media.jsound.mp3;

import javax.sound.sampled.AudioFormat;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.lecousin.framework.Triple;
import net.lecousin.framework.io.LCPartialBufferedInputStream;
import net.lecousin.framework.log.Log;
import net.lecousin.media.jsound.AudioDecoder;

public class JSoundMP3Decoder implements AudioDecoder {

	public JSoundMP3Decoder() {
	}
	
	private Decoder decoder;
	private Bitstream bitstream;
	LCPartialBufferedInputStream stream;

	public void init(LCPartialBufferedInputStream stream) {
		this.stream = stream;
		reset();
	}
	
	public synchronized void reset() {
		stream.move(0);
		bitstream = new Bitstream(stream, true);
		decoder = new Decoder();
	}

	public synchronized Triple<AudioFormat,byte[],Double> decodeSample() {
		try {
			Header h = bitstream.readFrame();
			
			if (h==null)
				return null;
				
			// sample buffer set when decoder constructed
			SampleBuffer output;
			try { output = (SampleBuffer)decoder.decodeFrame(h, bitstream); }
			catch (ArrayIndexOutOfBoundsException e) {
				bitstream.closeFrame();
				return new Triple<AudioFormat,byte[],Double>(null, null, (double)h.ms_per_frame());
			}
			
			if (output.getBufferLength() == 0) {
				bitstream.closeFrame();
				return new Triple<AudioFormat,byte[],Double>(null, null, (double)h.ms_per_frame());
			}
			byte[] sample = toByteArray(output.getBuffer(), 0, output.getBufferLength());
			
			bitstream.closeFrame();
		
			AudioFormat fmt = new AudioFormat(decoder.getOutputFrequency(),
					  16,
					  decoder.getOutputChannels(),
					  true,
					  false);
			
			return new Triple<AudioFormat,byte[],Double>(fmt, sample, (double)h.ms_per_frame());
		} catch (Throwable t) {
			if (Log.error(this))
				Log.error(this, "Error while decoding MP3 sample", t);
			return new Triple<AudioFormat,byte[],Double>(null, null, (double)0);
		}
	}
	
	public double skipSample() {
		try {
			Header h = bitstream.readFrame();
			if (h==null)
				return -1;
			double skipped = h.ms_per_frame();
			bitstream.closeFrame();
			return skipped;
		} catch (Throwable t) {
			if (Log.error(this))
				Log.error(this, "Error while decoding MP3 sample", t);
			return 0;
		}
	}

	protected byte[] toByteArray(short[] samples, int offs, int len)
	{
		byte[] b = new byte[len*2];
		int idx = 0;
		short s;
		while (len-- > 0)
		{
			s = samples[offs++];
			b[idx++] = (byte)s;
			b[idx++] = (byte)(s>>>8);
		}
		return b;
	}

	public void close() {
		try { bitstream.close(); } catch (BitstreamException e) {}
	}
}
