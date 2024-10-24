package store.roombook.dao;

import store.roombook.domain.AuthChgHistDto;

public interface AuthChgHistDao {
    int insert(AuthChgHistDto authChgHistDto);

    AuthChgHistDto selectLatestOne(String emplNo);
}
