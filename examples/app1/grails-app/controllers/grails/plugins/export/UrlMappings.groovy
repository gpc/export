package grails.plugins.export

class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{}
        "/"(redirect:'/test')
    }
}
