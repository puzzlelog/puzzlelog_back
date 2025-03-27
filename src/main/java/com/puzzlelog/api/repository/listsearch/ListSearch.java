package com.puzzlelog.api.repository.listsearch;

/**
 * 목록 조회 조건 빌더 인터페이스
 * - 요청 객체(Request DTO)를 기반으로 동적 검색 조건(Specification 등)을 생성합니다.
 * - 예: 사용자 검색 조건 → Specification<User>
 *
 * @param <T> 검색 조건 요청 DTO
 * @param <R> 반환할 검색 조건 객체 (예: Specification<Entity>)
 */
public interface ListSearch<T, R> {

    /**
     * 동적 검색 조건 생성 메서드
     *
     * @param request 검색 조건이 담긴 DTO
     * @return 생성된 검색 조건 객체 (JPA Specification 등)
     */
    R buildSearch(T request);
} 
