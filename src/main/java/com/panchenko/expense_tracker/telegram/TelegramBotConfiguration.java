package com.panchenko.expense_tracker.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramBotConfiguration {

    @Bean(destroyMethod = "close")
    public TelegramBotsLongPollingApplication telegramBotsApplication(
            @Value("${telegram.bot.token}") String token,
            ExpenseTrackerBot bot
    ) throws TelegramApiException {
        TelegramBotsLongPollingApplication application =
                new TelegramBotsLongPollingApplication();

        application.registerBot(token, bot);
        return application;
    }

    @Bean
    public TelegramClient telegramClient (@Value("${telegram.bot.token}") String token) {
        return new OkHttpTelegramClient(token);
    }
}
