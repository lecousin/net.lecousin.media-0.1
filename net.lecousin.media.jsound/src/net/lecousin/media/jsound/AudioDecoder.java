package net.lecousin.media.jsound;

import javax.sound.sampled.AudioFormat;

import net.lecousin.framework.Triple;
import net.lecousin.framework.io.LCPartialBufferedInputStream;

public interface AudioDecoder {

	public void init(LCPartialBufferedInputStream stream);
	/** return decoded sample, null if end has been reached, <Null,Null,time> if sample was invalid */
	public Triple<AudioFormat,byte[],Double> decodeSample();
	/** return number of milliseconds skipped, or -1 if nothing has been skipped */
	public double skipSample();
	public void reset();
	public void close();
	
}
