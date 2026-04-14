package grails.plugins.export

import grails.plugins.export.exporter.Exporter
import grails.plugins.export.exporter.ExportingException
import grails.core.GrailsApplication

class ExportService {

    def exporterFactory
	GrailsApplication grailsApplication

    void export(String type, OutputStream outputStream, List objects, Map formatters, Map parameters) throws ExportingException {
    	export(type, outputStream, objects, null, null, formatters, parameters)
    }
    
    void export(String type, OutputStream outputStream, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
    	Exporter exporter = exporterFactory.createExporter(type, fields, labels, formatters, parameters)
    	exporter.export(outputStream, objects)
    }
   
    void export(String type, def response, String filename, String extension, List objects, Map formatters, Map parameters) throws ExportingException {
    	export(type, response, filename, extension, objects, null, null, formatters, parameters)
    }

    void export(String type, def response, String filename, String extension, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
    	// Setup response
    	response.contentType = grailsApplication.config.grails.mime.types[type]
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "." + extension + "\"")

    	Exporter exporter = exporterFactory.createExporter(type, fields, labels, formatters, parameters)    	
    	exporter.export(response.outputStream, objects)
    }    
}
