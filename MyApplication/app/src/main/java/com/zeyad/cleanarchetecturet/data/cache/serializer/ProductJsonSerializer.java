package com.zeyad.cleanarchetecturet.data.cache.serializer;

import com.google.gson.Gson;
import com.zeyad.cleanarchetecturet.data.entity.ProductEntity;
import com.zeyad.cleanarchetecturet.data.entity.UserEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class user as Serializer/Deserializer for user entities.
 */
@Singleton
public class ProductJsonSerializer {

    private final Gson gson = new Gson();

    @Inject
    public ProductJsonSerializer() {
    }

    /**
     * Serialize an object to Json.
     *
     * @param productEntity {@link UserEntity} to serialize.
     */
    public String serialize(ProductEntity productEntity) {
        return gson.toJson(productEntity, ProductEntity.class);
    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param jsonString A json string to deserialize.
     * @return {@link UserEntity}
     */
    public ProductEntity deserialize(String jsonString) {
        return gson.fromJson(jsonString, ProductEntity.class);
    }
}
