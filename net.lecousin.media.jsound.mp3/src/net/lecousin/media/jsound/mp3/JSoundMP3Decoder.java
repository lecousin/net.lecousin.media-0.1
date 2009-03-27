package net.lecousin.media.jsound.mp3;

import javax.sound.sampled.AudioFormat;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.lecousin.framework.Pair;
import net.lecousin.framework.io.LCPartialBufferedInputStream;
import net.lecousin.framework.log.Log;
import net.lecousin.media.jsound.AudioDecoder;

public class JSoundMP3Decoder implements AudioDecoder {

	public JSoundMP3Decoder() {
	}
	
	private Decoder decoder;
	private Bitstream bitstream;

	public void init(LCPartialBufferedInputStream stream) {
		stream.move(0);
		bitstream = new Bitstream(stream);
		decoder = new Decoder();
	}

	public Pair<AudioFormat,byte[]> decodeSample() {
		try {
			Header h = bitstream.readFrame();
			
			if (h==null)
				return null;
				
			// sample buffer set when decoder constructed
			SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitstream);
								
			byte[] sample = toByteArray(output.getBuffer(), 0, output.getBufferLength());
			
			bitstream.closeFrame();
		
			AudioFormat fmt = new AudioFormat(decoder.getOutputFrequency(),
					  16,
					  decoder.getOutputChannels(),
					  true,
					  false);
			
			return new Pair<AudioFormat,byte[]>(fmt, sample);
		} catch (Throwable t) {
			if (Log.error(this))
				Log.error(this, "Error while decoding MP3 sample", t);
			return null;
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
