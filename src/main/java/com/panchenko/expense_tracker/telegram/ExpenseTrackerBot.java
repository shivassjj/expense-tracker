package com.panchenko.expense_tracker.telegram;

import com.panchenko.expense_tracker.category.Category;
import com.panchenko.expense_tracker.category.CategoryService;
import com.panchenko.expense_tracker.currency.CurrencyCode;
import com.panchenko.expense_tracker.user.User;
import com.panchenko.expense_tracker.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpenseTrackerBot extends DefaultLongPollingUpdateConsumer {

    private static final String CURRENCY_CALLBACK_PREFIX = "currency:";

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

        User user = userService.getOrCreateUser(telegramUser.getId(), telegramUser.getUserName());

        log.info("Получено сообщение: updateId={}, chatId={}, text={}",
                update.getUpdateId(), message.getChat().getId(), message.getText());

        if ("/start".equals(message.getText())) {
            sendText(message.getChat().getId(), "Привет! Я помогу тебе учитывать расходы.");
        } else if ("/categories".equals(message.getText())) {
            List<Category> categories = categoryService.getAvailableCategories(user);

            StringBuilder availableCategories = new StringBuilder("Доступные категории:\n");

            for (var category : categories) {
                availableCategories.append("- ").append(category.getName()).append("\n");
            }

            sendText(message.getChat().getId(), availableCategories.toString());
        } else if ("/settings".equals(message.getText())) {
            sendCurrencySettings(message.getChat().getId());
        }
    }

    private void handleCallback(Update update) {
        var callbackQuery = update.getCallbackQuery();
        answerCallback(callbackQuery.getId());

        String callbackData = callbackQuery.getData();

        if (callbackData == null) {
            return;
        }

        if (callbackData.startsWith(CURRENCY_CALLBACK_PREFIX)) {
            handleCurrencyCallback(callbackQuery, callbackData);
        }
    }

    private void answerCallback(String callbackQueryId) {
        AnswerCallbackQuery response = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .build();

        try {
            telegramClient.execute(response);
        } catch (TelegramApiException exception) {
            log.error("Не удалось подтвердить callback", exception);
        }
    }

    private void handleCurrencyCallback(CallbackQuery callbackQuery, String callbackData) {
        String currencyValue = callbackData.substring(CURRENCY_CALLBACK_PREFIX.length());
        CurrencyCode currencyCode;
        try {
            currencyCode = CurrencyCode.valueOf(currencyValue);
        } catch (IllegalArgumentException exception) {
            return;
        }
        Long telegramId = callbackQuery.getFrom().getId();

        userService.setDefaultCurrency(telegramId, currencyCode);

        Long chatId = callbackQuery.getMessage().getChat().getId();

        sendText(chatId, "Валюта установлена: " + currencyCode);
    }


    private void sendCurrencySettings(Long chatId) {
        InlineKeyboardButton rubButton = InlineKeyboardButton.builder()
                .text("RUB")
                .callbackData(CURRENCY_CALLBACK_PREFIX + CurrencyCode.RUB)
                .build();

        InlineKeyboardButton usdButton = InlineKeyboardButton.builder()
                .text("USD")
                .callbackData(CURRENCY_CALLBACK_PREFIX + CurrencyCode.USD)
                .build();

        InlineKeyboardButton eurButton = InlineKeyboardButton.builder()
                .text("EUR")
                .callbackData(CURRENCY_CALLBACK_PREFIX + CurrencyCode.EUR)
                .build();

        InlineKeyboardRow currencyRow = new InlineKeyboardRow();

        currencyRow.add(rubButton);
        currencyRow.add(usdButton);
        currencyRow.add(eurButton);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboard(List.of(currencyRow))
                .build();

        SendMessage response = SendMessage.builder()
                .chatId(chatId)
                .text("Выбери валюту")
                .replyMarkup(keyboard)
                .build();

        sendMessage(response);
    }

    private void sendText(Long chatId, String text) {
        SendMessage response = SendMessage.builder().chatId(chatId).text(text).build();

        sendMessage(response);
    }

    private void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException exception) {
            log.error("Не удалось отправить сообщение в Telegram", exception);
        }
    }

}