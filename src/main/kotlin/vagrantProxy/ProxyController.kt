package vagrantProxy

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.HEAD
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.ModelAndView

@RestController
class ProxyController @Autowired constructor (val config: Configuration, val repo: Repository) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @RequestMapping("/{org}/{box}", method = arrayOf(GET, HEAD))
    @ResponseBody
    fun index(@PathVariable org: String, @PathVariable box: String): Box {
        log.info("'$org/$box' box requested")
        if (org != config.organization) {
            log.warn("Invalid organization name '$org'")
            throw ResourceNotFoundException("This repository hosts boxes for '${config.organization}' organization only.")
        }

        return try {
            repo.box("$org/$box")
        } catch (e: BoxNotFoundException) {
            throw ResourceNotFoundException("Box '$org/$box' is not found on Artifactory server.")
        } catch (e: ArtifactoryErrorException) {
            throw BadGatewayException("Artifactory server is unavailable")
        }
    }

    @RequestMapping("/", method = arrayOf(GET))
    fun redirect () : ModelAndView {
        log.info("Home page requested")
        if (config.redirectUrl != "")
            return ModelAndView("redirect:${config.redirectUrl}")
        else
            throw ResourceNotFoundException("Page not found")
    }
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException (message: String): RuntimeException(message)

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
class BadGatewayException (message: String): RuntimeException(message)
