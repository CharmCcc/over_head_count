package entity;

import java.sql.Timestamp;

/**
 * @author chm
 * @date 2019/9/20 13:10
 * 帧的信息
 */
public class Message {
    private Timestamp timestamp; //帧的时刻
    private String param;        //传给API前的字符串

    public Message() {

    }
    public Message(Timestamp timestamp, String param) {
        this.timestamp = timestamp;
        this.param = param;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
