package com.hongik.pcrc.allinone.security.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService { // UserDetailsService 클래스 커스텀
    /*
    * DB에서 유저의 정보를 조회하는 역할 수행
    * UserDetailsService 인터페이스에서 DB에서 유저정보를 불러오는 중요한 메소드는 loadUserByUsername() 메서드이다.
    * */
    private final AuthEntityRepository authEntityRepository;
    private final AuthMapperRepository authMapperRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws AuthenticationException {

        Auth auth = authEntityRepository.findByEmail(email).get().toAuth();

        if (auth == null) {
            throw new AllInOneException(MessageType.UsernameOrPasswordNotFound);
        }

        return auth;
    }
}
