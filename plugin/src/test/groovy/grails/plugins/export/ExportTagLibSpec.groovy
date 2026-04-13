package grails.plugins.export

import grails.plugins.export.taglib.util.RenderUtils
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ExportTagLibSpec extends Specification implements TagLibUnitTest<ExportTagLib> {

    def setup() {
        webRequest.controllerName = 'test'
        webRequest.actionName = 'index'
    }

    // -------------------------------------------------------------------------
    // formats tag
    // -------------------------------------------------------------------------

    def "formats tag renders a div wrapper with class 'export' by default"() {
        when:
        def output = applyTemplate('<export:formats />')

        then:
        output.contains("class='export'")
    }

    def "formats tag uses a custom class attribute when provided"() {
        when:
        def output = applyTemplate('<export:formats class="my-export" />')

        then:
        output.contains("class='my-export'")
    }

    def "formats tag renders all 7 default formats as anchor elements"() {
        when:
        def output = applyTemplate('<export:formats />')

        then:
        output.count('<a ') == 7
        output.contains('f=csv')
        output.contains('f=pdf')
        output.contains('f=ods')
        output.contains('f=rtf')
        output.contains('f=xml')
    }

    def "formats tag renders only the specified formats"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['csv', 'pdf']])

        then:
        output.count('<a ') == 2
        output.contains('f=csv')
        output.contains('f=pdf')
        !output.contains('f=xml')
        !output.contains('f=ods')
    }

    def "formats tag maps excel 97 to xls extension"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['excel 97']])

        then:
        output.contains('extension=xls')
        !output.contains('extension=excel')
    }

    def "formats tag maps excel (xlsx) to xlsx extension"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['excel (xlsx)']])

        then:
        output.contains('extension=xlsx')
    }

    def "formats tag uses the format name as extension for non-mapped formats"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['csv', 'pdf', 'xml']])

        then:
        output.contains('extension=csv')
        output.contains('extension=pdf')
        output.contains('extension=xml')
    }

    def "formats tag wraps each link in a span with class menuButton"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['csv', 'pdf']])

        then:
        output.count("<span class='menuButton'>") == 2
    }

    def "formats tag sets each anchor's CSS class to the format name"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['csv', 'pdf']])

        then:
        output.contains("class='csv'")
        output.contains("class='pdf'")
    }

    def "formats tag uses uppercase format name as link text when no i18n message is found"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['csv', 'pdf']])

        then:
        output.contains('>CSV<')
        output.contains('>PDF<')
    }

    def "formats tag merges additional params into every generated link"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" params="${params}" />', [
            formats: ['csv', 'pdf'],
            params : [sort: 'name', order: 'asc']
        ])

        then:
        output.findAll('sort=name').size() == 2
        output.findAll('order=asc').size() == 2
    }

    def "formats tag does not emit action or controller as HTML attributes on the div"() {
        when:
        def output = applyTemplate('<export:formats action="export" controller="report" />')
        def openingDiv = output[0..output.indexOf('>')]

        then:
        !openingDiv.contains('action=')
        !openingDiv.contains('controller=')
        openingDiv.contains("class='export'")
    }

    def "formats tag uses the provided action when overriding the default"() {
        when:
        def output = applyTemplate('<export:formats formats="${formats}" action="export" />', [formats: ['csv']])

        then:
        output.count('<a ') == 1
    }

    def "formats tag uses webRequest controllerName when no controller attribute is provided"() {
        given:
        webRequest.controllerName = 'book'

        when:
        def output = applyTemplate('<export:formats formats="${formats}" />', [formats: ['csv']])

        then:
        output.count('<a ') == 1
    }

    // -------------------------------------------------------------------------
    // resource tag
    // -------------------------------------------------------------------------

    def "resource tag renders a stylesheet link pointing to the plugin export.css by default"() {
        given:
        request.contextPath = '/myapp'
        RenderUtils.metaClass.'static'.getResourcePath = { String name, String ctx ->
            "${ctx}/plugins/export-test"
        }

        when:
        def output = applyTemplate('<export:resource />')

        then:
        output.contains("rel='stylesheet'")
        output.contains("href='/myapp/plugins/export-test/css/export.css'")

        cleanup:
        RenderUtils.metaClass = null
    }

    def "resource tag with skin='default' renders the same stylesheet as no skin"() {
        given:
        request.contextPath = '/myapp'
        RenderUtils.metaClass.'static'.getResourcePath = { String name, String ctx ->
            "${ctx}/plugins/export-test"
        }

        when:
        def output = applyTemplate('<export:resource skin="default" />')

        then:
        output.contains("href='/myapp/plugins/export-test/css/export.css'")

        cleanup:
        RenderUtils.metaClass = null
    }

    def "resource tag with a named skin renders the application-level CSS link"() {
        given:
        request.contextPath = '/myapp'
        RenderUtils.metaClass.'static'.getResourcePath = { String name, String ctx ->
            "${ctx}/plugins/export-test"
        }

        when:
        def output = applyTemplate('<export:resource skin="mytheme" />')

        then:
        output.contains("rel='stylesheet'")
        output.contains('/css/mytheme.css')
        !output.contains('export.css')

        cleanup:
        RenderUtils.metaClass = null
    }
}
