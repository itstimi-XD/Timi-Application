package com.love.timi.controller.v2

import com.love.timi.auth.AuthType
import com.love.timi.controller.common.BaseController
import com.love.timi.data.MenuTb
import com.love.timi.data.RoleChangeHistoryTb
import com.love.timi.data.UserTb
import com.love.timi.data.dto.request.*
import com.love.timi.data.dto.response.*
import com.love.timi.response.RestResponse
import com.love.timi.service.v2.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Tag(name = "AuthController V2", description = "권한 관련 컨트롤러입니다.")
@RestController("AuthControllerV2")
@RequestMapping("/user-auth/api/v2/auth")
class AuthController: BaseController() {

    @Autowired lateinit var authService: AuthService


//    /**
//     * role 추가(권한)
//     */
//    @Operation(summary ="role 추가(권한)", description = "role 추가(권한) endpoint")
//    @Parameter(name = "role", description = "추가할 role 이름", required = true)
//    @Parameter(name = "description", description = "설명", required = true)
//    @AuthType([AuthType.Authority.ROLE_MANAGEMENT_CREATE])
//    @RequestMapping(value = ["/add-role"], method = [RequestMethod.POST])
//    fun addRole(): ResponseEntity<RestResponse<HashMap<String, Any>>> {
//        return RestResponse<HashMap<String, Any>>().ok().setBody(authService.addRole(RoleTb().apply {
//            this.name = getParameter("role")
//            this.description = getParameterOrNull("description")
//            this.registeredAt = LocalDateTime.now(ZoneOffset.UTC)
//        })).responseEntity()
//    }

    /**
     * 회원 역할 변경
     */
    @Operation(summary ="회원 역할 변경", description = "회원 역할 변경 endpoint")
    @ApiResponse(responseCode = "200", description = "회원 역할 변경 성공")
    @AuthType([AuthType.Authority.ROLE_MANAGEMENT_UPDATE])
    @RequestMapping(value = ["/{targetUserId}/role"], method = [RequestMethod.PUT])
    fun changeUserRole(@PathVariable("targetUserId") targetUserId: Long, @Valid @RequestBody changeUserRoleReq: ChangeUserRoleReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(authService.changeUserRole(targetUserId, changeUserRoleReq)).responseEntity()}

    /**
     * 회원 역할 변경 이력
     */
    @Operation(summary ="회원 역할 변경 이력", description = "회원 역할 변경 이력 endpoint")
    @AuthType([AuthType.Authority.USER_ROLE_HISTORY_READ])
    @RequestMapping(value = ["/role-change-history"], method = [RequestMethod.GET])
    fun getRoleChangeHistory(@Valid @ParameterObject roleChangeHistoryReq: RoleChangeHistoryReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PageListRes<RoleChangeHistoryTb>>> {
        return RestResponse<PageListRes<RoleChangeHistoryTb>>().ok().setBody(authService.getRoleChangeHistory(roleChangeHistoryReq)).responseEntity()}

    /**
     * 역할별 메뉴 권한 리스트 [권한관리>권한목록 - 메뉴 변경]
     */
    @Operation(summary ="역할별 메뉴 권한 리스트 [권한관리 > 권한목록 - 메뉴 변경]", description = "역할별 메뉴 권한 리스트 endpoint")
    @AuthType([AuthType.Authority.ROLE_MANAGEMENT_READ])
    @RequestMapping(value = ["/get-role-menu"], method = [RequestMethod.GET])
    fun getRoleMenu(@Valid @ParameterObject req: ChangeMenuPermissionListReq, bindingResult: BindingResult): ResponseEntity<RestResponse<List<RoleMenuRes>>> {
        return RestResponse<List<RoleMenuRes>>().ok().setBody(authService.getRoleMenu(req)).responseEntity()
    }
    /**
     * 역할별 메뉴 권한 수정 [권한관리 > 권한목록 - 메뉴 변경]
     */
    @Operation(summary ="역할별 메뉴 권한 수정 [권한관리 > 권한목록 - 메뉴 변경]", description = "역할별 메뉴 권한 수정 endpoint")
    @AuthType([AuthType.Authority.ROLE_MANAGEMENT_UPDATE])
    @RequestMapping(value = ["/modify-role-menu"], method = [RequestMethod.PUT])
    fun modifyRoleMenu(@Valid @RequestBody permissionPerMenuReq: PermissionPerMenuReq, bindingResult: BindingResult): ResponseEntity<out Any> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(authService.modifyRoleMenu(permissionPerMenuReq)).responseEntity() }

    /**
     * 권한별 메뉴 트리 가져오기
     */
    @Operation(summary ="권한별 메뉴 트리 가져오기", description = "권한별 메뉴 트리 가져오기 endpoint")
    @RequestMapping(value = ["/get-menu-tree"], method = [RequestMethod.GET])
    @AuthType([AuthType.Authority.MAIN_MENU_READ])
    fun getMenuTree(): ResponseEntity<RestResponse<List<MenuTb>>> {
        return RestResponse<List<MenuTb>>().ok().setBody(authService.getMenuTree(getTokenData())).responseEntity()
    }

    /**
     * 메뉴 권한 가져오기
     */
    @Operation(summary ="메뉴 트리 가져오기", description = "메뉴 트리 가져오기 endpoint")
    @RequestMapping(value = ["/get-full-menu-tree"], method = [RequestMethod.GET])
    fun getFullMenuTree(): ResponseEntity<RestResponse<List<MenuTb>>> {
        return RestResponse<List<MenuTb>>().ok().setBody(authService.getFullMenuTree()).responseEntity()
    }

