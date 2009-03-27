package net.lecousin.media.jsound;

import net.lecousin.framework.eclipse.extension.EclipsePluginExtensionUtil;
import net.lecousin.framework.files.TypedFile;
import net.lecousin.framework.log.Log;
import net.lecousin.media.jsound.internal.EclipsePlugin;

public class AudioDecoderPlugins {

	private AudioDecoderPlugins() {}
	
	public static AudioDecoder get(TypedFile file) {
		String type = file.getType().getFullName();
		try {
			return EclipsePluginExtensionUtil.createInstanceFromNode(EclipsePlugin.ID, "decoder", "decoder", "filetype", type, "class", AudioDecoder.class, new Object[][] { new Object[] {} });
		} catch (Throwable t) {
			if (Log.error(AudioDecoderPlugins.class))
				Log.error(AudioDecoderPlugins.class, "Unable to instantiate AudioDecoder", t);
			return null;
		}
	}
	
}
