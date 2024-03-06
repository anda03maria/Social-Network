package org.example.lab7.events;


import org.example.lab7.domain.User;

public class ChangeEvent implements Event, org.example.lab7.utils.event.Event {
    private ChangeEventType type;

    private Object data,oldData;

    public ChangeEvent(ChangeEventType type, Object data){
        this.type = type;
        this.data = data;
    }

    public ChangeEvent(ChangeEventType type, User data, User oldData){
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return this.type;
    }

    public Object getData(){
        return this.data;
    }
    public Object getOldData(){
        return this.oldData;
    }
}
