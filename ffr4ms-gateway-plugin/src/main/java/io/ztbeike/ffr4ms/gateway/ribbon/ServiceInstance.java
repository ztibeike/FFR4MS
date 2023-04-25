package io.ztbeike.ffr4ms.gateway.ribbon;

import lombok.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 *
 */
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "priorTTL")
public class ServiceInstance {

    /**
     * 实例名称
     */
    @NonNull
    private String serviceName;

    /**
     * 实例IP
     */
    private String host;

    /**
     * 实例端口
     */
    private Integer port;

    /**
     * 实例状态
     */
    private ServiceInstanceStatus status;

    /**
     * 优先选取次数
     */
    private volatile int priorTTL;

    /**
     * 优先实例被优先选取次数的阈值
     */
    public static final int INSTANCE_PRIOR_TTL_THRESHOLD = 10;

    private static final Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CAS自旋减1
     * @return 剩余优先选取次数
     */
    public int decreasePriorCount() {
        try {
            long offset = unsafe.objectFieldOffset(ServiceInstance.class.getDeclaredField("priorTTL"));
            return unsafe.getAndAddInt(this, offset, -1) - 1; // Unsafe.getAndAddInt返回的是修改前的值, 所以需要减1即为CAS修改后的值
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return this.priorTTL;
    }
}
