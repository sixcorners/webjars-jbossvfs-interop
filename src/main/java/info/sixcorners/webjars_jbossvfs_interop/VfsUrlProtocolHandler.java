package info.sixcorners.webjars_jbossvfs_interop;

import static java.util.stream.Collectors.toSet;

import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.util.IncludeFileNameVirtualFileFilter;
import org.kohsuke.MetaInfServices;
import org.webjars.urlprotocols.UrlProtocolHandler;

@Slf4j
@MetaInfServices
public class VfsUrlProtocolHandler implements UrlProtocolHandler {
	private static final boolean enabled = checkEnabled();

	private static boolean checkEnabled() {
		try {
			Class.forName("org.jboss.vfs.VFS");
			return true;
		} catch (ClassNotFoundException e) {
			log.warn("webjars/jboss-vfs interop jar included but not used", e);
			return false;
		}
	}

	@Override
	public boolean accepts(String protocol) {
		if (!enabled)
			return false;
		return "vfs".equalsIgnoreCase(protocol);
	}

	@Override
	@SneakyThrows
	public Set<String> getAssetPaths(URL url, Pattern filterExpr,
			ClassLoader... classLoaders) {
		val filter = new IncludeFileNameVirtualFileFilter(filterExpr.pattern());
		return VFS.getChild(url.toURI()).getChildrenRecursively(filter)
				.stream().map(VirtualFile::getPathName).collect(toSet());
	}
}
