package server;

import java.util.ArrayList;

public class Message {
    private int statusCode;
    private byte[] msg; //max 1484
    private int startLength;
    private int endLength;
    private int amountLength;
    public Message(int statusCode, int startLength, int endLength, int amountLength, byte[] msg) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.startLength = startLength;
        this.endLength = endLength;
        this.amountLength = amountLength;
    }
    public byte[] getMsgByteArray() {
        byte[] infoBytes = new byte[16];
        //byte[] returnValue = new byte[infoBytes.length + msg.length];
        byte[] metaData = intsToByteArray(statusCode, startLength, endLength,amountLength);
        ArrayList<Byte> tempByte = new ArrayList<>();
        for(byte b : metaData) tempByte.add(b);
        for(byte b : msg) tempByte.add(b);
        byte[] returnValue = new byte[tempByte.size()];
        for(int i = 0; i < tempByte.size(); i++) returnValue[i] = tempByte.get(i);
        //System.arraycopy(metaData, 0, returnValue, 0, metaData.length);
//        for (int i = 0, j = 16; i < msg.length; i++, j++) {
//            returnValue[j] = msg[i];
//        }
        return returnValue;
    }
    public byte[] intsToByteArray(Integer... arr){
        byte[] temp = new byte[arr.length * 4];
        for (int i = 0, j = 0; i < temp.length; i += 4, j++) {
            temp[i]     = (byte) (arr[j] >> 24);
            temp[i + 1] = (byte) (arr[j] >> 16);
            temp[i + 2] = (byte) (arr[j] >> 8);
            temp[i + 3] = (byte) (arr[j] >> 0);
        }
        return temp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public byte[] getMsg() {
        return msg;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }

    public int getStartLength() {
        return startLength;
    }

    public void setStartLength(int startLength) {
        this.startLength = startLength;
    }

    public int getEndLength() {
        return endLength;
    }

    public void setEndLength(int endLength) {
        this.endLength = endLength;
    }

    public int getAmountLength() {
        return amountLength;
    }

    public void setAmountLength(int amountLength) {
        this.amountLength = amountLength;
    }
}