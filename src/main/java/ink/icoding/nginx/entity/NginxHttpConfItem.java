package ink.icoding.nginx.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Nginx http 块配置
 * HTTP block configuration
 *
 * @author gsk
 */
public class NginxHttpConfItem extends NginxBlockConfItem {

    public NginxHttpConfItem(String content) {
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
     * 获取 MIME 类型配置
     * Get MIME types configuration
     * @return
     *      types 块
     *      types block
     */
    public NginxTypesConfItem getTypes() {
        for (NginxConfItem item : items) {
            if (item instanceof NginxTypesConfItem) {
                return (NginxTypesConfItem) item;
            }
        }
        return null;
    }

    /**
     * 获取默认类型
     * Get default type
     * @return
     *      默认类型
     *      default type
     */
    public String getDefaultType() {
        NginxConfItem item = getItem("default_type");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 获取 keepalive 超时时间
     * Get keepalive timeout
     * @return
     *      超时时间
     *      timeout value
     */
    public String getKeepaliveTimeout() {
        NginxConfItem item = getItem("keepalive_timeout");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 获取 gzip 配置
     * Get gzip configuration
     * @return
     *      是否启用 gzip
     *      whether gzip is enabled
     */
    public boolean isGzipEnabled() {
        NginxConfItem item = getItem("gzip");
        if (item instanceof NginxInlineConfItem) {
            return "on".equalsIgnoreCase(((NginxInlineConfItem) item).getValue());
        }
        return false;
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

    /**
     * 移除 server 块
     * Remove server block
     * @param server
     *      server 配置
     *      server configuration
     * @return
     *      是否移除成功
     *      whether removed successfully
     */
    public boolean removeServer(NginxServerConfItem server) {
        return removeItem(server);
    }
}
