package com.hongik.pcrc.allinone.chat.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.pcrc.allinone.chat.application.service.ChatReadUseCase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRecordsView {
    private final List<ChatReadUseCase.FindChatListResult> records;

    public ChatRecordsView(List<ChatReadUseCase.FindChatListResult> records) {
        this.records = records;
    }
}
