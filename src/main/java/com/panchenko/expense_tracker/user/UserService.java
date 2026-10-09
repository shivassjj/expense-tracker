package com.panchenko.expense_tracker.user;

import com.panchenko.expense_tracker.currency.CurrencyCode;
import com.panchenko.expense_tracker.user.exception.UserNotFoundException;
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

    @Transactional
    public User setDefaultCurrency(Long telegramId, CurrencyCode currencyCode) {
        if (telegramId == null || currencyCode == null) {
            throw new IllegalArgumentException("Telegram ID and currency must not be null.");
        }

        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь с id " + telegramId + " не найден"
                ));

        user.setDefaultCurrency(currencyCode);
        return user;
    }
}
