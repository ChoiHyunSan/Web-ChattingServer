package com.ll.webchattingserver.api.dto.request.chat;

import lombok.Getter;

@Getter
public class Message {
    private String from;
    private String to;
    private String message;
}
