package store.roombook.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

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
        return emplAuthNm != null ? List.of(new SimpleGrantedAuthority(emplAuthNm)) : Collections.emptyList();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmplDto emplDto = (EmplDto) o;
        return pwdErrTms == emplDto.pwdErrTms && empno == emplDto.empno && subsCertiYn == emplDto.subsCertiYn && termsAgreYn == emplDto.termsAgreYn && subsAprvYn == emplDto.subsAprvYn && secsnYn == emplDto.secsnYn && Objects.equals(emplNo, emplDto.emplNo) && Objects.equals(emplId, emplDto.emplId) && Objects.equals(pwd, emplDto.pwd) && Objects.equals(email, emplDto.email) && Objects.equals(rnm, emplDto.rnm) && Objects.equals(engNm, emplDto.engNm) && Objects.equals(subsDtm, emplDto.subsDtm) && Objects.equals(emplAuthNm, emplDto.emplAuthNm) && Objects.equals(entDt, emplDto.entDt) && Objects.equals(brdt, emplDto.brdt) && Objects.equals(wncomTelno, emplDto.wncomTelno) && Objects.equals(msgrId, emplDto.msgrId) && Objects.equals(prfPhotoPath, emplDto.prfPhotoPath) && Objects.equals(acctSecsnDtm, emplDto.acctSecsnDtm) && Objects.equals(fstRegDtm, emplDto.fstRegDtm) && Objects.equals(fstRegrIdnfNo, emplDto.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, emplDto.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, emplDto.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emplNo, emplId, pwd, email, pwdErrTms, rnm, engNm, subsDtm, emplAuthNm, entDt, brdt, wncomTelno, empno, msgrId, prfPhotoPath, subsCertiYn, termsAgreYn, subsAprvYn, secsnYn, acctSecsnDtm, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
