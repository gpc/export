package grails.plugins.export

class TestController {
    def exportService

    def index() {
        def data = [[name: grailsApplication.config.get('info.app.name'), version: grailsApplication.config.get('info.app.version'), 'current date': new Date()]]
        String ext = params.extension ?: params.format
        if (ext in ['csv', 'xls', 'xlsx', 'ods', 'pdf', 'rtf', 'xml']) {
            response.setHeader("Content-disposition", "attachment; filename=test.${ext}")
            def parameters = [
                    'fileFormat': ext,
                    'dateFormat': 'yyyy-MM-dd HH:mm:SS',
                    'column.width.autoSize': true
            ]
            exportService.export(ext == 'xls' || ext == 'xlsx' ? 'excel' : ext, response.outputStream, data, [:], parameters)
            return
        }

        respond data
    }
}
