package rarus.chat;

import org.junit.jupiter.api.Test;
import rarus.chat.client.main.ClientWriter;

import java.util.Scanner;

public class MainTest {
    @Test
    void mainTest() {
        String name = "testName";

        Thread chatThread = null;
        ClientWriter clientWriter = new ClientWriter(name);
        chatThread = new Thread(clientWriter);
        chatThread.start();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                clientWriter.passString(scanner.nextLine());
            }
        }
        chatThread.stop();
    }
}
