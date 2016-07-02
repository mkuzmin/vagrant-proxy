package vagrantProxy

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

@SpringBootApplication
@EnableCaching
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

@Component
open class Startup @Autowired constructor (val config: Configuration): CommandLineRunner {
    private val log = LoggerFactory.getLogger(this.javaClass)
    @Value("\${info.build.version}")
    lateinit var buildVersion: String

    override fun run(vararg args: String) {
        log.info("Vagrant proxy (version ${buildVersion})")
    }
}
