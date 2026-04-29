package ink.icoding.nginx.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Nginx server 块配置
 * Server block configuration
 *
 * @author gsk
 */
public class NginxServerConfItem extends NginxBlockConfItem {

    public NginxServerConfItem(String content) {
        super(content);
    }

    /**
     * 获取监听端口
     * Get listen port
     * @return
     *      监听端口列表
     *      listen ports
     */
    public List<String> getListenPorts() {
        List<String> ports = new ArrayList<>();
        for (NginxConfItem item : getItems("listen")) {
            if (item instanceof NginxInlineConfItem) {
                ports.add(((NginxInlineConfItem) item).getValue());
            }
        }
        return ports;
    }

    /**
     * 添加监听端口
     * Add listen port
     * @param port
     *      端口号
     *      port number
     * @param params
     *      额外参数 (如 ssl, default_server)
     *      extra params (like ssl, default_server)
     */
    public void addListenPort(String port, String... params) {
        StringBuilder sb = new StringBuilder(port);
        for (String param : params) {
            sb.append(" ").append(param);
        }
        NginxInlineConfItem item = new NginxInlineConfItem("listen " + sb.toString() + ";");
        addItem(item);
    }

    /**
     * 获取服务器名称
     * Get server names
     * @return
     *      服务器名称列表
     *      server names
     */
    public List<String> getServerNames() {
        List<String> names = new ArrayList<>();
        for (NginxConfItem item : getItems("server_name")) {
            if (item instanceof NginxInlineConfItem) {
                String value = ((NginxInlineConfItem) item).getValue();
                if (value != null && !value.isEmpty()) {
                    String[] parts = value.split("\\s+");
                    for (String part : parts) {
                        names.add(part);
                    }
                }
            }
        }
        return names;
    }

    /**
     * 设置服务器名称
     * Set server names
     * @param names
     *      服务器名称
     *      server names
     */
    public void setServerNames(String... names) {
        // 移除现有的 server_name
        items.removeIf(item -> "server_name".equals(item.getName()));
        // 添加新的 server_name
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(name);
        }
        NginxInlineConfItem item = new NginxInlineConfItem("server_name " + sb.toString() + ";");
        addItem(item);
    }

    /**
     * 获取所有 location 配置
     * Get all location configurations
     * @return
     *      location 列表
     *      location list
     */
    public List<NginxLocationConfItem> getLocations() {
        List<NginxLocationConfItem> locations = new ArrayList<>();
        for (NginxConfItem item : items) {
            if (item instanceof NginxLocationConfItem) {
                locations.add((NginxLocationConfItem) item);
            }
        }
        return locations;
    }

    /**
     * 获取 SSL 配置
     * Get SSL configuration
     * @return
     *      是否启用 SSL
     *      whether SSL is enabled
     */
    public boolean isSslEnabled() {
        for (NginxConfItem item : getItems("listen")) {
            if (item instanceof NginxInlineConfItem) {
                String value = ((NginxInlineConfItem) item).getValue();
                if (value != null && value.contains("ssl")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取 SSL 证书路径
     * Get SSL certificate path
     * @return
     *      证书路径
     *      certificate path
     */
    public String getSslCertificate() {
        NginxConfItem item = getItem("ssl_certificate");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 获取 SSL 证书密钥路径
     * Get SSL certificate key path
     * @return
     *      证书密钥路径
     *      certificate key path
     */
    public String getSslCertificateKey() {
        NginxConfItem item = getItem("ssl_certificate_key");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 获取根目录
     * Get root directory
     * @return
     *      根目录路径
     *      root directory path
     */
    public String getRoot() {
        NginxConfItem item = getItem("root");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 设置根目录
     * Set root directory
     * @param root
     *      根目录路径
     *      root directory path
     */
    public void setRoot(String root) {
        items.removeIf(item -> "root".equals(item.getName()));
        addItem(new NginxInlineConfItem("root " + root + ";"));
    }
}
