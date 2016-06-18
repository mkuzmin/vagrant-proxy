package vagrantProxy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.http.HttpStatus

@RestController
class ProxyController @Autowired constructor (val config: Configuration) {
    @RequestMapping("/{org}/{box}", method = arrayOf(GET))
    @ResponseBody
    fun index(@PathVariable org: String, @PathVariable box: String): Box {
// TODO: validate organization name
//        if (org != config.organization)
        val repo = ArtifactoryRepo(config.artifactoryUrl, config.repository)

        return try {
            repo.box("$org/$box")
        } catch (e: BoxNotFoundException) {
            throw ResourceNotFoundException()
        }
    }
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException : RuntimeException()

// TODO: redirect from home page
