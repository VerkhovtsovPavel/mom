package by.pv.mom.util;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Convertors {

    private Convertors(){}

    public static byte[] stringToByteArray(String value) {
        return value.getBytes(UTF_8);
    }

    public static String byteArrayToString(byte[] value) {
        return new String(value, UTF_8);
    }
}