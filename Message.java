/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchat;

/**
 *
 * @author Student
 */
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class Message {
    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private String status;

    private static int totalMessagesSent = 0;
    private static int totalMessagesProcessed = 0;

    private static final SecureRandom random = new SecureRandom();
    private static final Set<String> usedIDs = new HashSet<String>();

    public Message(String recipient, String message) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.message = message;
        totalMessagesProcessed++;
        this.messageHash = createMessageHash();
        this.status = "Pending";
    }

    private String generateMessageID() {
        String id;
        do {
            // Generate a 10-digit ID without requiring newer Java versions.
            long number = 1000000000L + (Math.abs(random.nextLong()) % 9000000000L);
            id = String.valueOf(number);
        } while (!usedIDs.add(id));
        return id;
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("\\d{10}");
    }

    public boolean checkRecipientCell() {
        return recipient != null && recipient.matches("\\+27\\d{9}");
    }

    public String createMessageHash() {
        String trimmed = message.trim();
        String[] words = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");

        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 0 ? words[words.length - 1] : "";

        firstWord = firstWord.replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");
        lastWord = lastWord.replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");

        return (messageID.substring(0, 2) + ":" + totalMessagesProcessed
                + ":" + firstWord + lastWord).toUpperCase();
    }

    public String SentMessage() {
        return status;
    }

    public String printMessages() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message
                + "\nStatus: " + status;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public void send() {
        if (!status.equals("Sent")) {
            status = "Sent";
            totalMessagesSent++;
        }
    }

    public void disregard() {
        status = "Disregarded";
    }

    public void store() {
        status = "Stored";
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getStatus() {
        return status;
    }

    public static int getTotalMessagesProcessed() {
        return totalMessagesProcessed;
    }
}
