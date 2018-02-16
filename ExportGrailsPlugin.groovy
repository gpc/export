import org.apache.commons.logging.LogFactory

class ExportGrailsPlugin {
    // the plugin version
    def version = "1.6.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    
    def title = "Export Plugin" // Headline display name of the plugin

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/export"

    def author = "Grails Plugin Collective"
    def authorEmail = "grails.plugin.collective@gmail.com"
    def description = '''\
This plugin offers export functionality supporting different formats e.g. CSV, Excel, Open Document Spreadsheet, PDF and XML 
and can be extended to add additional formats. 
'''
    def license = 'APACHE'
    def organization = [name: 'Grails Plugin Collective', url: 'http://github.com/gpc']
    def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPEXPORT']
    def scm = [url: 'https://github.com/gpc/grails-export']



    def doWithSpring = {        
		//This is only necessary here, because later on log is injected by Spring
		def log = LogFactory.getLog(ExportGrailsPlugin)
		 
		"exporterFactory"(de.andreasschmitt.export.exporter.DefaultExporterFactory)
		
		try {				
			ExportConfig.exporters.each { key, value ->
		  		try {
		  			//Override default renderer configuration
					if(application.config?.export."${key}"){
						value = application.config.export."${key}"
					}
		  			
		      		Class clazz = Class.forName(value, true, new GroovyClassLoader())
		      		
		      		//Add to spring
		      		"$key"(clazz) { bean ->
						  bean.scope = "prototype"
					}	
		  		}
		  		catch(ClassNotFoundException e){
		  			log.error("Couldn't find class: ${value}", e)
		  		}
			}			
		}
		catch(Exception e){
			log.error("Error initializing Export plugin", e)
		}
		catch(Error e){
			//Strange error which happens when using generate-all and hibernate.cfg
			log.error("Error initializing Export plugin")
		}
    }

}
