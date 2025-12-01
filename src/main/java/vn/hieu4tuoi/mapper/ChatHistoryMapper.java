package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;
import vn.hieu4tuoi.dto.request.chatbot.ChatRequest;
import vn.hieu4tuoi.dto.respone.chat.ChatHistoryResponse;
import vn.hieu4tuoi.model.ChatHistory;

@Mapper(componentModel = "spring")
public interface ChatHistoryMapper {
    ChatHistory toEntity(ChatRequest request);
    ChatHistoryResponse toDto(ChatHistory chatHistory);
}