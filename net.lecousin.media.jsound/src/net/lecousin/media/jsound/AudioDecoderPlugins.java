package net.lecousin.media.jsound;

import java.util.LinkedList;
import java.util.List;

import net.lecousin.framework.eclipse.extension.EclipsePluginExtensionUtil;
import net.lecousin.framework.files.TypedFile;
import net.lecousin.framework.log.Log;
import net.lecousin.media.jsound.internal.EclipsePlugin;

import org.eclipse.core.runtime.IConfigurationElement;

public class AudioDecoderPlugins {

	private AudioDecoderPlugins() {}
	
	private static List<AudioDecoderProvider> providers = null;
	public static AudioDecoder get(TypedFile file) {
		if (providers == null) {
			providers = new LinkedList<AudioDecoderProvider>();
			for (IConfigurationElement ext : EclipsePluginExtensionUtil.getExtensionsSubNode(EclipsePlugin.ID, "decoder", "decoder")) {
				try {
					AudioDecoderProvider provider = EclipsePluginExtensionUtil.createInstance(AudioDecoderProvider.class, ext, "provider", new Object[][] { new Object[] {} });
					providers.add(provider);
				} catch (Throwable t) {
					if (Log.error(AudioDecoderPlugins.class))
						Log.error(AudioDecoderPlugins.class, "Unable to instantiate AudioDecoderProvider", t);
					return null;
				}
			}
		}
		for (AudioDecoderProvider provider : providers) {
			AudioDecoder decoder = provider.get(file);
			if (decoder != null)
				return decoder;
		}
		return null;
	}
	
}
