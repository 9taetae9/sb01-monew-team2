package com.codeit.team2.monew.config;

public final class SwaggerTags {
    private SwaggerTags() {}

    public static final class Tags {
        public static final String USER = "사용자 관리";
        public static final String INTEREST = "관심사 관리";
        public static final String NOTIFICATION = "알림 관리";
        public static final String ARTICLE = "뉴스 기사 관리";
        public static final String COMMENT = "댓글 관리";
        public static final String USER_ACTIVITY = "사용자 활동 내역 관리";

        private Tags() {}
    }

    public static final class Descriptions {
        public static final String USER = "사용자 등록, 수정, 삭제 및 로그인 API";
        public static final String INTEREST = "관심사 관련 API";
        public static final String NOTIFICATION = "알림 관련 API";
        public static final String ARTICLE = "뉴스 기사 관련 API";
        public static final String COMMENT = "댓글 관련 API";
        public static final String USER_ACTIVITY = "사용자 활동 내역 관련 API";

        private Descriptions() {}
    }
}
