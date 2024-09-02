package site.roombook.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "EmplDtoBuilder")
@Getter
@ToString
public class EmplDto implements UserDetails {
    private String emplNo;
    private String emplId;
    private String pwd;
    private String email;
    private int pwdErrTms;
    private String rnm;
    private String engNm;
    private Date subsDtm;
    private String emplAuthNm;
    private String entDt;
    private String brdt;
    private String wncomTelno;
    private int empno;
    private String msgrId;
    private String prfPhotoPath;
    private char subsCertiYn;
    private char termsAgreYn;
    private char subsAprvYn;
    private char secsnYn;
    private LocalDateTime acctSecsnDtm;
    private LocalDateTime fstRegDtm;
    private String fstRegrIdnfNo;
    private LocalDateTime lastUpdDtm;
    private String lastUpdrIdnfNo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(emplAuthNm));
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return emplId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
