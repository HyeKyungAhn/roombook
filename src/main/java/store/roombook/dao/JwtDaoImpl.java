package store.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import store.roombook.domain.JwtDto;

@Repository
public class JwtDaoImpl implements JwtDao{

    private final String namespace = "site.roombook.dao.jwtMapper.";

    @Autowired
    private SqlSession session;

    @Override
    public JwtDto selectByToken(String token) {
        return session.selectOne(namespace+"selectByToken", token);
    }

    @Override
    public JwtDto selectUnexpiredTokenByToken(String token) {
        return session.selectOne(namespace+"selectUnexpiredTokenByToken", token);
    }

    @Override
    public JwtDto selectUnexpiredTokenAndAuthority(String token) {
        return session.selectOne(namespace+"selectUnexpiredTokenAndAuthority", token);
    }

    @Override
    public int insertToken(JwtDto jwtDto) {
        return session.insert(namespace+"insertToken", jwtDto);
    }

    @Override
    public int expireTokenByToken(String token) {
        return session.update(namespace + "expireTokenByToken", token);
    }

    @Override
    public int expireTokenByEmplId(String emplId) {
        return session.update(namespace+"expireTokenByEmplId", emplId);
    }

    @Override
    public void deleteAll() {
        session.delete(namespace + "deleteAll");
    }
}
