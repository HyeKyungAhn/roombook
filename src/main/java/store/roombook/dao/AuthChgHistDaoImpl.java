package store.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import store.roombook.domain.AuthChgHistDto;

@Repository
public class AuthChgHistDaoImpl implements AuthChgHistDao{

    String namespace = "site.roombook.dao.authChgHistMapper.";

    @Autowired
    SqlSession session;

    @Override
    public int insert(AuthChgHistDto authChgHistDto) {
        return session.insert(namespace+"insert", authChgHistDto);
    }

    @Override
    public AuthChgHistDto selectLatestOne(String emplNo) {
        return session.selectOne(namespace+"selectLatestOneByEmplNo", emplNo);
    }
}