    /**
     * 역할별 메뉴당 권한 리스트 [권한 관리 > 권한 목록]
     */
    @Operation(summary ="역할별 메뉴당 권한 리스트 [권한 관리 > 권한 목록]", description = "역할별 메뉴당 권한 리스트 endpoint")
    @AuthType([AuthType.Authority.ROLE_MANAGEMENT_READ, AuthType.Authority.ROLE_MANAGEMENT_UPDATE])
    @RequestMapping(value = ["/role-menu-permission"], method = [RequestMethod.GET])
    fun getRoleMenuPermission(): ResponseEntity<RestResponse<RoleListRes>> {
        return RestResponse<RoleListRes>().ok().setBody(authService.getRoleMenuPermissionList()).responseEntity()
    }

    /**
     * 역할 변경 사용자 리스트 [권한관리 > 권한목록 - 권한 변경]
     */
    @Operation(summary ="역할 변경 사용자 리스트 [권한관리 > 권한목록 - 권한 변경]", description = "역할 변경 사용자 리스트 endpoint")
    @AuthType([AuthType.Authority.ROLE_MANAGEMENT_READ])
    @RequestMapping(value = ["/role-change-user-list"], method = [RequestMethod.GET])
    fun getRoleChangeUserList(@Valid @ParameterObject roleChangeUserListReq: RoleChangeUserListReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PageListRes<UserTb>>> {
        return RestResponse<PageListRes<UserTb>>().ok().setBody(authService.getRoleChangeUserList(roleChangeUserListReq)).responseEntity()
    }

    /**
     * 검토자 리스트 검색 by name [검색관리]
     */
    @Operation(summary ="검토자 리스트 검색 by name [검색관리]", description = "검토자 리스트 검색 by name [검색관리] endpoint")
    @AuthType([AuthType.Authority.SEARCH_MAP_READ])
    @RequestMapping(value = ["/reviewer-search"], method = [RequestMethod.GET])
    fun getReviewerSearch(@Valid @ParameterObject reviewerSearchReq: FindUserByNameReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PeopleByName>> {
        return RestResponse<PeopleByName>().ok().setBody(authService.getReviewerSearch(reviewerSearchReq)).responseEntity()
    }
    /**
     * 승인자 리스트 검색 by name [검색관리]
     */
    @Operation(summary ="승인자 리스트 검색 by name [검색관리]", description = "승인자 리스트 검색 by name [검색관리] endpoint")
    @AuthType([AuthType.Authority.SEARCH_MAP_READ])
    @RequestMapping(value = ["/manager-search"], method = [RequestMethod.GET])
    fun getManagerSearch(@Valid @ParameterObject reviewerSearchReq: FindUserByNameReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PeopleByName>> {
        return RestResponse<PeopleByName>().ok().setBody(authService.getManagerSearch(reviewerSearchReq)).responseEntity()
    }

    /**
     * IP 접근 제한 추가 [유저관리 > IP 접근관리 - IP 등록]
     */
    @Operation(summary ="IP 접근 제한 추가 [유저관리 > IP 접근관리 - IP 등록]", description = "IP 접근 제한 추가 endpoint")
    @AuthType([AuthType.Authority.USER_IP_MANAGEMENT_CREATE])
    @RequestMapping(value = ["/ip/add"], method = [RequestMethod.POST])
    fun addIp(@Valid @RequestBody ipAddReq: IpAddReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(authService.addIp(ipAddReq)).responseEntity()
    }

    /**
    * IP 접근 관리 목록 [유저관리 > IP 접근관리]
    */
    @Operation(summary ="IP 접근 관리 목록 [유저관리 > IP 접근관리]", description = "IP 접근 관리 목록 endpoint")
    @AuthType([AuthType.Authority.USER_IP_MANAGEMENT_READ])
    @RequestMapping(value = ["/ip/list"], method = [RequestMethod.GET])
    fun getIpList(@Valid @ParameterObject ipListReq: IpListReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PageListRes<UserIpForListRes>>> {
        return RestResponse<PageListRes<UserIpForListRes>>().ok().setBody(authService.getIpList(ipListReq)).responseEntity()
    }

    /**
     * IP 수정 [유저관리 > IP 접근관리]
     */
    @Operation(summary ="IP 수정 [유저관리 > IP 접근관리]", description = "IP 수정 endpoint")
    @AuthType([AuthType.Authority.USER_IP_MANAGEMENT_UPDATE])
    @RequestMapping(value = ["/ip/update"], method = [RequestMethod.PUT])
    fun updateIp(@Valid @RequestBody ipUpdateReq: IpUpdateReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(authService.updateIp(ipUpdateReq)).responseEntity()
    }

    /**
     * IP 삭제 [유저관리 > IP 접근관리]
     */
    @Operation(summary ="IP 삭제 [유저관리 > IP 접근관리]", description = "IP 삭제 endpoint")
    @AuthType([AuthType.Authority.USER_IP_MANAGEMENT_DELETE])
    @RequestMapping(value = ["/ip/delete"], method = [RequestMethod.DELETE])
    fun deleteIp(@Valid @ParameterObject ipDeleteReq: IpDeleteReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(authService.deleteIp(ipDeleteReq)).responseEntity()
    }
}