package vagrantProxy

import org.springframework.web.bind.annotation.*

@RestController
class ProxyController {
    @RequestMapping("/org/windows")
    @ResponseBody
    fun index(): Box {
        val box = Box(
            name = "org/windows",
            versions = listOf(
                Version(
                    version = "0.1",
                    providers = listOf(
                        Provider(
                            name = "vsphere",
                            url = "http://localhost:8081/artifactory/vagrant/org/windows/0.1/windows-0.1-vsphere.box",
                            checksum_type = "sha1",
                            checksum = "208ae14e480d5c250e9169180be8d3ccc28b2442"
                        )
                    )
                )
            )
        )
        return box
    }
}
