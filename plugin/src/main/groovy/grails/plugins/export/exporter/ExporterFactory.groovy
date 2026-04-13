package grails.plugins.export.exporter

/**
 * @author Andreas Schmitt
 * 
 */
interface ExporterFactory {
	
	Exporter createExporter(String type) throws ExporterNotFoundException
	Exporter createExporter(String type, List fields, Map labels, Map formatters, Map parameters) throws ExporterNotFoundException
	Exporter createExporter(String type, Object domain) throws ExporterNotFoundException
	Exporter createExporter(String type, Object domain, List fields, Map labels, Map formatters, Map parameters) throws ExporterNotFoundException

}