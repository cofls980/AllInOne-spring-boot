package com.hongik.pcrc.allinone.application.Authentication;

import com.hongik.pcrc.allinone.application.domain.Auth;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthDetailsService implements UserDetailsService {

    @Autowired
    private AuthEntityRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Auth auth = repository.findById(username).get().toAuth();
        if (auth == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        User user = new User(auth.getId(), auth.getPassword(), true, true, true, true, new ArrayList<>());

        return user;
    }
}
