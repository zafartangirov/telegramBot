package uz.pdp.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static uz.pdp.bot.BotService.*;

public class BotController {
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void start() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                executorService.execute(() -> {
                    try {
                        handleUpdates(update);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleUpdates(Update update) throws IOException, InterruptedException {
        if (update.message() != null){
            Message message = update.message();
            TgUser tgUser = getOrCreateUser(message.chat().id(), message.from().firstName(), message.from().lastName());
            if (message.text() != null){
                if (message.text().equals("/start")){
                    acceptStartAndShowUsers(tgUser);
                }
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            String data = callbackQuery.data();
            TgUser tgUser = getOrCreateUser(callbackQuery.from().id(), callbackQuery.from().firstName(), callbackQuery.from().lastName());
            if (tgUser.getState().equals(TgState.SHOWING_POSTS)){
                showingPosts(tgUser);
            }else if (tgUser.getState().equals(TgState.SELECTING_POSTS)){
                selectingPosts(tgUser, data);
            }else if (tgUser.getState().equals(TgState.VIEWING_COMMENTS)){
                selectedPostsAndShowingComments(tgUser, data);
            }
        }
    }
}
