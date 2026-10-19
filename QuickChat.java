/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quickchat;

/**
 *
 * @author Student
 */
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuickChat {
    private static final Scanner input = new Scanner(System.in);
    private static final List<Message> messages = new ArrayList<Message>();
    private static final Path JSON_FILE = Paths.get("messages.json");

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("       QUICKCHAT LOGIN");
        System.out.println("================================");

        String correctUsername = "admin";
        String correctPassword = "1234";
        boolean loggedIn = false;

        for (int attempts = 1; attempts <= 3; attempts++) {
            System.out.print("Enter username: ");
            String username = input.nextLine();

            System.out.print("Enter password: ");
            String password = input.nextLine();

            if (username.equals(correctUsername) && password.equals(correctPassword)) {
                loggedIn = true;
                System.out.println("Login successful!");
                break;
            } else {
                System.out.println("Incorrect login details. Try again.");
            }
        }

        if (!loggedIn) {
            System.out.println("Login failed. Application closing.");
            input.close();
            return;
        }

        System.out.println("\nWelcome to QuickChat.");
        int messageLimit = readMessageLimit();
        int messagesProcessed = 0;
        boolean running = true;

        while (running) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Send Messages");
            System.out.println("2. Show Recently Sent Messages");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    if (messagesProcessed >= messageLimit) {
                        System.out.println("You have reached your message limit.");
                        break;
                    }

                    System.out.println("\nSend Message");
                    System.out.print("Enter recipient number (+27...): ");
                    String recipient = input.nextLine().trim();

                    if (!recipient.matches("\\+27\\d{9}")) {
                        System.out.println("Invalid recipient number. Use +27 followed by 9 digits.");
                        break;
                    }

                    System.out.print("Enter your message: ");
                    String text = input.nextLine();

                    if (text.trim().isEmpty()) {
                        System.out.println("Message cannot be empty.");
                        break;
                    }

                    if (text.length() > 250) {
                        System.out.println("Message exceeds 250 characters.");
                        break;
                    }

                    Message newMessage = new Message(recipient, text);

                    System.out.println("\nChoose what to do:");
                    System.out.println("1. Send Message");
                    System.out.println("2. Disregard Message");
                    System.out.println("3. Store Message to send later");
                    System.out.print("Enter your choice: ");

                    String action = input.nextLine().trim();

                    switch (action) {
                        case "1":
                            newMessage.send();
                            messages.add(newMessage);
                            messagesProcessed++;
                            System.out.println("Message successfully sent");
                            System.out.println();
                            System.out.println(newMessage.printMessages());
                            break;

                        case "2":
                            newMessage.disregard();
                            messagesProcessed++;
                            System.out.println("Press 0 to delete the message.");
                            String deleteChoice = input.nextLine().trim();

                            if (deleteChoice.equals("0")) {
                                System.out.println("Message deleted.");
                            } else {
                                messages.add(newMessage);
                                System.out.println("Message disregarded.");
                            }
                            break;

                        case "3":
                            newMessage.store();
                            messages.add(newMessage);
                            messagesProcessed++;
                            storeMessage(newMessage);
                            System.out.println("Message successfully stored");
                            System.out.println();
                            System.out.println(newMessage.printMessages());
                            break;

                        default:
                            System.out.println("Invalid choice. Message not processed.");
                            break;
                    }
                    break;

                case "2":
                    if (messages.isEmpty()) {
                        System.out.println("No messages are available.");
                    } else {
                        System.out.println("\n========== MESSAGES ==========");
                        for (Message message : messages) {
                            System.out.println(message.printMessages());
                            System.out.println("------------------------------");
                        }
                    }
                    break;

                case "3":
                    running = false;
                    System.out.println("Thank you for using QuickChat.");
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1, 2 or 3.");
                    break;
            }
        }

        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        System.out.println("Messages processed this session: " + messagesProcessed);
        input.close();
    }

    private static int readMessageLimit() {
        while (true) {
            System.out.print("How many messages would you like to enter? ");
            String value = input.nextLine().trim();

            try {
                int limit = Integer.parseInt(value);
                if (limit >= 0) {
                    return limit;
                }
                System.out.println("Enter zero or a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    public static void storeMessage(Message message) {
        try {
            String jsonObject = "{"
                    + "\"messageID\":\"" + escapeJson(message.getMessageID()) + "\","
                    + "\"messageHash\":\"" + escapeJson(message.getMessageHash()) + "\","
                    + "\"recipient\":\"" + escapeJson(message.getRecipient()) + "\","
                    + "\"message\":\"" + escapeJson(message.getMessage()) + "\","
                    + "\"status\":\"" + escapeJson(message.getStatus()) + "\""
                    + "}";

            String existingJson = "[]";
            if (Files.exists(JSON_FILE)) {
                byte[] bytes = Files.readAllBytes(JSON_FILE);
                existingJson = new String(bytes, StandardCharsets.UTF_8).trim();
            }

            if (existingJson.isEmpty()) {
                existingJson = "[]";
            }

            if (!existingJson.startsWith("[") || !existingJson.endsWith("]")) {
                System.out.println("Could not store message: messages.json is not a valid JSON array.");
                return;
            }

            String contents = existingJson.substring(1, existingJson.length() - 1).trim();
            String updatedJson;

            if (contents.isEmpty()) {
                updatedJson = "[\n" + jsonObject + "\n]";
            } else {
                updatedJson = "[\n" + contents + ",\n" + jsonObject + "\n]";
            }

            Files.write(JSON_FILE, updatedJson.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
