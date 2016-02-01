package com.zeyad.cleanarchetecturet.data.cache.serializer;

import com.google.gson.Gson;
import com.zeyad.cleanarchetecturet.data.entity.UserEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class user as Serializer/Deserializer for user entities.
 */
@Singleton
public class UserJsonSerializer {

    private final Gson gson = new Gson();

    @Inject
    public UserJsonSerializer() {
    }

    /**
     * Serialize an object to Json.
     *
     * @param userEntity {@link UserEntity} to serialize.
     */
    public String serialize(UserEntity userEntity) {
        return gson.toJson(userEntity, UserEntity.class);
    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param jsonString A json string to deserialize.
     * @return {@link UserEntity}
     */
    public UserEntity deserialize(String jsonString) {
        return gson.fromJson(jsonString, UserEntity.class);
    }
}