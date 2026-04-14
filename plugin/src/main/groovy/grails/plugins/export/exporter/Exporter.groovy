package grails.plugins.export.exporter

/**
 * @author Andreas Schmitt
 * 
 */
interface Exporter {
	
	void setExportFields(List fields)
	List getExportFields()
	void setLabels(Map labels)
	Map getLabels()
	void setFormatters(Map formatters)
	Map getFormatters()
	void setParameters(Map parameters)
	Map getParameters()
	
	void export(OutputStream outputStream, List data) throws ExportingException
	
}