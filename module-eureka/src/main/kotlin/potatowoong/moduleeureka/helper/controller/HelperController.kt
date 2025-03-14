package potatowoong.moduleeureka.helper.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import potatowoong.moduleeureka.helper.dto.InfoDto
import potatowoong.moduleeureka.helper.service.HelperService

@RestController
@RequestMapping("/helper")
class HelperController(
    private val helperService: HelperService
) {

    /**
     * Eureka 서버에 등록된 서버 정보 조회
     */
    @GetMapping("/info")
    fun getInfo(): ResponseEntity<List<InfoDto>> {
        return ResponseEntity.ok(helperService.getServerInfo())
    }
}