package com.love.timi.auth

@Target(AnnotationTarget.FUNCTION)
annotation class AuthType(val auth: Array<Authority>) {
    enum class Authority(val code: String, val category: String? = null, val subCategory: String? = null){
        MAIN_MENU_READ("M101R", "Main"), // 메인 - 읽기 권한

        SEARCH_MAP_READ("M121R", "Search-Management", "Map"), // 검색관리 - 지도 - 읽기 권한

        USER_LIST_APPROVE("M161A","User-Management","User-List"), //사용자 관리 - 사용자 목록 - 승인 권한
        USER_LIST_READ("M161R","User-Management","User-List"), //사용자 관리 - 사용자 목록 - 읽기 권한
        USER_LIST_UPDATE("M161U","User-Management","User-List"), //사용자 관리 - 사용자 목록 - 수정 권한

        USER_LOGIN_HISTORY_READ("M162R","User-Management","Login-History"), //사용자 관리 - 로그인 이력 - 읽기 권한
        USER_ROLE_HISTORY_READ("M163R","User-Management","Role-Change-History"), //사용자 관리 - 권한 변경 이력 - 읽기 권한
        USER_IP_MANAGEMENT_CREATE("M164C","User-Management","IP-Management"), //사용자 관리 - IP 관리 - 생성 권한
        USER_IP_MANAGEMENT_READ("M164R","User-Management","IP-Management"), //사용자 관리 - IP 관리 - 읽기 권한
        USER_IP_MANAGEMENT_UPDATE("M164U","User-Management","IP-Management"), //사용자 관리 - IP 관리 - 수정 권한
        USER_IP_MANAGEMENT_DELETE("M164D","User-Management","IP-Management"), //사용자 관리 - IP 관리 - 삭제 권한

        // 권한관리 하위의 메뉴변경도 마찬가지
        ROLE_MANAGEMENT_CREATE("M171C","Permission-Management","Permission-List"), //권한 관리 - 권한 목록 - 생성 권한
        ROLE_MANAGEMENT_READ("M171R","Permission-Management","Permission-List"), //권한 관리 - 권한 목록- 읽기 권한
        ROLE_MANAGEMENT_UPDATE("M171U","Permission-Management","Permission-List"), //권한 관리 - 권한 목록 - 수정 권한
    }
}
