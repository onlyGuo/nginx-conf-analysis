package ink.icoding.nginx.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nginx map 块配置
 * Map block configuration
 *
 * 用于变量映射
 * Used for variable mapping
 *
 * 格式 (format):
 * map $uri $new {
 *     default        http://www.domain.com/home ;
 *     /aa            http://www.domain.com/bb ;
 *     /bb            http://www.domain.com/cc ;
 * }
 *
 * @author gsk
 */
public class NginxMapConfItem extends NginxBlockConfItem {

    /**
     * 源变量 (source variable)
     */
    private String sourceVariable;

    /**
     * 目标变量 (target variable)
     */
    private String targetVariable;

    public NginxMapConfItem(String content) {
        super(content);
        parseMapValues();
    }

    /**
     * 解析 map 的源变量和目标变量
     * Parse map's source and target variables
     */
    private void parseMapValues() {
        if (values.size() >= 2) {
            sourceVariable = values.get(0);
            targetVariable = values.get(1);
        }
    }

    /**
     * 获取源变量
     * Get source variable
     * @return
     *      源变量
     *      source variable
     */
    public String getSourceVariable() {
        return sourceVariable;
    }

    /**
     * 设置源变量
     * Set source variable
     * @param sourceVariable
     *      源变量
     *      source variable
     */
    public void setSourceVariable(String sourceVariable) {
        this.sourceVariable = sourceVariable;
        if (values.size() < 1) {
            values.add(sourceVariable);
        } else {
            values.set(0, sourceVariable);
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
        if (values.size() < 2) {
            values.add(targetVariable);
        } else {
            values.set(1, targetVariable);
        }
    }

    /**
     * 获取所有映射条目
     * Get all map entries
     * @return
     *      映射条目 (key -> value)
     *      map entries (key -> value)
     */
    public Map<String, String> getMapEntries() {
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
     * 添加映射条目
     * Add map entry
     * @param key
     *      源值
     *      source value
     * @param value
     *      目标值
     *      target value
     */
    public void addMapEntry(String key, String value) {
        NginxInlineConfItem item = new NginxInlineConfItem(key + " " + value + ";");
        addItem(item);
    }

    /**
     * 移除映射条目
     * Remove map entry
     * @param key
     *      源值
     *      source value
     * @return
     *      是否移除成功
     *      whether removed successfully
     */
    public boolean removeMapEntry(String key) {
        return items.removeIf(item -> key.equals(item.getName()));
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
        addMapEntry("default", defaultValue);
    }

    /**
     * 是否包含主机名映射
     * Whether hostnames is included
     * @return
     *      是否包含 hostnames
     *      whether hostnames is included
     */
    public boolean isHostnames() {
        NginxConfItem item = getItem("hostnames");
        return item != null;
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

        // map 块开始
        sb.append(prefix).append("map");
        if (sourceVariable != null) {
            sb.append(" ").append(sourceVariable);
        }
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
