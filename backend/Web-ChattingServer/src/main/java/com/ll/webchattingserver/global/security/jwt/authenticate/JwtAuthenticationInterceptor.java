package com.ll.webchattingserver.global.security.jwt.authenticate;
import com.ll.webchattingserver.socket.WssAccessorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements ChannelInterceptor {

    private final WssAccessorManager accessorManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        accessorManager.checkMessageCommand(message);
       return message;
    }
}
