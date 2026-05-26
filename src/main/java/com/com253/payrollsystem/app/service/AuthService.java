package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.EndUser;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.AccountRepository;
import java.sql.SQLException;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final AccountRepository accountRepository = new AccountRepository();

    public EndUser authenticate(String username, String password) throws SQLException {
        Optional<EndUser> userOpt = accountRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return null;
        }
        EndUser user = userOpt.get();
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return null;
        }
        return user;
    }
}
