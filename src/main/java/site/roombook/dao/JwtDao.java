package site.roombook.dao;

import org.springframework.stereotype.Repository;
import site.roombook.domain.JwtDto;

@Repository
public interface JwtDao {

    JwtDto selectByToken(String token);

    JwtDto selectUnexpiredTokenByToken(String token);

    JwtDto selectUnexpiredTokenAndAuthority(String token);

    int insertToken(JwtDto jwtDto);

    int expireTokenByToken(String token);

    int expireTokenByEmplId(String emplId);

    void deleteAll();
}
