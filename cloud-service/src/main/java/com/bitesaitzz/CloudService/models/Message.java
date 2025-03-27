package com.bitesaitzz.CloudService.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class Message {

    @JsonCreator
    public Message(@JsonProperty("mail") String mail,
                       @JsonProperty("subject") String subject,
                       @JsonProperty("text") String text,
                   @JsonProperty("type") MessageType type) {
        this.mail = mail;
        this.subject = subject;
        this.text = text;
        this.type = type;
    }
    private String mail;
    private String subject;
    private String text;
    private MessageType type;


}

