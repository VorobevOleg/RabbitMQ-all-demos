package ru.vorobev.rabbitmq.console.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class RoutingProducerApp {
    private static final String EXCHANGE_NAME = "topic_exchange";
    private static final String DEFAULT_ROUTING_KEY = "prog";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            String routingKey;
            String message;
            String input;

            Scanner console = new Scanner(System.in);

            System.out.println("Enter message ('stop' from exit app):");
            input = console.nextLine();

            while (!input.equals("stop")) {
                String key = input.split(" ")[0];
                routingKey = DEFAULT_ROUTING_KEY + "." + key;
                message = input.substring(key.length() + 1);

                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + routingKey + "' : '" + message + "'");

                System.out.println("Enter message ('stop' from exit app):");
                input = console.nextLine();
            }

        }
    }

}