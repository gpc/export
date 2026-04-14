package grails.plugins.export

import grails.plugins.export.taglib.util.RenderUtils
import groovy.xml.MarkupBuilder

class ExportTagLib {

	static namespace = "export"

	def formats = { attrs ->
		StringWriter writer = new StringWriter()
		def builder = new MarkupBuilder(writer)

		if(!attrs?.'class'){
			attrs.'class' = "export"
		}

		String action = actionName
		String controller = controllerName

		if(attrs?.action){
			action = new String(attrs.action)
			attrs.remove("action")
		}

		if(attrs?.controller){
			controller = new String(attrs.controller)
			attrs.remove("controller")
		}

		List formats = ['csv', 'excel 97', 'excel (xlsx)', 'ods', 'pdf', 'rtf', 'xml']
		if(attrs?.formats){
			formats = new ArrayList(attrs.formats)
			attrs.remove("formats")
		}

		Map parameters = [:]
		if(attrs?.params){
			parameters = new HashMap(attrs.params)
			attrs.remove("params")
		}

		Map extensions = ['excel 97': "xls", 'excel (xlsx)': 'xlsx']

		builder."div"(attrs){
			formats.each { format ->
				span("class": "menuButton"){
					String message = ""

					try {
						message = g.message(code: 'default.' + format)
						if(message == 'default.' + format){
							message = format.toUpperCase()
						}
					}
					catch(Exception e) {
						message = format.toUpperCase()
					}

					// Extension defaults to format
					String extension = format
					if(extensions.containsKey(format)){
						extension = extensions[format]
					}

					Map params = [f: format, extension: extension] + parameters

					a('class': format, href: "${createLink(controller: controller, action: action, params: params)}", message)
				}
			}
		}

		writer.flush()
		out << writer.toString()
	}

	def resource = { attrs ->
		StringWriter writer = new StringWriter()
		def builder = new MarkupBuilder(writer)

		String resourcePath = RenderUtils.getResourcePath("export", request?.contextPath)

		if(!attrs.skin || attrs.skin == "default"){
			builder.link(rel: "stylesheet", type: "text/css", href: "${resourcePath}/css/export.css")
		}
		else {
			String applicationResourcePath = RenderUtils.getApplicationResourcePath(resourcePath)
			builder.link(rel: "stylesheet", type: "text/css", href: "${applicationResourcePath}/css/${attrs.skin}.css")
		}

		writer.flush()
		out << writer.toString()
	}

}
