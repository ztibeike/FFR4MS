package io.ztbeike.ffr4ms.common.util;

import java.util.StringJoiner;

public class TraceUtil {

    public static String makeInstanceId(String serviceName, String ip, Integer port) {
        StringJoiner joiner = new StringJoiner("-");
        joiner.add(serviceName).add(ip).add(port.toString());
        return joiner.toString();
    }

}
