package io.mkuzmin.vagrant_proxy

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull

@ConfigurationProperties
@Component
class Configuration {
    // TODO: validate url
    @NotNull
    lateinit var artifactoryUrl: String

    @NotNull
    lateinit var repository: String

    @NotNull
    lateinit var organization: String

    var redirectUrl = ""

    // TODO: better error messages for missing properties
}
