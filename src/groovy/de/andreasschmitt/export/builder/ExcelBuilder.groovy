package de.andreasschmitt.export.builder

import groovy.util.BuilderSupport
import jxl.CellView
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.CellFormat
import jxl.format.Colour
import jxl.format.Pattern
import jxl.write.WritableFont
import jxl.Workbook
import jxl.write.Label
import jxl.write.Number
import jxl.write.DateTime
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.WriteException
import jxl.write.biff.RowsExceededException
import jxl.write.WritableFont
import jxl.format.UnderlineStyle
import jxl.format.UnderlineStyle
import jxl.write.WritableCellFormat
import jxl.write.biff.CellValue
import jxl.write.WritableFont
import jxl.write.WritableHyperlink

import org.apache.commons.logging.*

/**
 * @author Andreas Schmitt
 * 
 * This class implements a Groovy builder for creating Excel files. It uses 
 * JExcelApi under the hood to build the actual Excel files. The builder supports 
 * basic formatting and multiple sheets. Formulas and more advanced features are 
 * not supported yet.
 * 
 * The following code snippet shows how to create Excel files using this builder:
 * 
 * def builder = new ExcelBuilder()
 * 
 * builder {
 *     workbook(outputStream: outputStream){
 *     
 *         sheet(name: "Sheet1"){
 *     	       format(name: "format1"){
 *                 font(name: "Arial", size: 10, bold: true, underline: "single", italic: true)    
 *     	       }
 *         
 *             cell(row: 0, column: 0, value: "Hello1")
 *             cell(row: 0, column: 1, value: "Hello2")
 *         }
 *     
 *         sheet(name: "Sheet2"){
 *         
 *         }
 *     }
 * }
 * 
 * builder.write()
 * 
 */

class ExcelBuilder extends BuilderSupport {

	WritableWorkbook workbook
	WritableSheet sheet
	
	String format
	Map formats = [:]

	private static Log log = LogFactory.getLog(ExcelBuilder)

	/**
    * This method isn't implemented.
    */
    protected void setParent(Object parent, Object child) {
    }

	/**
	 * This method is invoked when you invoke a method without arguments on the builder 
	 * e.g. builder.write().
	 * 
	 * @param name The name of the method which should be invoked e.g. write.
	 * 
	 */
    protected Object createNode(Object name) {
    	log.debug("createNode(Object name)")
    	log.debug("name: ${name}")
    	
    	if(name == "write"){
    		this.write()
    	}
    	
        return null
    }

    /**
     * This method isn't implemented.
     */
    protected Object createNode(Object name, Object value) {
    	log.debug("createNode(Object name, Object value)")
    	log.debug("name: ${name} value: ${value}")
        return null
    }

