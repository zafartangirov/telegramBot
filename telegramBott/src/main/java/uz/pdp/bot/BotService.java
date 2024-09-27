package uz.pdp.bot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import uz.pdp.DB;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BotService {
    public static TelegramBot telegramBot = new TelegramBot("7688394878:AAG64tzuhgH0Labtd6oaX0oAdVRqQMIf21Y");

    public static TgUser getOrCreateUser(Long chatId, String firstName, String lastName) {
        for (TgUser tgUser : DB.USERS) {
            if (tgUser.getChatId().equals(chatId)){
                return tgUser;
            }
        }
        TgUser tgUser = new TgUser();
        tgUser.setChatId(chatId);
        tgUser.setFirstName(firstName);
        if (lastName != null){
            tgUser.setLastName(lastName);
        }
        tgUser.setLastName("");
        DB.USERS.add(tgUser);
        return tgUser;
    }

    public static void acceptStartAndShowUsers(TgUser tgUser) throws IOException, InterruptedException {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                        Assalomu aleykum botimizga xush kelibsiz,
                        hurmatli %s %s
                        """.formatted(tgUser.getFirstName(), tgUser.getLastName())
        );
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        Message message = sendResponse.message();

        Integer messageId = message.messageId();

        tgUser.setLastSentMessageId(messageId);
        tgUser.setState(TgState.SHOWING_POSTS);
        showingPosts(tgUser);
    }

    public static void showingPosts(TgUser tgUser) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        Gson gson = new Gson();
        List<TgUser> users = gson.fromJson(str, new TypeToken<List<TgUser>>() {}.getType());
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                        USERS:
                        
                        """
        );
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        int i = 0;
        for (TgUser user : users) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(user.getName()).callbackData("user"),
                    new InlineKeyboardButton("posts").callbackData("posts" + ++i)
            );
            sendMessage.replyMarkup(inlineKeyboardMarkup);
        }
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        Message message = sendResponse.message();

        Integer messageId = message.messageId();

        tgUser.setLastSentMessageId(messageId);
        tgUser.setState(TgState.SELECTING_POSTS);
    }

    public static void selectingPosts(TgUser tgUser, String data) throws IOException, InterruptedException {
        switch (data){
            case "posts1" ->  {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(1)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post1"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + ++i)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }
            case "posts2" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(2)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post2"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts3" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(3)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post3"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts4" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(4)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post4"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts5" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(5)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post5"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts6" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(6)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post6"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts7" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(7)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post7"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts8" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(8)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post8"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts9" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(9)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post9"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }case "posts10" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                SendMessage sendMessage = new SendMessage(
                        tgUser.getChatId(),
                        """
                                POSTS:
                                """
                );
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String str = response.body();
                Gson gson = new Gson();
                List<Post> posts = gson.fromJson(str, new TypeToken<List<Post>>(){}.getType());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                int i = 0;
                for (Post post : posts) {
                        if (post.getUserId().equals(10)) {
                            inlineKeyboardMarkup.addRow(
                                    new InlineKeyboardButton(post.getTitle()).callbackData("post10"),
                                    new InlineKeyboardButton("comment").callbackData("commentary" + i++)
                            );
                        }
                }
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅Orqaga").callbackData("back")
                );
                sendMessage.replyMarkup(inlineKeyboardMarkup);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                Message message = sendResponse.message();

                Integer messageId = message.messageId();

                tgUser.setLastSentMessageId(messageId);
                tgUser.setState(TgState.VIEWING_COMMENTS);
            }
        }
    }

    public static void selectedPostsAndShowingComments(TgUser tgUser, String data) throws IOException, InterruptedException {
        switch (data){
            case "commentary1" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary2" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/2/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary3" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/3/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary4" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/4/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary5" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/5/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary6" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/6/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary7" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/7/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary8" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/8/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary9" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/9/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }case "commentary10" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);

                try {
                    byte[] bytes = Files.readAllBytes(Path.of("photos/comment.jpg"));
                    SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), bytes);

                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest commentRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/10/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    List<Comment> comments = gson.fromJson(commentResponse.body(), new TypeToken<List<Comment>>() {
                    }.getType());

                    if (!comments.isEmpty()) {
                        Comment firstComment = comments.get(0);
                        sendPhoto.caption(firstComment.getBody());
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton("⬅ Orqaga").callbackData("backto")
                    );
                    sendPhoto.replyMarkup(inlineKeyboardMarkup);

                    SendResponse sendResponse = telegramBot.execute(sendPhoto);
                    Message message = sendResponse.message();

                    tgUser.setLastSentMessageId(message.messageId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            case "back" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                showingPosts(tgUser);
            }
            case "backto" -> {
                DeleteMessage deleteMessage = new DeleteMessage(tgUser.getChatId(), tgUser.getLastSentMessageId());
                telegramBot.execute(deleteMessage);
                showingPosts(tgUser);
            }
        }
    }
}
