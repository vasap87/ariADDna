package com.lexsus.ariaddna.server;

import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by Lexsus on 01.04.2017.
 */
@Configuration
@ComponentScan("com.lexsus.ariaddna")
public class ConfigServer {

    @Autowired
    ITestAuto testAuto;


    @Bean()
    public UserService userServiceImpl(){return new UserServiceImpl();}

    @Bean
    public MyAdvancedEchoCreator webSocketCreator(){return new MyAdvancedEchoCreator(userServiceImpl());}

    @Bean
    @Scope("singleton")
    public SharedQueue<String> queue(){
        return new SharedQueue<>();
    }

    @Bean
    public MessageProcessor<String> processor() {
        return new ServerMessageProcessor();
    }

    @Bean
    public IPushConsumeServer<String> consumer() {
        return new PushConsumeServerImpl(queue(),  processor());
    }

    @Bean
    public MessageGenerator<String> generator() {
        //NOTE: spring will scan dependencies because of ComponentScan and inject them
        return new ServerMessageGenerator();
    }

    @Bean
    public IPushProduceServer<String> producer() {
        return new PushProduceServerImpl(queue(), generator());
    }

    @Bean
    public ServerMessageSystem serverSystem() {
        return new ServerMessageSystem(producer(),consumer());
    }



}


