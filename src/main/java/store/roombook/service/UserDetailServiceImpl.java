package store.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import store.roombook.dao.EmplDao;
import store.roombook.domain.EmplDto;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private EmplDao emplDao;

    @Override
    public UserDetails loadUserByUsername(String emplId) throws UsernameNotFoundException {
        EmplDto emplDto = emplDao.selectEmplById(emplId).orElseThrow(() -> new AuthenticationServiceException(emplId));
        return EmplDto.EmplDtoBuilder()
                .emplId(emplDto.getEmplId())
                .pwd(emplDto.getPwd())
                .emplAuthNm(emplDto.getEmplAuthNm())
                .build();
    }
}