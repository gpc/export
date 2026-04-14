<%@ page import="grails.util.Environment"%>
<%@ page import="org.springframework.boot.SpringBootVersion"%>
<%@ page import="org.springframework.core.SpringVersion"%>
<g:set var="pluginManager" bean="pluginManager"/>
<g:set var="servletContext" bean="servletContext"/>
<g:set var="pluginsWithOrder"
       value="${pluginManager.allPlugins.toList()
               .withIndex()
               .collect { p, i -> [plugin: p, order: i + 1] }
               .sort { a, b -> a.plugin.name.toLowerCase() <=> b.plugin.name.toLowerCase() }}"
/>
<g:set var="numControllers" value="${grailsApplication.controllerClasses.size()}"/>
<!doctype html>
<html>
<head>
    <title>Test Controller</title>
    <meta name="layout" content="main"/>
    <asset:stylesheet src="welcome.css"/>
</head>

<body>
<main id="content" role="main" class="pb-4 pb-md-5">
    <div class="container-lg py-2 py-md-3">
        <div class="row align-items-top g-4">

            <%-- WELCOME MESSAGE --%>
            <div class="col-12 col-md-7">
                <h1 class="display-6 fw-semibold mb-2">Welcome to Grails Export Plugin</h1>
                <p class="lead text-body-secondary">
                    Find below sample output of the different supported output formats
                </p>
                <export:formats formats="['csv', 'excel 97', 'excel (xlsx)', 'ods', 'pdf', 'rtf', 'xml']" />
            </div>

            <%-- RUNTIME VERSIONS --%>
            <div class="col-12 col-md-5">
                <div class="card border-1 shadow-sm">
                    <div class="card-body">
                        <h6 class="card-title mb-3 fw-semibold">Runtime versions</h6>
                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="d-inline-flex align-items-center text-body-secondary">
                                    <asset:image src="grails.svg" alt="Grails" width="18" height="18" class="me-2"/>
                                    Grails
                                </span>
                                <g:meta name="info.app.grailsVersion"/>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="d-inline-flex align-items-center text-body-secondary">
                                    <asset:image src="spring-boot.svg" alt="Spring Boot" width="18" height="18" class="me-2"/>
                                    Spring Boot
                                </span>
                                ${SpringBootVersion.getVersion()}
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="d-inline-flex align-items-center text-body-secondary">
                                    <asset:image src="spring.svg" alt="Spring" width="18" height="18" class="me-2"/>
                                    Spring
                                </span>
                                ${SpringVersion.getVersion()}
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="d-inline-flex align-items-center text-body-secondary">
                                    <asset:image src="groovy.svg" alt="Groovy" width="18" height="18" class="me-2"/>
                                    Groovy
                                </span>
                                ${GroovySystem.getVersion()}
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="d-inline-flex align-items-center text-body-secondary">
                                    <asset:image src="java.svg" alt="Java" width="18" height="18" class="me-2"/>
                                    JVM (${System.getProperty('java.vendor')})
                                </span>
                                ${System.getProperty('java.version')}
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="container-lg">
        <div class="row g-4 align-items-stretch">

            <%-- APPLICATION INFO --%>
            <div class="col-12 col-lg-4">
                <div class="card border-1 shadow-sm h-100">
                    <div class="card-body">
                        <div class="d-flex align-items-center justify-content-between mb-3">
                            <h6 class="card-title mb-0 fw-semibold">Application</h6>
                            <g:if test="${Environment.reloadingAgentEnabled}">
                                <span class="reload-indicator text-success" role="status" aria-label="Reloading active">
                                    <span class="reload-dot ping" aria-hidden="true"></span>
                                    <span class="text-body-secondary">Reloading active</span>
                                </span>
                            </g:if>
                            <g:else>
                                <span class="reload-indicator text-danger" role="status" aria-label="Reloading inactive">
                                    <span class="reload-dot" aria-hidden="true"></span>
                                    <span class="text-body-secondary">Reloading inactive</span>
                                </span>
                            </g:else>
                        </div>
                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Name</span>
                                <span class="fw-medium text-truncate ms-3"><g:meta name="info.app.name"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Version</span>
                                <span class="fw-medium" style="font-variant-numeric: tabular-nums;">
                                    <g:meta name="info.app.version"/>
                                </span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Profile</span>
                                <span class="fw-medium text-truncate ms-3">
                                    ${grailsApplication.config.getProperty('grails.profile')}
                                </span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Environment</span>
                                <span class="fw-medium">${Environment.current.name}</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <%-- SERVER INFO --%>
            <div class="col-12 col-lg-4">
                <div class="card border-1 shadow-sm h-100">
                    <div class="card-body">
                        <div class="d-flex align-items-center justify-content-between mb-3">
                            <h6 class="card-title mb-0 fw-semibold">Server</h6>
                        </div>
                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Servlet Container</span>
                                <span class="fw-medium text-truncate ms-3">${servletContext.serverInfo}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Host</span>
                                <span class="fw-medium text-truncate ms-3">${InetAddress.localHost}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">OS</span>
                                <span class="fw-medium text-truncate ms-3">
                                    ${System.getProperty('os.name')} ${System.getProperty('os.version')} (${System.getProperty('os.arch')})
                                </span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <%-- ARTEFACT COUNTS --%>
            <div class="col-12 col-lg-4">
                <div class="card border-1 shadow-sm h-100">
                    <div class="card-body">
                        <div class="d-flex align-items-center justify-content-between mb-3">
                            <h6 class="card-title mb-0 fw-semibold">Artefact counts</h6>
                        </div>

                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Controllers</span>
                                <span class="fw-medium">${numControllers}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Domains</span>
                                <span class="fw-medium">${grailsApplication.domainClasses.size()}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Services</span>
                                <span class="fw-medium">${grailsApplication.serviceClasses.size()}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <span class="text-body-secondary">Tag Libraries</span>
                                <span class="fw-medium">${grailsApplication.tagLibClasses.size()}</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

        </div>
    </div>
</main>
<asset:javascript src="welcome.js"/>
</body>
</html>