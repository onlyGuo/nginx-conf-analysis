package ink.icoding.nginx.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Nginx upstream 块配置
 * Upstream block configuration
 *
 * 支持的格式 (supported formats):
 * - upstream backend { ... }
 * - upstream backend { hash $request_uri consistent; ... }
 * - upstream backend { least_conn; ... }
 *
 * @author gsk
 */
public class NginxUpstreamConfItem extends NginxBlockConfItem {

    /**
     * 负载均衡方法 (load balancing method)
     * 可选值: hash, ip_hash, least_conn, random
     */
    private String loadBalancingMethod;

    public NginxUpstreamConfItem(String content) {
        super(content);
        parseLoadBalancingMethod();
    }

    /**
     * 解析负载均衡方法
     * Parse load balancing method
     */
    private void parseLoadBalancingMethod() {
        for (NginxConfItem item : items) {
            String itemName = item.getName().toLowerCase();
            if (itemName.equals("ip_hash") || itemName.equals("least_conn") || itemName.equals("random")) {
                loadBalancingMethod = itemName;
                break;
            }
        }
    }

    /**
     * 获取所有 server 地址
     * Get all server addresses
     * @return
     *      server 地址列表
     *      server addresses
     */
    public List<String> getServerAddresses() {
        List<String> addresses = new ArrayList<>();
        for (NginxConfItem item : getItems("server")) {
            if (item instanceof NginxInlineConfItem) {
                addresses.add(((NginxInlineConfItem) item).getValue());
            }
        }
        return addresses;
    }

    /**
     * 添加 server
     * Add server
     * @param address
     *      server 地址 (如 127.0.0.1:8080)
     *      server address (e.g. 127.0.0.1:8080)
     * @param params
     *      额外参数 (如 weight=5, max_fails=3, fail_timeout=30s)
     *      extra params (e.g. weight=5, max_fails=3, fail_timeout=30s)
     */
    public void addServer(String address, String... params) {
        StringBuilder sb = new StringBuilder(address);
        for (String param : params) {
            sb.append(" ").append(param);
        }
        NginxInlineConfItem item = new NginxInlineConfItem("server " + sb.toString() + ";");
        addItem(item);
    }

    /**
     * 移除 server
     * Remove server
     * @param address
     *      server 地址
     *      server address
     * @return
     *      是否移除成功
     *      whether removed successfully
     */
    public boolean removeServer(String address) {
        return items.removeIf(item -> {
            if ("server".equals(item.getName()) && item instanceof NginxInlineConfItem) {
                String value = ((NginxInlineConfItem) item).getValue();
                return value != null && value.startsWith(address);
            }
            return false;
        });
    }

    /**
     * 获取负载均衡方法
     * Get load balancing method
     * @return
     *      负载均衡方法
     *      load balancing method
     */
    public String getLoadBalancingMethod() {
        return loadBalancingMethod;
    }

    /**
     * 设置负载均衡方法
     * Set load balancing method
     * @param method
     *      负载均衡方法 (ip_hash, least_conn, random)
     *      load balancing method (ip_hash, least_conn, random)
     */
    public void setLoadBalancingMethod(String method) {
        // 移除现有的负载均衡方法
        items.removeIf(item -> {
            String name = item.getName().toLowerCase();
            return name.equals("ip_hash") || name.equals("least_conn") || name.equals("random");
        });

        // 添加新的方法
        if (method != null && !method.isEmpty()) {
            items.add(0, new NginxInlineConfItem(method + ";"));
        }
        loadBalancingMethod = method;
    }

    /**
     * 获取 hash key
     * Get hash key
     * @return
     *      hash key
     */
    public String getHashKey() {
        for (NginxConfItem item : getItems("hash")) {
            if (item instanceof NginxInlineConfItem) {
                return ((NginxInlineConfItem) item).getValue();
            }
        }
        return null;
    }

    /**
     * 是否使用连接数均衡
     * Whether using least connections
     * @return
     *      是否使用 least_conn
     *      whether using least_conn
     */
    public boolean isLeastConn() {
        return "least_conn".equals(loadBalancingMethod);
    }
}
