package vagrantProxy

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties
@Component
class Configuration (
    var artifactoryUrl: String = "",
    var repository: String = "",
    var organization: String = ""
)
