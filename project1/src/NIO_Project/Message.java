package NIO_Project;

import java.util.List;

public class Message {
    private String type; // 메시지 타입
    private String sender; // 보낸사람 ID
    private String receiver; // 받는 사용자(귓말 포함)
    private String content; // 메시지 
    private List<String> userList; // 사용자 목록 (로그온 시 추가)

    // 기본 생성자 JSON -> java 객체 변환(Gson 역직렬화)
    public Message() {}

    // Message 객체 생성자
    public Message(String type, String sender, String receiver, String content, List<String> userList) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.userList = userList;
    }

    // Gson의 내부적 java Bean 규약을 따르기 때문에 getter, setter 필요함
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
}

