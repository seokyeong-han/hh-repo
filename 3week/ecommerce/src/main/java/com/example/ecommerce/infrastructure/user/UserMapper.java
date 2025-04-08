package com.example.ecommerce.infrastructure.user;

import com.example.ecommerce.domain.user.User;

public class UserMapper {
    //User 도메인, UserJpaEntity 연경해주는 용도
    //나중에 MongoDB등으로 변경시 이곳에서 변경만 해주면됨

    public static User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getUsername());
    }
}
