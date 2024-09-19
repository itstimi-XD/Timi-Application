package com.love.timi.service.v2

import com.love.timi.data.*
import com.love.timi.data.dto.request.*
import com.love.timi.data.dto.response.*
import com.love.timi.data.predicate.IpTbPredicate
import com.love.timi.data.predicate.RoleChangeHistoryTbPredicate
import com.love.timi.data.predicate.UserTbPredicate
import com.love.timi.data.repo.*
import com.love.timi.enumTypes.UserStatus
import com.love.timi.exception.ErrorMessage
import com.love.timi.service.common.BaseService
import com.love.timi.util.CommonUtil.isIpValid
import com.love.timi.util.MaskingUtil
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

@Service("AuthServiceV2")
class AuthService: BaseService() {

    @Autowired lateinit var redis: RedisTemplate<String, String>

//    fun addRole(roleTb: RoleTb): HashMap<String, Any> {
//        if (roleTbRepo.findOne(roleTb) != null) {
//            throw ErrorMessage.ROLE_ALREADY_EXISTS.exception
//        }
//        roleTbRepo.save(roleTb)
//        return HashMap<String, Any>().apply {
//            this["result"] = "Success"
//        }
//    }

    /**
     * 회원 역할 변경
     * @param targetUserId
     * @param changeUserRoleReq
     * @return HashMap
     */
    @Transactional
    fun changeUserRole(targetUserId: Long, changeUserRoleReq: ChangeUserRoleReq): HashMap<String, Any> {
        // 변경을 원하는 역할Id값이 존재하는 역할인지 확인
        val roleId = changeUserRoleReq.roleId
        if (roleId != null) {
            if (!roleTbRepo.existsById(roleId.toInt())) {
                throw ErrorMessage.ROLE_NOT_FOUND.exception
            }
        }
        userTbRepo.findOne(UserTb().apply { this.userId = targetUserId })?.copy()
            ?.let {
                val beforeRoleId = it.roleId
                if(beforeRoleId?.toInt() == 29 && roleId?.toInt() != 29) {
                    ipTbRepo.deleteByUserId(it.userId!!)
                }
                roleChangeHistoryTbRepo.save(RoleChangeHistoryTb().apply {
                    this.userId = it.userId
                    this.afterRoleId = roleId
                    this.email = it.email
                    this.name = it.name
                    this.changedAt = LocalDateTime.now(ZoneOffset.UTC)
                    this.changeHistory = "Role changed from \'${getRoleById(beforeRoleId!!.toInt()).name}\' to \'${getRoleById(roleId!!.toInt()).name}\'"
                })
                it.roleId = changeUserRoleReq.roleId
                userTbRepo.save(it)
            } ?: throw ErrorMessage.USER_NOT_FOUND.exception
        
        // 역할 바뀐 사용자 로그아웃
        redis.opsForValue().set("GlobalSearch-Token-Serial.{$targetUserId}", "RoleMenuPermissionChanged")
        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }

