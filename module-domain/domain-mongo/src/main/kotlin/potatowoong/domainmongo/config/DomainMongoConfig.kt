package potatowoong.domainmongo.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["potatowoong.domainmongo"])
class DomainMongoConfig {
}