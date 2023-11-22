package server;
class Message {
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
        byte[] returnValue = new byte[infoBytes.length + msg.length];
        byte[] metaData = intsToByteArray(statusCode, startLength, endLength,amountLength);
        System.arraycopy(metaData, 0, returnValue, 0, metaData.length);
        for (int i = 0, j = 16; i < msg.length; i++, j++) {
            returnValue[j] = msg[i];
        }
        return returnValue;
    }
    public byte[] intsToByteArray(Integer... arr){
        byte[] temp = new byte[arr.length * 4];
        for(int i = 0, j = 0, k = 24; i < arr.length; i++, k-=8){
            temp[i] = (byte) (arr[j] >> k);
            if(k == 0){
                k = 24;
                j++;
            }
        }
        return temp;
    }
}