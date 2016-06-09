package vagrantProxy

import org.springframework.web.bind.annotation.*

@RestController
class ProxyController {
    @RequestMapping("/")
    @ResponseBody
    fun index(): String {
        return "Hello"
    }
}
