package net.lecousin.media.jsound;

import javax.sound.sampled.AudioFormat;

import net.lecousin.framework.Pair;
import net.lecousin.framework.io.LCPartialBufferedInputStream;

public interface AudioDecoder {

	public void init(LCPartialBufferedInputStream stream);
	public Pair<AudioFormat,byte[]> decodeSample();
	public void reset();
	public void close();
	
}
