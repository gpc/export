package de.andreasschmitt.export.exporter

import groovy.transform.CompileStatic
import org.apache.commons.lang.time.FastDateFormat
import org.springframework.stereotype.Service

import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * @author Andreas Schmitt
 * @author sgirotti
 *
 */
@CompileStatic
@Service
class ExportConstant {
    public static final String TIME_FORMAT_PATTERN = "HH:mm:ss"
    public static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy"
    public static final String DATE_TIME_FORMAT_PATTERN = DATE_FORMAT_PATTERN + " " + TIME_FORMAT_PATTERN
    public static final FastDateFormat df = FastDateFormat.getInstance(DATE_FORMAT_PATTERN)
    public static final FastDateFormat tf = FastDateFormat.getInstance(TIME_FORMAT_PATTERN)
    public static final FastDateFormat dtf = FastDateFormat.getInstance(DATE_TIME_FORMAT_PATTERN)
    public static final String DECIMAL_FORMAT_PATTERN = '#,##0.00000'
    public static final DecimalFormat decf = new DecimalFormat(DECIMAL_FORMAT_PATTERN)
    public static final NumberFormat intf = NumberFormat.getIntegerInstance()

    static {
		intf.setGroupingUsed(false)
	}

    public static final Closure formatByType = { Object domain, Object object, String field ->
        switch (object) {
            case Date:
                return ExportConstant.df.format(object)
                break
            case BigDecimal:
                BigDecimal bd = (BigDecimal) object
                int bdScale = bd.scale()
                if(bdScale>0){
                    return ExportConstant.decf.format(object)
                }
                return ExportConstant.intf.format(object)
                break
            case Number:
                return ExportConstant.intf.format(object)
                break
            default:
                return object
                break
        }
    }
}