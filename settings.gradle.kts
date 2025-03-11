rootProject.name = "spring-chat-backend"

include("module-api")
include("module-domain")
include("module-websocket")
include("module-sse")
include("module-security")
include("module-common")
include("module-domain:domain-rdb")
include("module-domain:domain-mongo")
include("module-domain:domain-redis")
include("module-eureka")
