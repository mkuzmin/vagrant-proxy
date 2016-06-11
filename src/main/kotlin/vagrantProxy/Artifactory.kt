package vagrantProxy

import org.jfrog.artifactory.client.ArtifactoryClient
import org.jfrog.artifactory.client.Artifactory
import org.jfrog.artifactory.client.RepositoryHandle
import org.jfrog.artifactory.client.model.File
import org.jfrog.artifactory.client.model.Folder

class ArtifactoryRepo {
    private val repo: RepositoryHandle

    constructor(serverUrl: String, repoName: String) {
        val artifactory = ArtifactoryClient().invokeMethod("create", serverUrl) as Artifactory
        repo = artifactory.repository(repoName)
    }

    fun box(name: String): Box {
        val versionListFolder = repo.folder(name).info<Folder>()
        val box = Box(
            name = name,
            versions = versions(versionListFolder)
        )
        return box
    }

    private fun versions(versionListFolder: Folder): List<Version> {
        val versions = mutableListOf<Version>()
        versionListFolder.children.forEach {
            if (it.isFolder) {
                val versionFolder = repo.folder(versionListFolder.path + it.uri).info<Folder>()
                val versionValue = it.uri.replace(Regex("^/"), "")
                versions.add(version(versionFolder, versionValue))
            }
        }
        return versions
    }

    private fun version(versionFolder: Folder, versionValue: String): Version {
        return Version (
            version = versionValue,
            providers = providers(versionFolder)
        )
    }

    private fun providers(versionFolder: Folder): List<Provider> {
        val providers = mutableListOf<Provider>()
        for (f in versionFolder.children) {
            if (f.isFolder) continue

            val match = Regex(".+-(.+?)\\.box").find(f.uri)
            if (match==null || match.groups.size!=2) continue
            val provider = match.groupValues[1]

            val boxFile = repo.file(versionFolder.path.replace(Regex("^/"), "") + f.uri).info<File>()
            providers.add(provider(boxFile, provider))
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
