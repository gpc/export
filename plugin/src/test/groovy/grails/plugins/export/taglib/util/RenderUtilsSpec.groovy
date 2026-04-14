package grails.plugins.export.taglib.util

import spock.lang.Specification

class RenderUtilsSpec extends Specification {

    def "getUniqueId returns a non-null non-empty hex string"() {
        when:
        def id = RenderUtils.getUniqueId()

        then:
        id != null
        !id.isEmpty()
        id ==~ /[0-9a-f]+/
    }

    def "getUniqueId returns distinct values on successive calls"() {
        when:
        def id1 = RenderUtils.getUniqueId()
        def id2 = RenderUtils.getUniqueId()

        then:
        id1 != id2
    }

    def "getApplicationResourcePath extracts app base path from a plugin resource path"() {
        expect:
        RenderUtils.getApplicationResourcePath(input) == expected

        where:
        input                             | expected
        '/myapp/plugins/export-1.0'       | '/myapp'
        '/ctx/sub/plugins/export-2.0'     | '/ctx/sub'
        '/plugins/export-1.0'             | ''
    }

    def "getApplicationResourcePath returns null when path has no /plugins segment"() {
        when:
        def result = RenderUtils.getApplicationResourcePath('/someotherpath')

        then:
        result == null
    }

    def "getApplicationResourcePath returns empty string for null input"() {
        when:
        def result = RenderUtils.getApplicationResourcePath(null)

        then:
        result == ''
    }
}
