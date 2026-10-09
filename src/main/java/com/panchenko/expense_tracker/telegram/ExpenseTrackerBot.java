package com.panchenko.expense_tracker.telegram;

import com.panchenko.expense_tracker.category.Category;
import com.panchenko.expense_tracker.category.CategoryService;
import com.panchenko.expense_tracker.user.User;
import com.panchenko.expense_tracker.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpenseTrackerBot extends DefaultLongPollingUpdateConsumer {

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public void consume(Update update) {

        if (update.hasCallbackQuery()) {
            handleCallback(update);
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var message = update.getMessage();
        var telegramUser = message.getFrom();

        log.info("Получено сообщение: updateId={}, chatId={}, text={}",
                update.getUpdateId(), message.getChat().getId(), message.getText());

        if ("/start".equals(message.getText())) {
            Long telegramId = telegramUser.getId();
            String username = telegramUser.getUserName();

            userService.getOrCreateUser(telegramId, username);

            sendText(message.getChat().getId(), "Привет! Я помогу тебе учитывать расходы.");
        } else if ("/categories".equals(message.getText())) {
            Long telegramId = telegramUser.getId();
            String username = telegramUser.getUserName();

            User user = userService.getOrCreateUser(telegramId, username);
            List<Category> categories = categoryService.getAvailableCategories(user);

            StringBuilder availableCategories = new StringBuilder("Доступные категории:\n");

            for (var category : categories) {
                availableCategories.append("- ").append(category.getName()).append("\n");
            }

            sendText(message.getChat().getId(), availableCategories.toString());
        }
    }

    private void handleCallback(Update update) {

    }

    private void sendText(Long chatId, String text) {
        SendMessage response = SendMessage.builder().chatId(chatId).text(text).build();

        try {
            telegramClient.execute(response);
        } catch (TelegramApiException exception) {
            log.error("Не удалось отправить сообщение в Telegram", exception);
        }
    }
}