package io.ztbeike.ffr4ms.gateway.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * 标记实例优先和故障的请求实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplaceInstanceDTO {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 故障实例IP
     */
    private String downInstanceHost;

    /**
     * 故障实例端口
     */
    private Integer downInstancePort;

    /**
     * 优先实例IP
     */
    private String replaceInstanceHost;

    /**
     * 优先实例端口
     */
    private Integer replaceInstancePort;

    /**
     * 判断是否能够标记故障实例
     * @return true or false
     */
    public boolean validForDownInstance() {
        return !StringUtils.isEmpty(this.downInstanceHost) && this.downInstancePort != null;
    }

    /**
     * 判断是否能够标记优先实例
     * @return true or false
     */
    public boolean validForReplaceInstance() {
        return !StringUtils.isEmpty(this.replaceInstanceHost) && this.replaceInstancePort != null;
    }

}