    /**
     * This method is invoked when you invoke a method with a map of attributes e.g. 
     * cell(row: 0, column: 0, value: "Hello1"). It switches between different build 
     * actions such as creating the workbook, sheet, cells etc.
     * 
     * @param name The name of the method which should be invoked e.g. cell
     * @param attributes The map of attributes which have been supplied e.g. [row: 0, column: 0, value: "Hello1"]
     * 
     */
    protected Object createNode(Object name, Map attributes) {
    	log.debug("createNode(Object name, Map attributes)")
    	log.debug("name: ${name} attributes: ${attributes}")
    	
    	switch(name){
    		// Workbook, the Excel document as such
    		case "workbook":
    			if(attributes?.outputStream){
    		    	try {
    		    		log.debug("Creating workbook")
    		        	workbook = Workbook.createWorkbook(attributes?.outputStream)	
    		    	}
    		    	catch(Exception e){
    		    		log.error("Error creating workbook", e)
    		    	}
    			}
    			break
    		
    		// Sheet, an Excel file can contain multiple sheets which are typically shown as tabs
    		case "sheet":
    			try {
        			log.debug("Creating sheet")
        			sheet = workbook.createSheet(attributes?.name, workbook.getNumberOfSheets())	
					if (attributes?.widths && !attributes?.widths?.isEmpty()) {
						attributes.widths.eachWithIndex { width, i ->
							sheet.setColumnView(i, (width < 1.0 ? width * 100 : width) as int )
						}
					} else {
                        if(attributes?.widthAutoSize){
                            for(int i = 0; i < attributes.numberOfFields; i++){
                                sheet.setColumnView(i, new CellView(autosize: true))
                            }
                        }
                    }
    			}
    			catch(Exception e){
    				log.error("Error creating sheet", e)
    			}
    			break
    			
    		// Cell, column header or row cells
    		case "cell":
    			try {
    				CellValue value
        			if(attributes?.value instanceof java.lang.Number){
        				log.debug("Creating number cell")
        				value = new Number(attributes?.column, attributes?.row, attributes?.value)
        			} else if(attributes?.value instanceof Date){
                        log.debug("Creating date cell")
                        value = new DateTime(attributes?.column, attributes?.row, attributes?.value)
                    }
        			else {
        				log.debug("Creating label cell")
        				value = new Label(attributes?.column, attributes?.row, attributes?.value?.toString())
        			}

    				if(attributes?.format && formats.containsKey(attributes?.format)){
    					value.setCellFormat(formats[attributes.format])
    				}
					
					// Create hyperlinks for values beginning with http
					if (attributes?.value?.toString()?.toLowerCase()?.startsWith('http://') || attributes?.value?.toString()?.toLowerCase()?.startsWith('https://')) {
        				log.debug("Changing cell to Hyperlink")
        				def link = new WritableHyperlink(attributes?.column, attributes?.row, new URL(attributes?.value?.toString()))
						link.setDescription(attributes?.value?.toString() ?: 'no URL')
						sheet.addHyperlink(link);
					}
					else {
						sheet.addCell(value)
					}
    			}
    			catch(Exception e){
    				log.error("Error adding cell with attributes: ${attributes}", e)
    			}
    			break
    			
    		case "format":
    			if(attributes?.name){
    				format = attributes?.name
    			}
    			break
    		
    		case "font":
    			try {
    				log.debug("attributes: ${attributes}")
    				
        			attributes.name = attributes?.name ? attributes?.name : "arial"
        	    	attributes.italic = attributes?.italic ? attributes?.italic : false
        	    	attributes.bold = attributes?.bold ? attributes?.bold : "false"
        	    	attributes["size"] = attributes["size"] ? attributes["size"] : WritableFont.DEFAULT_POINT_SIZE
        	    	attributes.underline = attributes?.underline ? attributes?.underline : "none"
                    //attributes.backColor = attributes?.backColor ? attributes?.backColor : Colour.WHITE
                    attributes.foreColor = attributes?.foreColor ? attributes?.foreColor : Colour.BLACK
                    attributes.useBorder = attributes?.useBorder ? attributes?.useBorder : false
        	    			
        	    	Map bold = ["true": WritableFont.BOLD, "false": WritableFont.NO_BOLD]		
        	    	if(bold.containsKey(attributes.bold.toString())){
        	    		attributes.bold = bold[attributes?.bold.toString()]	
        	    	}
        	    			
        	    	Map underline = ["none": UnderlineStyle.NO_UNDERLINE, "double accounting": UnderlineStyle.DOUBLE_ACCOUNTING,
        	    			         "single": UnderlineStyle.SINGLE, "single accounting": UnderlineStyle.SINGLE_ACCOUNTING]
        	    	if(underline.containsKey(attributes.underline)){
        	    		attributes.underline = underline[attributes.underline]
        	    	}
        	    	
        	    	Map fontname = ["arial":  WritableFont.ARIAL, "courier": WritableFont.COURIER, 
        	    	            "tahoma":  WritableFont.TAHOMA, "times":  WritableFont.TIMES]
        	    	if(fontname.containsKey(attributes.name)){
        	    		attributes.name = fontname[attributes.name]
        	    	}
        	    	
        	    	log.debug("attributes: ${attributes}")
        	    			
                    WritableFont font = new WritableFont(attributes.name, attributes["size"], attributes.bold, attributes.italic, attributes.underline)
                    font.colour = attributes.foreColor
                    WritableCellFormat cellFormat = new WritableCellFormat(font)

                    if (attributes.useBorder) cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN)
                    if (attributes.backColor) cellFormat.setBackground(attributes.backColor)
                    if (attributes.alignment) cellFormat.setAlignment(attributes.alignment)

                    formats.put(format, cellFormat)
    			}
    			catch(Exception e){
    				println "Error!"
                    e.printStackTrace();
    			}
    			break
            case "mergeCells":
                log.debug("attributes: ${attributes}")
                try {
                    sheet.mergeCells(attributes?.startColumn, attributes?.startRow, attributes?.endColumn, attributes?.endRow)
                } catch (Exception ex) {
                    log.error("Could not merge cells.  Ensure startColumn, startRow, endColumn, endRow attributes are set.  Attributes: ${attributes}")
                }
                break
    	}
    	
        return null
    }

    /**
     * This method isn't implemented.
     */
    protected Object createNode(Object name, Map attributes, Object value) {
    	log.debug("createNode(Object name, Map attributes, Object value)")
    	log.debug("name: ${name} attributes: ${attributes}, value: ${value}")
        return null
    }
    
    /**
     * Finish writing the document.
     */
    public void write(){
    	log.debug("Writing document")
    	
    	try {
        	workbook.write()
    		workbook.close()	
    	}
    	catch(Exception e){
    		log.error("Error writing document", e)
    	}
    }
    
}