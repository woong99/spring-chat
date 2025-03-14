package potatowoong.moduleeureka.helper.dto

import org.json.JSONObject
import potatowoong.moduleeureka.helper.enums.DeployType

data class InfoDto(
    val instanceId: String,
    val app: String,
    val ip: String,
    val port: Int,
    val deployType: DeployType
) {
    companion object {
        fun of(
            jsonObject: JSONObject
        ) = InfoDto(
            instanceId = jsonObject.getString("instanceId"),
            app = jsonObject.getString("app"),
            ip = jsonObject.getString("ipAddr"),
            port = jsonObject.getJSONObject("port").getInt("$"),
            deployType = DeployType.valueOf(jsonObject.getString("instanceId").split(":").first().uppercase()),
        )
    }
}