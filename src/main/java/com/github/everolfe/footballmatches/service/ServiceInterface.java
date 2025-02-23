package com.github.everolfe.footballmatches.service;

import java.util.List;

public interface ServiceInterface<T> {
    void create(T obj);

    List<T> readAll();

    T read(final Integer id);

    boolean update(T obj, final Integer id);

    boolean delete(final Integer id);
}
