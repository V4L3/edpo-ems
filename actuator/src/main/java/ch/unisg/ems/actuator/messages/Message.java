package ch.unisg.ems.actuator.messages;

import ch.unisg.ems.actuator.model.ConsumerShutdownRequest;

import java.util.Date;
import java.util.UUID;

public class Message<T> {

    // Cloud Events compliant
    private String id = UUID.randomUUID().toString(); // unique id of this message
    private Date time = new Date();
    private T data;

    // Extension attributes

    public Message(T payload) {
        this.data = payload;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

