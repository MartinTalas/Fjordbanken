package com.fjordbanken.account_service.service;

import java.util.List;
import java.util.stream.Collectors;

public interface MapperService<D, E> {

    E toEntity(D dto);
    D toDto(E entity);

    default List<E> toEntityList(List<D> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    default List<D> toDtoList(List<E> entityList) {
        if (entityList == null) return null;
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
