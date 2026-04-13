package grails.plugins.export.exporter

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * @author Andreas Schmitt
 * 
 */
class DefaultExporterFactory implements ExporterFactory, ApplicationContextAware {

	ApplicationContext context
	
	public Exporter createExporter(String type) throws ExporterNotFoundException {
		return createExporter(type, null, null, null, null, null)
	}
	
	public Exporter createExporter(String type, Object domain) throws ExporterNotFoundException {
		return createExporter(type, domain, null, null, null, null)
	}
	
	public Exporter createExporter(String type, List fields, Map labels, Map formatters, Map parameters) throws ExporterNotFoundException {
		return createExporter(type, null, fields, labels, formatters, parameters)
	}
	
	public synchronized Exporter createExporter(String type, Object domain, List fields, Map labels, Map formatters, Map parameters) throws ExporterNotFoundException {
		try {
			Exporter exporter = null
			
			if(domain){
				exporter = context?.getBean("${type.toLowerCase()}${domain?.simpleName}Exporter")
			}
			else {
				exporter = context?.getBean("${type.toLowerCase()}Exporter")
			}
			
			if(fields){
				exporter.setExportFields(fields)
			}
			
			if(labels){
				exporter.setLabels(labels)
			}
			
			if(formatters){
				exporter.setFormatters(formatters)
			}
			
			if(parameters){
				exporter.setParameters(parameters)
			}
			
			return exporter
		}
		catch(Exception e){
			throw new ExporterNotFoundException("No exporter found for type: ${type}", e)
		}
	}
	
	public void setApplicationContext(ApplicationContext context){
		this.context = context
	}
	
}