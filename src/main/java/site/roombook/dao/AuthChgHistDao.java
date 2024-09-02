package site.roombook.dao;

import site.roombook.domain.AuthChgHistDto;

public interface AuthChgHistDao {
    int insert(AuthChgHistDto authChgHistDto);

    AuthChgHistDto selectLatestOne(String emplNo);
}
