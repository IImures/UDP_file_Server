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
        this.startLength = startLength;   ln
        this.endLength = endLength;
        this.amountLength = amountLength;
    }

    public byte[] getMsgByteArray() {
        byte[] infoBytes = new byte[16];
        infoBytes[0] = (byte) ((statusCode & 0xFF000000) >> 24);
        infoBytes[1] = (byte) ((statusCode & 0x00FF0000) >> 16);
        infoBytes[2] = (byte) ((statusCode & 0x0000FF00) >> 8);
        infoBytes[3] = (byte) ((statusCode & 0x000000FF) >> 0);

        infoBytes[4] = (byte) ((startLength & 0xFF000000) >> 24);
        infoBytes[5] = (byte) ((startLength & 0x00FF0000) >> 16);
        infoBytes[6] = (byte) ((startLength & 0x0000FF00) >> 8);
        infoBytes[7] = (byte) ((startLength & 0x000000FF) >> 0);

        infoBytes[8] = (byte) ((endLength & 0xFF000000) >> 24);
        infoBytes[9] = (byte) ((endLength & 0x00FF0000) >> 16);
        infoBytes[10] = (byte) ((endLength & 0x0000FF00) >> 8);
        infoBytes[11] = (byte) ((endLength & 0x000000FF) >> 0);

        infoBytes[12] = (byte) ((amountLength & 0xFF000000) >> 24);
        infoBytes[13] = (byte) ((amountLength & 0x00FF0000) >> 16);
        infoBytes[14] = (byte) ((amountLength & 0x0000FF00) >> 8);
        infoBytes[15] = (byte) ((amountLength & 0x000000FF) >> 0);

        byte[] returnValue = new byte[infoBytes.length + msg.length];

        for (int i = 0, j = 16; i < msg.length; i++, j++) {
            returnValue[j] = msg[i];
        }

        System.out.println((((int)(infoBytes[0]) << 24) & 0xFF000000) |
                (((int)(infoBytes[1]) << 16) & 0xFF0000) |
                (((int)(infoBytes[2]) << 8) & 0xFF00) |
                ((int)(infoBytes[3]) & 0xFF));

        System.out.println((((int)(infoBytes[4]) << 24) & 0xFF000000) |
                (((int)(infoBytes[5]) << 16) & 0xFF0000) |
                (((int)(infoBytes[6]) << 8) & 0xFF00) |
                ((int)(infoBytes[7]) & 0xFF));

        System.out.println((((int)(infoBytes[8]) << 24) & 0xFF000000) |
                (((int)(infoBytes[9]) << 16) & 0xFF0000) |
                (((int)(infoBytes[10]) << 8) & 0xFF00) |
                ((int)(infoBytes[11]) & 0xFF));

        System.out.println((((int)(infoBytes[12]) << 24) & 0xFF000000) |
                (((int)(infoBytes[13]) << 16) & 0xFF0000) |
                (((int)(infoBytes[14]) << 8) & 0xFF00) |
                ((int)(infoBytes[15]) & 0xFF));

        return returnValue;
    }
}