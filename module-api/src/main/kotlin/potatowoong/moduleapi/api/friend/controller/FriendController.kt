package potatowoong.moduleapi.api.friend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto
import potatowoong.moduleapi.api.friend.service.FriendService
import potatowoong.moduleapi.common.api.ApiResponse

@RestController
@RequestMapping("/api/v1/friends")
class FriendController(
    private val friendService: FriendService
) {

    /**
     * 전체 친구 목록 조회 API
     */
    @GetMapping
    fun getFriends(
        @RequestParam("page", defaultValue = "0") page: Long,
        @RequestParam("searchQuery") searchQuery: String?,
    ): ResponseEntity<ApiResponse<SearchFriendDto.Response>> {
        return ApiResponse.success(friendService.searchAllFriends(page, searchQuery))
    }
}