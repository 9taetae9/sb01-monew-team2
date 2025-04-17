package com.codeit.team2.monew.module.domain.article.dto;

/**
 * 다양한 뉴스 api 요청에 필요한 필드를 공통으로 묶어 제공하는 DTO.
 * <br>
 * 이후 필요에 따라 추가 필드 생성
 */
public record FetchCommand(
    String keyword,
    int pageSize,
    int offset,
    String sortBy
) {

}
