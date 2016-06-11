package vagrantProxy

import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET

@RestController
class ProxyController {
    @RequestMapping("/{org}/{box}", method = arrayOf(GET))
    @ResponseBody
    fun index(@PathVariable org: String, @PathVariable box: String): Box {
        val repo = ArtifactoryRepo("http://localhost:8081/artifactory/", "vagrant")
        return repo.box("$org/$box")
    }
}
