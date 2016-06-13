package vagrantProxy

import org.jfrog.artifactory.client.ArtifactoryClient
import org.jfrog.artifactory.client.RepositoryHandle
import org.jfrog.artifactory.client.model.File
import org.jfrog.artifactory.client.model.Folder

class ArtifactoryRepo (serverUrl: String, repoName: String) {
    private val repo: RepositoryHandle
    init {
        val artifactory = ArtifactoryClient.create(serverUrl)
        repo = artifactory.repository(repoName)
    }

    fun box(name: String): Box {
        val versionListFolder = repo.folder(name).info<Folder>()
        // TODO: validate 404
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

            val versionFolder = repo.folder(versionListFolder.path + item.uri).info<Folder>()
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

            val boxFile = repo.file(versionFolder.path.replace(Regex("^/"), "") + item.uri).info<File>()
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
