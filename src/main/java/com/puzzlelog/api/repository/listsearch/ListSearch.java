package com.puzzlelog.api.repository.listsearch;

public interface ListSearch<T, R> {
    R buildSearch(T request);
}