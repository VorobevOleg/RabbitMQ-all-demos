package ru.vorobev.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class RoutingConsumerApp {
    private static final String EXCHANGE_NAME = "topic_exchange";
    private static final String DEFAULT_ROUTING_KEY = "prog";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = channel.queueDeclare().getQueue();
        System.out.println("QUEUE NAME: " + queueName);

        Scanner console = new Scanner(System.in);

        System.out.println(" [#] Enter topic to subscribe (set_topic 'name'):");
        String input = console.nextLine();
        String inputKey = "java";
        if (input.split(" ")[0].equals("set_topic")) {
            inputKey = input.split(" ")[1];
        } else {
            System.out.println("Wrong command! Set default topic: 'java'");
        }

        String routingKey = DEFAULT_ROUTING_KEY + "." + inputKey;
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

        System.out.println(" [*] Waiting for messages with routing key (" + routingKey + "):");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

        while (!input.equals("stop")) {
            System.out.println(" [#] Enter to subscribe - set_topic 'name'" + "\n" +
                    " [#] Enter to unsubscribe - del_topic 'name'");
            input = console.nextLine();

            if (input.split(" ")[0].equals("set_topic")) {
                inputKey = input.split(" ")[1];
                routingKey = DEFAULT_ROUTING_KEY + "." + inputKey;
                channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
            } else if (input.split(" ")[0].equals("del_topic")){
                inputKey = input.split(" ")[1];
                routingKey = DEFAULT_ROUTING_KEY + "." + inputKey;
                channel.queueUnbind(queueName, EXCHANGE_NAME, routingKey);
            } else {
                System.out.println("Wrong command! Set default topic: 'java'");
            }
        }
    }
}
