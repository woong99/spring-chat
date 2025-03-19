package potatowoong.moduleapi.api.friend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto
import potatowoong.domainrdb.domains.auth.enums.FriendshipStatusFilter
import potatowoong.moduleapi.api.friend.dto.FriendDto
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
        @RequestParam("filter") filter: FriendshipStatusFilter?
    ): ResponseEntity<ApiResponse<SearchFriendDto.Response>> {
        return ApiResponse.success(friendService.searchAllFriends(page, searchQuery, filter))
    }

    /**
     * 친구 상태 변경 API
     */
    @PutMapping("/status")
    fun changeFriendStatus(
        @RequestBody request: FriendDto.ChangeFriendStatusRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        friendService.changeFriendStatus(request)

        return ApiResponse.success()
    }
}