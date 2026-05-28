package contributor

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.yaml.YamlBuilder
import groovy.yaml.YamlSlurper

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ContributorTask extends DefaultTask {

    @Input
    abstract Property<String> getRepoOwner()

    @Input
    abstract Property<String> getRepoName()

    @Input
    @Optional
    abstract Property<String> getGithubToken()

    @OutputFile
    abstract RegularFileProperty getYamlOutput()

    @Internal
    Provider<Map<String, String>> getContributors() {
        yamlOutput.map { regularFile ->
            def yamlFile = regularFile.asFile
            if (!yamlFile.exists()) return Collections.<String, String>emptyMap()
            def yaml = new YamlSlurper().parse(yamlFile) as Map<String, Serializable>
            (yaml.contributors ?: [:]) as Map<String, String>
        }
    }

    @TaskAction
    void generate() {
        def token = githubToken.orNull
        def outputFile = yamlOutput.get().asFile
        outputFile.parentFile.mkdirs()

        if (!token) {
            logger.warn("No GITHUB_TOKEN — skipping contributor fetch for ${repoOwner.get()}/${repoName.get()}")
            YamlBuilder emptyYaml = new YamlBuilder()
            emptyYaml contributors: [:]
            outputFile.text = emptyYaml.toString()
            return
        }

        // 1. Fetch from REST (sorted by contributions, bots excluded)
        def restUrl = "https://api.github.com/repos/${repoOwner.get()}/${repoName.get()}/contributors"
        def contributors = new JsonSlurper().parseText(
                restUrl.toURL().getText(requestProperties: [Authorization: "token $token"])
        ).findAll { it.type != 'Bot' } as List<Map<String, Serializable>>

        def sortedNodes = contributors*.node_id
        def loginMap = contributors.collectEntries { [it.node_id, it.login] } as Map<String, String>

        // 2. Fetch display names via GraphQL
        def gqlQuery = 'query($ids: [ID!]!) { nodes(ids: $ids) { ... on User { id name login } } }'
        def body = JsonOutput.toJson([query: gqlQuery, variables: [ids: sortedNodes]])

        def connection = "https://api.github.com/graphql".toURL().openConnection() as HttpURLConnection
        connection.with {
            doOutput = true
            requestMethod = 'POST'
            setRequestProperty('Authorization', "bearer $token")
            outputStream.withWriter { it << body }
        }

        def gqlResponse = new JsonSlurper().parseText(connection.inputStream.text)
        def nameLookup = gqlResponse.data.nodes.collectEntries { [it.id, it.name ?: it.login] } as Map<String, String>

        // 3. Write YAML output (login → display name, preserving contribution order)
        def finalMap = sortedNodes
                .findAll { loginMap[it] }
                .collectEntries { id -> [loginMap[id], nameLookup[id] ?: loginMap[id]] } as Map<String, String>

        YamlBuilder yaml = new YamlBuilder()
        yaml contributors: finalMap
        outputFile.text = yaml.toString()
    }
}
