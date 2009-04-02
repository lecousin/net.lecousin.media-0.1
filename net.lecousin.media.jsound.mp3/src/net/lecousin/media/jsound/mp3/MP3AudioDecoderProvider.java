package net.lecousin.media.jsound.mp3;

import net.lecousin.framework.files.TypedFile;
import net.lecousin.framework.files.audio.mp3.MP3File;
import net.lecousin.media.jsound.AudioDecoder;
import net.lecousin.media.jsound.AudioDecoderProvider;

public class MP3AudioDecoderProvider implements AudioDecoderProvider {

	public MP3AudioDecoderProvider() {
	}

	public AudioDecoder get(TypedFile file) {
		if (file instanceof MP3File)
			return new JSoundMP3Decoder();
		return null;
	}

}
