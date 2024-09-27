package uz.pdp;

import uz.pdp.bot.BotController;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        BotController botController = new BotController();
        botController.start();
    }
}