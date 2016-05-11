package com.zeyad.cleanarchitecture.data.entities.mapper;

import java.util.List;

/**
 * @author Zeyad on 11/05/16.
 */
public interface EntityMapper<D, R> {

    R transformToRealm(D item, Class dataClass);

    List<R> transformAllToRealm(List<D> list, Class dataClass);

    D transformToDomain(R tenderoRealmModel);

    List<D> transformAllToDomain(List<R> tenderoRealmModels);

    D transformToDomain(R userRealmModel, Class domainClass);

    List<D> transformAllToDomain(List<R> tenderoRealmModels, Class domainClass);
}