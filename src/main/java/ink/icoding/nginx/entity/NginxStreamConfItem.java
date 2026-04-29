package ink.icoding.nginx.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Nginx stream 块配置
 * Stream block configuration
 *
 * 用于 TCP/UDP 代理配置
 * Used for TCP/UDP proxy configuration
 *
 * @author gsk
 */
public class NginxStreamConfItem extends NginxBlockConfItem {

    public NginxStreamConfItem(String content) {
        super(content);
    }

    /**
     * 获取所有 server 配置
     * Get all server configurations
     * @return
     *      server 列表
     *      server list
     */
    public List<NginxServerConfItem> getServers() {
        List<NginxServerConfItem> servers = new ArrayList<>();
        for (NginxConfItem item : items) {
            if (item instanceof NginxServerConfItem) {
                servers.add((NginxServerConfItem) item);
            }
        }
        return servers;
    }

    /**
     * 获取所有 upstream 配置
     * Get all upstream configurations
     * @return
     *      upstream 列表
     *      upstream list
     */
    public List<NginxUpstreamConfItem> getUpstreams() {
        List<NginxUpstreamConfItem> upstreams = new ArrayList<>();
        for (NginxConfItem item : items) {
            if (item instanceof NginxUpstreamConfItem) {
                upstreams.add((NginxUpstreamConfItem) item);
            }
        }
        return upstreams;
    }

    /**
     * 添加 server 块
     * Add server block
     * @param server
     *      server 配置
     *      server configuration
     */
    public void addServer(NginxServerConfItem server) {
        addItem(server);
    }
}