    /**
     * 회원 역할 변경 이력
     * @param roleChangeHistoryReq
     * @return HashMap
     */
    fun getRoleChangeHistory(roleChangeHistoryReq: RoleChangeHistoryReq): PageListRes<RoleChangeHistoryTb> {
        val page = roleChangeHistoryReq.page?: 1

        val afterRoleId = if(roleChangeHistoryReq.roleId?.toLong() == 0L) null else roleChangeHistoryReq.roleId?.toLong()
        val roleChangeHistoryTb = RoleChangeHistoryTb().apply {
            this.email = roleChangeHistoryReq.email.takeIf { it?.isNotBlank() == true }
            this.name = roleChangeHistoryReq.name.takeIf { it?.isNotBlank() == true }
            this.afterRoleId = afterRoleId
            this.startDate = roleChangeHistoryReq.startDate?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDateTime.parse(it + "T00:00:00")
                } catch (e: DateTimeParseException) {
                    null // 유효하지 않은 날짜인 경우 null 처리
                }
            }
            this.endDate = roleChangeHistoryReq.endDate?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDateTime.parse(it + "T23:59:59")
                } catch (e: DateTimeParseException) {
                    null // 유효하지 않은 날짜인 경우 null 처리
                }
            }
        }
        val roleChangeHistoryList = roleChangeHistoryTbRepo.findAll(
                RoleChangeHistoryTbPredicate.searchLike(roleChangeHistoryTb),
                PageRequest.of(page-1, 10,
                        Sort.by(Sort.Direction.DESC, "changedAt"))).map { it.copy() }
        val roleChangeHistoryTbList = roleChangeHistoryList.content.map{
            val number = roleChangeHistoryList.content.indexOf(it) + 1 + (10 * (page -1))
            convertToRoleChangeHistoryTb(it.copy(), number)
        }
        return PageListRes(
            totalPages = roleChangeHistoryList.totalPages,
            currentPage = page,
            pageSize = 10,
            totalElements = roleChangeHistoryList.totalElements,
            list = roleChangeHistoryTbList
        )
    }
    fun convertToRoleChangeHistoryTb(roleChangeHistoryTb: RoleChangeHistoryTb, number: Int): RoleChangeHistoryTb {
        roleChangeHistoryTb.number = number
        roleChangeHistoryTb.afterRoleName = getRoleNameById(roleChangeHistoryTb.afterRoleId!!.toInt())!!
        roleChangeHistoryTb.name = MaskingUtil.maskName(roleChangeHistoryTb.name)
        roleChangeHistoryTb.email = MaskingUtil.maskEmail(roleChangeHistoryTb.email)
        return roleChangeHistoryTb
    }

    // 권한의 Id값으로 권한명 가져오기
    fun getRoleNameById(roleId: Int): String? {
        return roleTbRepo.findOne(RoleTb().apply { this.roleId = roleId.toLong() })?.name?: throw ErrorMessage.ROLE_NOT_FOUND.exception
    }

    fun getRoleById(roleId: Int): RoleTb {
        return roleTbRepo.findById(roleId)
            .orElseThrow { ErrorMessage.ROLE_NOT_FOUND.exception }
    }

    // 권한명으로 권한 가져오기
    fun getRoleByName(roleName: String): RoleTb {
        return roleTbRepo.findOne(RoleTb().apply {
            this.name = roleName
        }) ?: throw ErrorMessage.ROLE_NOT_FOUND.exception
    }

    // 입력받은 role id값 기반으로 권한별 메뉴 권한 가져오기
    fun getRoleMenuListWithM(roleId: Int): List<String> {
        val roleMenus = roleMenuTbRepo.findByRoleId(roleId)
        val permissionStrings = mutableListOf<String>()
        roleMenus.forEach { roleMenu ->
            if (roleMenu.approvalYn == "Y") permissionStrings.add("M${roleMenu.menuId}A")
            if (roleMenu.createYn == "Y") permissionStrings.add("M${roleMenu.menuId}C")
            if (roleMenu.readYn == "Y") permissionStrings.add("M${roleMenu.menuId}R")
            if (roleMenu.updateYn == "Y") permissionStrings.add("M${roleMenu.menuId}U")
            if (roleMenu.deleteYn == "Y") permissionStrings.add("M${roleMenu.menuId}D")
        }
        return permissionStrings
    }

    fun getRoleMenuListWithoutM(roleId: Int): List<PermissionListDTO> {
        val roleMenus = roleMenuTbRepo.findByRoleId(roleId).map{it.copy()}
        val permissionMap = mutableMapOf<String, MutableList<String>>()

        roleMenus.forEach { roleMenu ->
            // 메뉴 ID를 기준으로 권한 목록을 구성
            val permissions = permissionMap.getOrPut(roleMenu.menuId.toString()) { mutableListOf() }

            // 각 권한 유형별로 확인하고, 해당되는 권한이 있을 경우 목록에 추가
            if (roleMenu.approvalYn == "Y") permissions.add("A")
            if (roleMenu.createYn == "Y") permissions.add("C")
            if (roleMenu.readYn == "Y") permissions.add("R")
            if (roleMenu.updateYn == "Y") permissions.add("U")
            if (roleMenu.deleteYn == "Y") permissions.add("D")
        }

        // 권한 목록을 PermissionDTO 객체로 변환 전, 권한이 하나도 없는 메뉴는 제외
        return permissionMap.mapNotNull { (menu, perms) ->
            if (perms.isNotEmpty()) PermissionListDTO(menu, perms.sorted()) else null
        }
    }

    // 사용자의 '역할 - 메뉴'에 따른 권한(P,C,R,U,D) 수정
    fun modifyRoleMenu(permissionPerMenuReq: PermissionPerMenuReq): HashMap<String, Any> {
        val roleId = permissionPerMenuReq.roleId?.toLong()!!

        if (!roleTbRepo.existsById(roleId.toInt())) {
            throw ErrorMessage.ROLE_NOT_FOUND.exception
        }
        // JUNIT 권한이면 진행X
        if (roleId == 30L) return HashMap()
        permissionPerMenuReq.menuPermissionList?.forEach { menuPermission ->

            val existingRoleMenu = roleMenuTbRepo.findByRoleIdAndMenuId(
                roleId,
                menuPermission.menuId!!.toLong() // menuId는 각 menuPermission 내부에 존재
            )?.copy()?: throw ErrorMessage.ROLE_MENU_NOT_FOUND.exception

            val updatedRoleMenu = existingRoleMenu.apply {
                this.approvalYn = menuPermission.approval?.let { if (it) "Y" else "N" } ?: this.approvalYn
                this.createYn = menuPermission.create?.let { if (it) "Y" else "N" } ?: this.createYn
                this.readYn = menuPermission.read?.let { if (it) "Y" else "N" } ?: this.readYn
                this.updateYn = menuPermission.update?.let { if (it) "Y" else "N" } ?: this.updateYn
                this.deleteYn = menuPermission.delete?.let { if (it) "Y" else "N" } ?: this.deleteYn
                // readYn에 따라 showYn도 업데이트
                this.showYn = this.readYn
                this.modifiedAt = LocalDateTime.now(ZoneOffset.UTC)
            }
            roleMenuTbRepo.save(updatedRoleMenu)
        }
        // 해당 역할 ID를 가진 모든 사용자 조회
        val usersWithRole = userTbRepo.findAll(UserTbPredicate.searchLike(UserTb().apply { this.roleId = roleId })).map { it.copy() }

        // Redis에 사용자 토큰 값 대체
        usersWithRole.forEach { user ->
            redis.opsForValue().set("GlobalSearch-Token-Serial.{${user.userId}}", "RoleMenuPermissionChanged")
        }
        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }

    /**
     * 권한별 메뉴 트리 조회
     */
    fun getMenuTree(tokenData: TokenData): List<MenuTb> {
        // 슈퍼 유저 확인
        // 아래의 ID는 슈퍼 유저로 간주됩니다. 새로운 슈퍼 유저 ID를 추가하려면 아래 목록에 추가하십시오.
        val superUserIds = listOf(999L, 989L, 1051L)
        if (tokenData.userId in superUserIds) return getFullMenuTree()

        val roleId = userTbRepo.findOne(UserTb().apply {
            this.userId = tokenData.userId
        })?.roleId ?: throw ErrorMessage.USER_NOT_FOUND.exception

        // 입력받은 roleId에 해당하고, showYn이 "Y"인 roleMenuTb 데이터 가져오기
        val roleMenuList = roleMenuTbRepo.findByRoleIdAndShowYn(roleId, "Y").map { it.copy() }

        // menuId 값들을 List로 만들기
        val menuIdList = roleMenuList.mapNotNull { it.menuId?.toInt() }

        // 해당 menuIdList를 기반으로 menuTb 데이터 가져오기
        val menus = menuTbRepo.findAllById(menuIdList).map{it.copy()}.filter { it.menuOrder != null }

        // 메인 메뉴 식별 및 분리, 동시에 children을 빈 리스트로 초기화
        val mainMenu = menus.find { it.menuId == 101L }?.apply { this.children = mutableListOf() }

        val otherMenus = menus.filterNot { it.menuId == 101L }

        // 루트메뉴 아이디로 메뉴들을 그룹화 (메인 메뉴를 제외한 나머지 메뉴들로)
        val menuMap = otherMenus.groupBy { it.rootMenuId }.mapValues { it.value.sortedBy { menu -> menu.menuOrder } }

        // 메뉴트리를 만들기 위한 재귀 함수
        fun buildMenuTree(menu: MenuTb, menuMap: Map<Long?, List<MenuTb>>): Boolean {
            val children = menuMap[menu.menuId]
            if (!children.isNullOrEmpty()) {
                menu.children?.addAll(children)
                return true
            }
            return false
        }

        // 각 루트 메뉴를 얻고 메뉴 오더에 따라 정렬, 자식이 있는 메뉴만 필터링 (메인 메뉴를 제외한 나머지 메뉴들로)
        val rootMenus = menuTbRepo.findByRootMenuIdIsNull().map{it.copy()}.filter { it.menuOrder != null && it.menuId != 101L }
            .mapNotNull { rootMenu ->
                rootMenu.children = mutableListOf()
                if (buildMenuTree(rootMenu, menuMap)) rootMenu else null
            }.sortedBy { it.menuOrder }

        // 최종 메뉴 리스트에 메인 메뉴를 최상위로 추가, 만약 메인 메뉴가 없다면 rootMenus만 반환
        return listOfNotNull(mainMenu) + rootMenus
    }

    /**
     * 메뉴 트리 조회
     */
    fun getFullMenuTree(): List<MenuTb> {
        // 모든 메뉴를 가져온다
        val menus = menuTbRepo.findAll().map{it.copy()}.filter { it.menuOrder != null }

        // 메인 메뉴 식별 및 분리, children을 빈 리스트로 초기화
        val mainMenu = menus.find { it.menuId == 101L }?.apply { this.children = mutableListOf() }

        val otherMenus = menus.filterNot { it.menuId == 101L }

        // 루트메뉴 아이디로 메뉴들 그룹화 (메인 메뉴를 제외한 나머지 메뉴들로)
        val menuMap = otherMenus.groupBy { it.rootMenuId }.mapValues { it.value.sortedBy { menu -> menu.menuOrder } }

        // 메뉴트리 만드는 함수
        fun buildMenuTree(menu: MenuTb, menuMap: Map<Long?, List<MenuTb>>): Boolean {
            val children = menuMap[menu.menuId]
            if (!children.isNullOrEmpty()) {
                menu.children = children.toMutableList()
                children.forEach { childMenu -> buildMenuTree(childMenu, menuMap) }
                return true
            }
            return false
        }

        // 루트메뉴 얻고 메뉴 오더에 따라 정렬, 자식이 있는 메뉴만 필터링 (메인 메뉴를 제외한 나머지 메뉴들로)
        val rootMenus = menuTbRepo.findByRootMenuIdIsNull().map{it.copy()}.filter { it.menuOrder != null && it.menuId != 101L }
            .mapNotNull { rootMenu ->
                rootMenu.children = mutableListOf()
                if (buildMenuTree(rootMenu, menuMap)) rootMenu else null
            }.sortedBy { it.menuOrder }

        // 최종 메뉴 리스트에 메인 메뉴를 최상위로 추가
        return listOfNotNull(mainMenu) + rootMenus
    }



    /**
     * 역할별 메뉴 권한 리스트
     */
    fun getRoleMenu(req: ChangeMenuPermissionListReq): List<RoleMenuRes>{

        if(req.roleId != null){
            if (!roleTbRepo.existsById(req.roleId.toInt())){
                throw ErrorMessage.ROLE_NOT_FOUND.exception
            }
        }

        // 입력받은 role id값으로 roleMenuTb 데이터 가져오기
        val roleMenuList = roleMenuTbRepo.findByRoleId(req.roleId!!.toInt()).map{it.copy()}
        
        // menuId값들 List로 만들기
        val menuIdList = roleMenuList.map { it.menuId?.toInt() }

        // 해당 menuIdList값들로 menuTb 데이터 가져오기
        val menus = menuTbRepo.findAllById(menuIdList.filterNotNull()).map { it.copy() }.filter{ it.menuOrder != null }

        // 루트메뉴아이디로 메뉴들 그룹화
        val menuMap = menus.groupBy { it.rootMenuId }.mapValues { it.value.sortedBy { menu -> menu.menuOrder } }

        // 메뉴트리를 만들기 위한 재귀 함수
        fun buildMenuTree(rootMenu: MenuTb, menuMap: Map<Long?, List<MenuTb>>): Boolean {
            // 현재 루트 메뉴의 자식 메뉴들을 가져온다
            val children = menuMap[rootMenu.menuId]

            // 자식 메뉴들이 있다면
            if (!children.isNullOrEmpty()) {
                // 현재 부모 메뉴의 자식에 추가한다
                rootMenu.children = children.toMutableList()

                // 각 자식 메뉴에 대해 재귀적으로 buildMenuTree 함수를 호출하고 permission 설정
                children.forEach { child ->
                    // 여기에서 각 메뉴에 대한 RoleMenuTb 객체를 찾아서 permission 필드에 설정
                    val roleMenu = roleMenuList.find { it.menuId == child.menuId }
                    child.permission = roleMenu

                    // 재귀 호출
                    buildMenuTree(child, menuMap)
                }
                return true
            }
            return false
        }
        // 최상위 메뉴 항목을 가져오고 menuId 100, 101을 제외한 후 필터링
        val rootMenus = menuTbRepo.findByRootMenuIdIsNull().map{it.copy()}
            .filter { it.menuOrder != null }
            .mapNotNull { rootMenu ->
                rootMenu.children = mutableListOf()
                if (buildMenuTree(rootMenu, menuMap)) rootMenu else null
            }
            .sortedBy { it.menuOrder }

        // menuId 100과 101을 제외하고 나머지 메뉴 항목만 필터링하여 DTO 변환
        val filteredRootMenus = rootMenus.filterNot { it.menuId == 100L || it.menuId == 101L }
        val menuResponses = convertToMenuResponseDTOs(filteredRootMenus, roleMenuList)

        return menuResponses
    }

    fun convertToMenuResponseDTOs(menus: List<MenuTb>, roleMenuList: List<RoleMenuTb>): List<RoleMenuRes> {
        var currentNo = 1 // 최상위 메뉴 항목에만 사용되는 번호
        return menus.map { menu ->
            // 현재 메뉴의 권한 정보를 찾아 변환
            val permission = roleMenuList.find { it.menuId == menu.menuId }?.let {
                PermissionDTO(
                    menuId = it.menuId ?: 0L,
                    approval = it.approvalYn == "Y",
                    create = it.createYn == "Y",
                    read = it.readYn == "Y",
                    update = it.updateYn == "Y",
                    delete = it.deleteYn == "Y"
                )
            }

            // 자식 메뉴 항목을 변환할 때는 number 매개변수를 업데이트하지 않음
            val children = menu.children?.let { convertToMenuResponseDTOs(it, roleMenuList) } ?: emptyList()

            // 메뉴 응답 객체 생성, 최상위 메뉴에만 no 값을 할당
            RoleMenuRes(
                menuId = menu.menuId ?: 0L,
                label = menu.label ?: "",
                number = if (menu.rootMenuId == null) currentNo++ else null, // root 메뉴일 때만 number 할당
                children = children,
                permission = permission
            )
        }
    }


    /**
     * 역할별 메뉴당 권한 리스트
     */
    fun getRoleMenuPermissionList(): RoleListRes {
        val roleList = roleTbRepo.findAll()
            .filter { it.roleId != 30L } // roleId가 30인 역할을 제외
            .map{it.copy()}
            .sortedBy { it.roleId }
            .map{ roleTb ->
                RolePermissionResponseDTO(
                    roleId = roleTb.roleId!!,
                    roleName = roleTb.name!!,
                    permissionList = getRoleMenuListWithoutM(roleTb.roleId!!.toInt()).sortedBy { it.menu.toInt() },
                    userCount = userTbRepo.countByRoleId(roleTb.roleId!!)
                )
            }
        return RoleListRes(
            list = roleList
        ) }

    /**
     * 역할 변경 사용자 리스트
     */
    fun getRoleChangeUserList(roleChangeUserListReq: RoleChangeUserListReq): PageListRes<UserTb> {
        val page = roleChangeUserListReq.page?: 1

        val roleId = if(roleChangeUserListReq.roleId?.toLong() == 0L) null else roleChangeUserListReq.roleId?.toLong()

        if (roleId != null) {
            if (!roleTbRepo.existsById(roleId.toInt())) {
                throw ErrorMessage.ROLE_NOT_FOUND.exception
            }
        }
        val userTb = UserTb().apply {
            this.email = roleChangeUserListReq.email.takeIf { it?.isNotBlank() == true }
            this.name = roleChangeUserListReq.name.takeIf { it?.isNotBlank() == true }
            this.roleId = roleId
            this.statusName = UserStatus.ACTIVE.name
        }
        val userTbList = userTbRepo.findAll(UserTbPredicate.searchLike(userTb), PageRequest.of(page-1, 10)).map { it.copy() }
        val userDtoList = userTbList.content.map {
            val number = userTbList.content.indexOf(it) + 1 + (10 * (page - 1))
            convertToUserTb(it.copy(), number)
        }
        return PageListRes(
            totalPages = userTbList.totalPages,
            currentPage = page,
            pageSize = 10,
            totalElements = userTbList.totalElements,
            list = userDtoList
        )
    }

    fun convertToUserTb(userTb: UserTb, number: Int): UserTb{
        userTb.number = number
        userTb.roleName = getRoleNameById(userTb.roleId!!.toInt())!!
        return userTb
    }

    /**
     * 검토자 리스트 검색 by name
     */
    fun getReviewerSearch(reviewerSearchReq: FindUserByNameReq): PeopleByName {
        // 검토자 화면에 approvalYn이 Y인 역할 가져오기, 단 roleId가 30이 아닌 경우만
        val roles = roleMenuTbRepo.findByMenuIdAndApprovalYn(143, "Y")
            .filter { it.roleId != 30L }

        val roleIds = roles.mapNotNull { it.roleId }

        if (roleIds.isEmpty()) {
            throw ErrorMessage.REVIEW_APPROVAL_ROLE_NOT_FOUND.exception
        }
        // 그 역할에 해당하는 사용자들 중 이름으로 like 검색
        val userTbList = userTbRepo.findAll(UserTbPredicate.searchLikeWithRoleIds(UserTb().apply {
            this.name = reviewerSearchReq.name
            this.statusName = UserStatus.ACTIVE.name
        }, roleIds)).map{ it.copy() }

        return PeopleByName(
            people = userTbList.map {
                Person(
                    userId = it.userId!!,
                    email = it.email!!,
                    name = it.name!!,
                    company = it.companyName!!,
                    team = it.teamName!!
                )
            }
        )
    }

    /**
     * 승인자 리스트 검색 by name
     */
    fun getManagerSearch(managerSearchReq: FindUserByNameReq): PeopleByName {
        // 승인자 화면에 approvalYn이 Y인 역할 가져오기, 단 roleId가 30이 아닌 경우만
        val roles = roleMenuTbRepo.findByMenuIdAndApprovalYn(144, "Y")
            .filter { it.roleId != 30L }

        val roleIds = roles.mapNotNull { it.roleId }

        if (roleIds.isEmpty()) {
            throw ErrorMessage.MANAGE_APPROVAL_ROLE_NOT_FOUND.exception
        }

        // 그 역할에 해당하는 사용자들 중 이름으로 like 검색
        val userTbList = userTbRepo.findAll(UserTbPredicate.searchLikeWithRoleIds(UserTb().apply {
            this.name = managerSearchReq.name
            this.statusName = UserStatus.ACTIVE.name
        }, roleIds)).map{ it.copy() }

        return PeopleByName(
            people = userTbList.map {
                Person(
                    userId = it.userId!!,
                    email = it.email!!,
                    name = it.name!!,
                    company = it.companyName!!,
                    team = it.teamName!!
                )
            }
        )
    }


    /**
     * IP 접근 제한 추가 [유저관리 > IP 접근관리 - IP 등록]
     */
    fun addIp(ipReq: IpAddReq): HashMap<String, Any> {
        val user = userTbRepo.findOne(UserTb().apply { this.userId = ipReq.userId })?.copy() ?: throw ErrorMessage.USER_NOT_FOUND.exception
        user.roleId?.let {
            if (it.toInt() != 29) throw ErrorMessage.IP_DONT_NEEDED.exception
        }

        if (!isIpValid(ipReq.address)) throw ErrorMessage.INVALID_IP_ADDRESS.exception

        val ip = ipTbRepo.save(IpTb().apply {
            this.userId = ipReq.userId
            this.address = ipReq.address
            ipReq.desc?.let { this.description = it }
            this.registeredAt = LocalDateTime.now(ZoneOffset.UTC)
        })

        return HashMap<String, Any>().apply {
            this["result"] = "Success"
            this["ip"] = ip
        }
    }

    /**
     * 사용자 IP 리스트 조회
     */
    fun getIpList(ipListReq: IpListReq): PageListRes<UserIpForListRes> {
        val page = ipListReq.page?: 1
        val isIpSearch = ipListReq.ip?.isNotBlank() == true
        val userIdList = ipTbRepo.findAll(IpTbPredicate.searchLike(IpTb().apply { this.address = ipListReq.ip.takeIf { it?.isNotBlank() == true }})).map{ it.userId }

        val userIdListByEmailAndName =
                if(isIpSearch) {
                    // IP로 검색하는 경우, userIdList를 사용하여 필터링
                    userTbRepo.findAll(
                            UserTbPredicate.searchLike(UserTb().apply {
                                this.roleId = 29
                                this.email = ipListReq.email.takeIf { it?.isNotBlank() == true }
                                this.name = ipListReq.name.takeIf { it?.isNotBlank() == true }
                                this.statusName = UserStatus.ACTIVE.name }, userIdList),
                            PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC,"name")))
                            .map { it.copy() }
                } else {
                    // IP로 검색하지 않는 경우, 모든 해당 역할의 사용자를 대상으로 조회
                    userTbRepo.findAll(
                            UserTbPredicate.searchLike(UserTb().apply {
                                this.roleId = 29
                                this.email = ipListReq.email.takeIf { it?.isNotBlank() == true }
                                this.name = ipListReq.name.takeIf { it?.isNotBlank() == true }
                                this.statusName = UserStatus.ACTIVE.name }),
                            PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC,"name")))
                            .map { it.copy() }
                }
        
        val userIpList = userIdListByEmailAndName.content.map {userTb ->
            val number = userIdListByEmailAndName.content.indexOf(userTb) + 1 + (10 * (page -1))
            val ip = ipTbRepo.findAll(
                IpTbPredicate.search(IpTb().apply {
                    this.userId = userTb.userId })
            ).map { it.copy() }
            convertToUserIp(userTb, ip.toList(), number)
        }
        return PageListRes(
            totalPages = userIdListByEmailAndName.totalPages,
            currentPage = page,
            pageSize = 10,
            totalElements = userIdListByEmailAndName.totalElements,
            list = userIpList
        )
    }

    fun convertToUserIp(user: UserTb, ipTb: List<IpTb>, number: Int) : UserIpForListRes{
        // IP 정보가 비어 있을 경우, 빈 UserIpList 객체를 생성
        val userIpList = if (ipTb.isEmpty()) {
            listOf(UserIpList(
                    userIpId = -1,
                    address = "",
                    desc = ""
            ))
        } else {
            ipTb.map {
                UserIpList(
                        userIpId = it.ipId!!,
                        address = it.address ?: "",
                        desc = it.description ?: ""
                )
            }
        }

        return UserIpForListRes(
            number = number,
            userId = user.userId!!,
            email = MaskingUtil.maskEmail(user.email),
            name = MaskingUtil.maskName(user.name),
            company = user.companyName,
            team = user.teamName,
            roleId = user.roleId!!,
            roleName = getRoleNameById(user.roleId!!.toInt())!!,
            userIpList = userIpList
        )
    }

    /**
     * IP 수정 [유저관리 > IP 접근관리]
     */
    fun updateIp(ipUpdateReq: IpUpdateReq): HashMap<String, Any> {
        val ip = ipTbRepo.findOne(IpTb().apply { this.ipId = ipUpdateReq.userIpId })?: throw ErrorMessage.IP_ADDRESS_MISSING.exception
        if(!isIpValid(ipUpdateReq.address!!)) throw ErrorMessage.INVALID_IP_ADDRESS.exception
        ip.let { it2 ->
            it2.address = ipUpdateReq.address
            ipUpdateReq.desc?.let { it2.description = ipUpdateReq.desc }
            it2.modifiedAt = LocalDateTime.now(ZoneOffset.UTC)
            ipTbRepo.save(it2)
        }

        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }

    /**
     * IP 삭제 [유저관리 > IP 접근관리]
     */
    fun deleteIp(ipDeleteReq: IpDeleteReq): HashMap<String, Any> {
        ipTbRepo.findOne(IpTb().apply { this.ipId = ipDeleteReq.userIpId })?.let {
            ipTbRepo.delete(it)
        } ?: throw ErrorMessage.IP_ADDRESS_MISSING.exception
        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }
}