package potatowoong.moduleeureka.helper.service

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import potatowoong.moduleeureka.helper.dto.InfoDto

@Service
class HelperService(
    @Value("\${eureka.helper-url}")
    private val helperUrl: String,
    private val restTemplate: RestTemplate
) {
    fun getServerInfo(): List<InfoDto> {
        // Eureka 서버에 등록된 서버 정보 조회
        val result = restTemplate.getForObject(helperUrl, String::class.java)

        // Json 파싱
        return parseJson(result)
    }

    private fun parseJson(result: String?): List<InfoDto> {
        val jsonObject = JSONObject(result)
        val root = jsonObject.getJSONObject("applications")
        val applications = root.getJSONArray("application")
        if (applications.isEmpty) {
            return emptyList()
        }

        val application = applications[0] as JSONObject
        val instances = application.getJSONArray("instance")
        if (instances.isEmpty) {
            return emptyList()
        }

        val infoDtoList = mutableListOf<InfoDto>()
        for (i in 0 until instances.length()) {
            val instance = instances.getJSONObject(i)
            if (instance.getString("status") != "UP") {
                continue
            }
            infoDtoList.add(InfoDto.of(instance))
        }

        return infoDtoList
    }
}