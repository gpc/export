package de.andreasschmitt.export.exporter

import de.andreasschmitt.export.builder.ExcelBuilder
import jxl.format.Alignment
import jxl.format.Colour

/**
 * @author Andreas Schmitt
 *
 */
class DefaultExcelExporter extends AbstractExporter {

    protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException{
        try {
            def builder = new ExcelBuilder()

            // Enable/Disable header output
            boolean isHeaderEnabled = true
            if(getParameters().containsKey("header.enabled")){
                isHeaderEnabled = getParameters().get("header.enabled")
            }

            boolean useZebraStyle = false
            if(getParameters().containsKey("zebraStyle.enabled")){
                useZebraStyle = getParameters().get("zebraStyle.enabled")
            }

            builder {
                workbook(outputStream: outputStream){
                    sheet(name: getParameters().get("title") ?: "Export", widths: getParameters().get("column.widths"), numberOfFields: fields.size(), widthAutoSize: getParameters().get("column.width.autoSize")) {

                        format(name: "title") {
                            Alignment alignment = Alignment.GENERAL
                            if (getParameters().containsKey('titles.alignment')) {
                                alignment = Alignment."${getParameters().get('titles.alignment')}"
                            }
                            font(name: "arial", bold: true, size: 14, alignment: alignment)
                        }

                        format(name: "header") {
                            if (useZebraStyle) {
                                font(name: "arial", bold: true, backColor: Colour.GRAY_80, foreColor: Colour.WHITE, useBorder: true)
                            } else {
                                // Use default header format
                                font(name: "arial", bold: true)
                            }
                        }
                        format(name: "odd") {
                            font(backColor: Colour.GRAY_25, useBorder: true)
                        }
                        format(name: "even") {
                            font(backColor: Colour.WHITE, useBorder: true)
                        }

                        int rowIndex = 0

                        // Option for titles on top of data table
                        def titles = getParameters().get("titles")
                        titles.each {
                            cell(row: rowIndex, column: 0, value: it, format: "title")
                            rowIndex++
                        }

                        //Create header
                        if (isHeaderEnabled) {
                            fields.eachWithIndex { field, index ->
                                String value = getLabel(field)
                                cell(row: rowIndex, column: index, value: value, format: "header")
                            }
                            rowIndex++
                        }

                        //Rows
                        data.eachWithIndex { object, k ->
                            String format = useZebraStyle ? ((k % 2) == 0 ? "even" : "odd") : ""
                            fields.eachWithIndex { field, i ->
                                Object value = getValue(object, field)
                                cell(row: k + rowIndex, column: i, value: value, format: format)
                            }
                        }

                        if (getParameters().get('titles.mergeCells')) {
                            //Merge title cells
                            titles.eachWithIndex { title, index ->
                                mergeCells(startColumn: 0, startRow: index, endColumn: fields.size(), endRow: index)
                            }
                        }
                    }
                }
            }

            builder.write()
        }
        catch(Exception e){
            throw new ExportingException("Error during export", e)
        }
    }

}