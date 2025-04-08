package by.base.main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "telegram_chat_quality")
public class TelegramChatQuality {

	@Id
	@Column(name = "chat_id")
    private Long chatId;

    public TelegramChatQuality() {}

    public TelegramChatQuality(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

	@Override
	public String toString() {
		return "TelegramChatQuality [chatId=" + chatId + "]";
	}
    
    
}
