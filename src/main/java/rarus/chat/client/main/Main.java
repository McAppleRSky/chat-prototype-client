package rarus.chat.client.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("deprecation")
public class Main {

    public static void main(String[] args) {

        Thread chatThread = null;

        try (Scanner scanner = new Scanner(System.in)) {
            String name = "";
            System.out.print("For enter chat input your nic: ");
            while (name.isEmpty()) {
                if(scanner.hasNext()) {
                    String stringName = scanner.nextLine();
                    name = stringName.trim().split(" ")[0];
                }
            }

            ClientWriter clientWriter = new ClientWriter(name);
            chatThread = new Thread(clientWriter);
            chatThread.start();

            while (scanner.hasNext()) {
                clientWriter.passString(scanner.nextLine());
            }
        }

        chatThread.stop();
    }

}
