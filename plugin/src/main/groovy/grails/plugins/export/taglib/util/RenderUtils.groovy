package grails.plugins.export.taglib.util

import grails.core.GrailsApplication
import grails.plugins.metadata.GrailsPlugin
import grails.util.Holders
import org.apache.commons.codec.digest.DigestUtils

import java.rmi.server.UID
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Andreas Schmitt
 *
 */
class RenderUtils {

	GrailsApplication grailsApplication

	/**
	 * Create unique ID as hex representation.
	 */
	public static String getUniqueId() {
		return DigestUtils.md5Hex(new UID().toString())
    }

	/**
	 *
	 * @param pluginName
	 * @param contextPath
	 *
	 */
	public static String getResourcePath(String pluginName, String contextPath){
		def plugin = Holders.pluginManager.allPlugins.find {it.name == 'export'}
		String pluginVersion = plugin?.version

		"${contextPath}/plugins/${pluginName.toLowerCase()}-$pluginVersion"
	}

	/**
	 *
	 * @param pluginResourcePath
	 *
	 */
	public static String getApplicationResourcePath(String pluginResourcePath){
		try {
			Pattern pattern = Pattern.compile("(.*)/plugins.*");
			Matcher matcher = pattern.matcher(pluginResourcePath);

			if(matcher.matches()){
				return matcher.group(1);
			}
		}
		catch(Exception e){
			return ""
		}
	}

}
