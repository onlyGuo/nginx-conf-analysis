package ink.icoding.nginx.entity;

/**
 * Nginx events 块配置
 * Events block configuration
 *
 * @author gsk
 */
public class NginxEventsConfItem extends NginxBlockConfItem {

    public NginxEventsConfItem(String content) {
        super(content);
    }

    /**
     * 获取工作连接数
     * Get worker connections
     * @return
     *      工作连接数
     *      worker connections
     */
    public String getWorkerConnections() {
        NginxConfItem item = getItem("worker_connections");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 设置工作连接数
     * Set worker connections
     * @param connections
     *      连接数
     *      connections count
     */
    public void setWorkerConnections(String connections) {
        items.removeIf(item -> "worker_connections".equals(item.getName()));
        addItem(new NginxInlineConfItem("worker_connections " + connections + ";"));
    }

    /**
     * 获取事件模型
     * Get event model
     * @return
     *      事件模型 (epoll, kqueue, select 等)
     *      event model (epoll, kqueue, select, etc.)
     */
    public String getUseModel() {
        NginxConfItem item = getItem("use");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 是否允许多个连接
     * Whether multi accept is enabled
     * @return
     *      是否启用 multi_accept
     *      whether multi_accept is enabled
     */
    public boolean isMultiAccept() {
        NginxConfItem item = getItem("multi_accept");
        if (item instanceof NginxInlineConfItem) {
            return "on".equalsIgnoreCase(((NginxInlineConfItem) item).getValue());
        }
        return false;
    }
}
