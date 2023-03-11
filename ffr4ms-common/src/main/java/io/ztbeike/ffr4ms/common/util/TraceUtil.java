package io.ztbeike.ffr4ms.common.util;

import io.ztbeike.ffr4ms.common.constant.TraceConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.StringJoiner;

public class TraceUtil {

    public static String makeInstanceId(String serviceName, String ip, Integer port) {
        StringJoiner joiner = new StringJoiner("-");
        joiner.add(serviceName).add(ip).add(port.toString());
        return joiner.toString();
    }

    public static boolean containTraceInfo(Map<String, String> headers) {
        return !StringUtils.isEmpty(headers.get(TraceConstant.TRACE_ID_HEADER))
                && !StringUtils.isEmpty(headers.get(TraceConstant.SERVICE_NAME_HEADER))
                && !StringUtils.isEmpty(headers.get(TraceConstant.TRACE_SERVICE_INSTANCE_HEADER));
    }

}
