package com.panchenko.expense_tracker.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(Long telegramId, String username) {
        return userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setTelegramId(telegramId);
                    user.setUsername(username);
                    return userRepository.save(user);
                });
    }
}
