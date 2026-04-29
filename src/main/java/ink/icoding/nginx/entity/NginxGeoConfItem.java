package ink.icoding.nginx.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nginx geo 块配置
 * Geo block configuration
 *
 * 用于地理位置映射
 * Used for geographic location mapping
 *
 * 格式 (format):
 * geo $geo {
 *     default        0;
 *     127.0.0.1      1;
 *     192.168.1.0/24 2;
 * }
 *
 * @author gsk
 */
public class NginxGeoConfItem extends NginxBlockConfItem {

    /**
     * 目标变量 (target variable)
     */
    private String targetVariable;

    public NginxGeoConfItem(String content) {
        super(content);
        parseGeoValues();
    }

    /**
     * 解析 geo 的目标变量
     * Parse geo's target variable
     */
    private void parseGeoValues() {
        if (!values.isEmpty()) {
            targetVariable = values.get(0);
        }
    }

    /**
     * 获取目标变量
     * Get target variable
     * @return
     *      目标变量
     *      target variable
     */
    public String getTargetVariable() {
        return targetVariable;
    }

    /**
     * 设置目标变量
     * Set target variable
     * @param targetVariable
     *      目标变量
     *      target variable
     */
    public void setTargetVariable(String targetVariable) {
        this.targetVariable = targetVariable;
        if (values.isEmpty()) {
            values.add(targetVariable);
        } else {
            values.set(0, targetVariable);
        }
    }

    /**
     * 获取所有 geo 条目
     * Get all geo entries
     * @return
     *      geo 条目 (address -> value)
     *      geo entries (address -> value)
     */
    public Map<String, String> getGeoEntries() {
        Map<String, String> entries = new LinkedHashMap<>();
        for (NginxConfItem item : items) {
            if (item instanceof NginxInlineConfItem) {
                NginxInlineConfItem inlineItem = (NginxInlineConfItem) item;
                String name = inlineItem.getName();
                String value = inlineItem.getValue();
                entries.put(name, value);
            }
        }
        return entries;
    }

    /**
     * 添加 geo 条目
     * Add geo entry
     * @param address
     *      IP 地址或 CIDR
     *      IP address or CIDR
     * @param value
     *      对应的值
     *      corresponding value
     */
    public void addGeoEntry(String address, String value) {
        NginxInlineConfItem item = new NginxInlineConfItem(address + " " + value + ";");
        addItem(item);
    }

    /**
     * 移除 geo 条目
     * Remove geo entry
     * @param address
     *      IP 地址或 CIDR
     *      IP address or CIDR
     * @return
     *      是否移除成功
     *      whether removed successfully
     */
    public boolean removeGeoEntry(String address) {
        return items.removeIf(item -> address.equals(item.getName()));
    }

    /**
     * 获取默认值
     * Get default value
     * @return
     *      默认值
     *      default value
     */
    public String getDefaultValue() {
        NginxConfItem item = getItem("default");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 设置默认值
     * Set default value
     * @param defaultValue
     *      默认值
     *      default value
     */
    public void setDefaultValue(String defaultValue) {
        items.removeIf(item -> "default".equals(item.getName()));
        addGeoEntry("default", defaultValue);
    }

    /**
     * 是否包含所有者 (include)
     * Whether include is used
     * @return
     *      是否包含 include
     *      whether include is used
     */
    public boolean isInclude() {
        NginxConfItem item = getItem("include");
        return item != null;
    }

    /**
     * 获取 include 路径
     * Get include path
     * @return
     *      include 路径
     *      include path
     */
    public String getIncludePath() {
        NginxConfItem item = getItem("include");
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String prefix = getIndent(indent);
        String innerPrefix = getIndent(indent + 1);

        // 注释
        if (comment != null && !comment.isEmpty()) {
            sb.append(prefix).append(comment).append("\n");
        }

        // geo 块开始
        sb.append(prefix).append("geo");
        if (targetVariable != null) {
            sb.append(" ").append(targetVariable);
        }
        sb.append(" {\n");

        // 子项
        for (NginxConfItem item : items) {
            if (item instanceof NginxEmptyLineConfItem) {
                sb.append("\n");
            } else if (item instanceof NginxBlockConfItem) {
                sb.append(((NginxBlockConfItem) item).toString(indent + 1)).append("\n");
            } else {
                sb.append(innerPrefix).append(item.toString()).append("\n");
            }
        }

        // 块结束
        sb.append(prefix).append("}");
        return sb.toString();
    }
}
