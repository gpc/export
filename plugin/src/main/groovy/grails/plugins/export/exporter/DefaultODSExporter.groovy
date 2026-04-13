package grails.plugins.export.exporter

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

/**
 * @author Andreas Schmitt
 *
 */
class DefaultODSExporter  extends AbstractExporter {

	protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException {
		try {
			 SpreadsheetDocument spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument()

             Table table = spreadsheetDocument.getSheetByIndex(0)

			 // Enable/Disable header output
			 boolean isHeaderEnabled = true
			 if(getParameters().containsKey("header.enabled")){
				 isHeaderEnabled = getParameters().get("header.enabled")
			 }

			 // Create header
			 if(isHeaderEnabled){
				 //Header
				 fields.eachWithIndex { field, i ->
			     	String label = getLabel(field)

			        Cell cell = table.getCellByPosition(i,0)
			        cell.setStringValue(label)
				 }
			 }

		     //Rows
		     data.eachWithIndex { object, i ->
				fields.eachWithIndex { field, j ->
                    Cell cell = table.getCellByPosition(j,i+1)

                    cell.setStringValue(object[field]?.toString())
				}
		     }

			 spreadsheetDocument.save(outputStream)
		}
		catch(Exception e){
			throw new ExportingException("Error during export", e)
		}
	}
}
