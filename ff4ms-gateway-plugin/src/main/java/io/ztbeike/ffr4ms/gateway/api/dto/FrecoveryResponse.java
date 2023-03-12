package io.ztbeike.ffr4ms.gateway.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 统一API返回响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrecoveryResponse {

    private Integer code;

    private String message;

    public FrecoveryResponse setCode(Integer code) {
        this.code = code;
        return this;
    }

    public FrecoveryResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public static FrecoveryResponse ok() {
        return new FrecoveryResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    }

    public static FrecoveryResponse bad() {
        return new FrecoveryResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    public static FrecoveryResponse error() {
        return new FrecoveryResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
