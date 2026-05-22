package grails.plugins.export.exporter

import groovy.transform.CompileStatic

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument
import org.odftoolkit.odfdom.doc.table.OdfTable

/**
 * Simple ODS exporter.
 *
 */
@CompileStatic
class DefaultODSExporter extends AbstractExporter {

    protected void exportData(OutputStream outputStream, List data, List<String> fields) throws ExportingException {
        try {
            OdfSpreadsheetDocument spreadsheetDocument = OdfSpreadsheetDocument.newSpreadsheetDocument()

            OdfTable table = spreadsheetDocument.getTableList(true).first

            // Enable/Disable header output
            boolean isHeaderEnabled = getParameters().getOrDefault("header.enabled", true)

            // Create header
            if (isHeaderEnabled) {
                //Header
                fields.eachWithIndex { field, i ->
                    String label = getLabel(field)
                    def cell = table.getCellByPosition(i, 0)
                    cell.setStringValue(label)
                }
            }

            //Rows
            data.eachWithIndex { object, i ->
                fields.eachWithIndex { field, j ->
                    def cell = table.getCellByPosition(j, i + 1)

                    cell.setStringValue(object[field] as String)
                }
            }

            spreadsheetDocument.save(outputStream)
        } catch (Exception e) {
            throw new ExportingException("Error during export", e)
        }
    }
}
