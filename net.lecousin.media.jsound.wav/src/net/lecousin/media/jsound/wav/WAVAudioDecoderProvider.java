package net.lecousin.media.jsound.wav;

import net.lecousin.framework.files.TypedFile;
import net.lecousin.framework.files.audio.wav.WAVFile;
import net.lecousin.framework.files.audio.wav.WAVFormat;
import net.lecousin.media.jsound.AudioDecoder;
import net.lecousin.media.jsound.AudioDecoderProvider;

public class WAVAudioDecoderProvider implements AudioDecoderProvider {

	public WAVAudioDecoderProvider() {
	}

	public AudioDecoder get(TypedFile file) {
		if (file instanceof WAVFile) {
			WAVFormat wav = (WAVFormat)file.getInfo();
			if (wav != null) {
				if (wav.getCompression() == WAVFormat.COMP_MP3)
					return new WAV_MP3_Decoder(wav.getDataChunkPos() >= 0 ? wav.getDataChunkPos() : 0);
				//return new WAV_Misc_Decoder();
			}
		}
		return null;
	}
}
