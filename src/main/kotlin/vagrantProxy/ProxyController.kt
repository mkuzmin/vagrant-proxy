package vagrantProxy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.HEAD
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.ModelAndView

@RestController
class ProxyController @Autowired constructor (val config: Configuration, val repo: Repository) {
    @RequestMapping("/{org}/{box}", method = arrayOf(GET, HEAD))
    @ResponseBody
    fun index(@PathVariable org: String, @PathVariable box: String): Box {
        if (org != config.organization)
            throw ResourceNotFoundException("This repository hosts boxes for '${config.organization}' organization only.")

        return try {
            repo.box("$org/$box")
        } catch (e: BoxNotFoundException) {
            throw ResourceNotFoundException("Box '$org/$box' is not found on Artifactory server.")
        }
    }

    @RequestMapping("/", method = arrayOf(GET))
    fun redirect () : ModelAndView {
        if (config.redirectUrl != "")
            return ModelAndView("redirect:${config.redirectUrl}")
        else
            throw ResourceNotFoundException("Page not found")
    }
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException (message: String): RuntimeException(message)
