if(System.getenv('TRAVIS_BRANCH')) {
    grails.project.repos.grailsCentral.username = System.getenv("GRAILS_CENTRAL_USERNAME")
    grails.project.repos.grailsCentral.password = System.getenv("GRAILS_CENTRAL_PASSWORD")
}

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolver="maven"
grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenCentral()
        mavenRepo "http://repo.grails.org/grails/core"
    }

    dependencies {
        compile 'net.sf.opencsv:opencsv:2.3'

        compile 'com.lowagie:itext:2.1.7'
        compile("com.lowagie:itext-rtf:2.1.7")
        runtime 'xerces:xercesImpl:2.9.0'
        compile 'org.odftoolkit:odfdom-java:0.8.7'
        compile 'net.sourceforge.jexcelapi:jxl:2.6.12'
        compile 'commons-beanutils:commons-beanutils:1.8.3'
    }

    plugins {
        build(":tomcat:7.0.52.1",
                ":release:3.0.1") {
            export = false
        }
    }
}
