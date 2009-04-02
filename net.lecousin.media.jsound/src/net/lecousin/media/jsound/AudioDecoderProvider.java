package net.lecousin.media.jsound;

import net.lecousin.framework.files.TypedFile;

public interface AudioDecoderProvider {

	public AudioDecoder get(TypedFile file);
	
}
