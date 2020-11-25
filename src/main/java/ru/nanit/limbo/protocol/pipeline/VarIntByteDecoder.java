package ru.nanit.limbo.protocol.pipeline;

import io.netty.util.ByteProcessor;

public class VarIntByteDecoder implements ByteProcessor {

    private int readVarInt;
    private int bytesRead;
    private DecodeResult result = DecodeResult.TOO_SHORT;

    @Override
    public boolean process(byte k) {
        readVarInt |= (k & 0x7F) << bytesRead++ * 7;
        if (bytesRead > 3) {
            result = DecodeResult.TOO_BIG;
            return false;
        }
        if ((k & 0x80) != 128) {
            result = DecodeResult.SUCCESS;
            return false;
        }
        return true;
    }

    public int getReadVarint() {
        return readVarInt;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public DecodeResult getResult() {
        return result;
    }

    public enum DecodeResult {
        SUCCESS,
        TOO_SHORT,
        TOO_BIG
    }
}