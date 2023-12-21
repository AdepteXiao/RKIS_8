package ru.ad.lab8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.ad.lab8.messages.Receiver;

@SpringBootApplication
public class Lab8Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Lab8Application.class, args);
		Receiver messageReceiver = context.getBean(Receiver.class);
		messageReceiver.startReceivingMessages();
	}

}
