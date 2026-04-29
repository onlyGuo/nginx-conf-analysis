package ink.icoding.nginx.core;

import ink.icoding.nginx.entity.NginxConfItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Nginx配置文件
 * Nginx configuration file
 * @author gsk
 */
public class NginxConfig {

    private List<NginxConfItem> items = new LinkedList<>();

    public NginxConfig(List<NginxConfItem> items){
        this.items = items;
    }

    /**
     * 从配置文件中解析出所有配置项
     * Parse all configuration items from configuration file
     * @param content
     *      配置文件内容
     *      configuration file content
     * @return
     *      配置文件中的所有配置项
     *      all configuration items in the configuration file
     */
    public static NginxConfig parse(String content){
        List<NginxConfItem> items = NginxConfParser.parse(content);
        return new NginxConfig(items);
    }

    /**
     * 获取配置文件中的所有配置项
     * Get all configuration items in the configuration file
     * @return
     *      配置文件中的所有配置项
     *      all configuration items in the configuration file
     */
    public List<NginxConfItem> getItems() {
        return items;
    }

    /**
     * 将配置文件序列化为字符串
     * Serialize configuration file to string
     * @return
     *      配置文件内容
     *      configuration file content
     */
    public String toConfString() {
        StringBuilder sb = new StringBuilder();
        for (NginxConfItem item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toConfString();
    }
}
