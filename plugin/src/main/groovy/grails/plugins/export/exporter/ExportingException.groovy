package grails.plugins.export.exporter

/**
 * @author Andreas Schmitt
 * 
 */
class ExportingException extends Exception {

	public ExportingException(){
		super()
	}
	
	public ExportingException(String message){
		super(message)
	}
	
	public ExportingException(Throwable throwable){
		super(throwable)
	}
	
	public ExportingException(String message, Throwable throwable){
		super(message, throwable)
	}

}