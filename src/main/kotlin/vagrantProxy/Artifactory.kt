package vagrantProxy

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.jfrog.artifactory.client.ArtifactoryClient
import org.jfrog.artifactory.client.RepositoryHandle
import org.jfrog.artifactory.client.model.File
import org.jfrog.artifactory.client.model.Folder
import groovyx.net.http.HttpResponseException
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import java.net.ConnectException

@Component
class Repository @Autowired constructor (val artifactory: Artifactory) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun box(name: String): Box {
        val versionListFolder = try {
            artifactory.boxInfo(name)
        } catch (e: HttpResponseException) {
            if (e.response.status == 404) {
                log.warn("Box '$name' not found")
                throw BoxNotFoundException()
            } else {
                log.error("Artifactory returned an error: [${e.response.status}] ${e.response.statusLine}")
                throw ArtifactoryErrorException()
            }
        } catch (e: ConnectException) {
            log.error("Artifactory connection error: ${e.message}")
            throw ArtifactoryErrorException()
        }
        val box = Box(
            name = name,
            versions = versions(versionListFolder)
        )
        return box
    }

    private fun versions(versionListFolder: Folder): List<Version> {
        val versions = mutableListOf<Version>()
        for (item in versionListFolder.children) {
            if (!item.isFolder) continue

            val versionFolder = artifactory.versionInfo(versionListFolder.path + item.uri)
            val versionNumber = item.uri.replace(Regex("^/"), "")
            versions.add(version(versionFolder, versionNumber))
        }
        return versions
    }

    private fun version(versionFolder: Folder, versionNumber: String): Version {
        return Version (
            version = versionNumber,
            providers = providers(versionFolder)
        )
    }

    private fun providers(versionFolder: Folder): List<Provider> {
        val providers = mutableListOf<Provider>()
        for (item in versionFolder.children) {
            if (item.isFolder) continue

            val matcher = Regex(".+-(.+?)\\.box").find(item.uri)
            if (matcher==null || matcher.groupValues.size!=2) continue
            val providerName = matcher.groupValues[1]

            val boxFile = artifactory.fileInfo(versionFolder.path.replace(Regex("^/"), "") + item.uri)
            providers.add(provider(boxFile, providerName))
        }
        return providers
    }

    private fun provider(boxFile: File, providerName: String): Provider {
        return Provider(
            name = providerName,
            url = boxFile.downloadUri,
            checksum_type = "sha1",
            checksum = boxFile.checksums.sha1
        )
    }
}

class BoxNotFoundException : Exception()
class ArtifactoryErrorException : Exception()

@Component
open class Artifactory @Autowired constructor (open val config: Configuration) {
    open protected val repo: RepositoryHandle
    init {
        val artifactory = ArtifactoryClient.create(config.artifactoryUrl)
        repo = artifactory.repository(config.repository)
    }
    open protected val log = LoggerFactory.getLogger(this.javaClass)


    @Throws(HttpResponseException::class, ConnectException::class)
    open fun boxInfo (path: String) : Folder {
        log.debug("Request box info in Artifactory for '$path'")
        return repo.folder(path).info<Folder>()
    }

    @Throws(HttpResponseException::class, ConnectException::class)
    @Cacheable("versions")
    open fun versionInfo (path: String) : Folder {
        log.debug("Request version info in Artifactory for '$path'")
        return repo.folder(path).info<Folder>()
    }

    @Throws(HttpResponseException::class, ConnectException::class)
    @Cacheable("files")
    open fun fileInfo (path: String) : File {
        log.debug("Request file info in Artifactory for '$path'")
        return repo.file(path).info<File>()
    }
}
