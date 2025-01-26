package org.example.mysocialnetworkgui.domain;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Long from;
    private Long to;
    private String msg;
    private LocalDateTime date;
    private Message reply;

    public Message(Long from, Long to, String msg, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.msg = msg;
        this.date = date;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return this.getId().equals(message.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", msg='" + msg + '\'' +
                ", date=" + date +
                ", reply=" + reply +
                '}';
    }
}
