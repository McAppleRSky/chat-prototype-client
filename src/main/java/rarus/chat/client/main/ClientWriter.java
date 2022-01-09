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

public class ClientWriter implements Runnable{

    private String name;
    private boolean authorized = false, continueChat = true;
    private final Queue<String>
            inputStrings = new ConcurrentLinkedQueue<>();
    private final ObjectMapper
            objectMapper = new ObjectMapper();

    public ClientWriter(String name) {
        this.name = name;
    }

    public void passString(String inputString) {
        inputStrings.add(inputString);
    }

    @Override
    public void run() {
        try (Socket clientSocket = new Socket(InetAddress.getByName(null), 3443)) {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                try (Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()))) {
                    while (!authorized) {
                        out.println(
                                objectMapper.writeValueAsString(
                                        new Message(name, "", "") ) );
                        if (in.hasNext()) {
                            String returnedName = objectMapper.readValue(in.next(), Message.class).getName();
                            if (name.equals(returnedName)){
                                authorized = true;
                            }
                        }
                    }

                    while (continueChat) {
                        String inputString = inputStrings.poll();
                        if (inputString != null) {
                            out.println(
                                    objectMapper.writeValueAsString(
                                            new Message(name, "", inputString) ) );
                        }
                        while (in.hasNext()) {
                            Message message = objectMapper.readValue(in.next(), Message.class);
                            printMessage(message);
                        }
                    }
                }
            }
        }catch (ConnectException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void printMessage(Message message) {
        String templateMessage = "%s %s : %s";
        System.out.println(
                String.format( templateMessage,
                        message.getDateTime(),
                        message.getName(),
                        message.getText() ) );
    }

}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Message {

    private String name;
    @JsonProperty("date_time")
    private String dateTime;
    private String text;

}
