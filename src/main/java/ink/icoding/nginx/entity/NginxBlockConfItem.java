package ink.icoding.nginx.entity;

import ink.icoding.nginx.core.NginxConfParser;

import java.util.*;

/**
 * Nginx块类型的配置文件
 * Nginx block type configuration file
 * @author gsk
 */
public class NginxBlockConfItem implements NginxConfItem {

    /**
     * 块名称
     * Block name
     */
    final protected String name;

    /**
     * 块的值/参数
     * Block's values/parameters
     */
    protected List<String> values = new LinkedList<>();

    /**
     * 子配置项列表
     * List of sub-configuration items
     */
    protected List<NginxConfItem> items = new LinkedList<>();

    /**
     * 块开始前的注释
     * Comments before block
     */
    protected String comment = "";

    /**
     * 原始缩进
     * Original indentation
     */
    protected String indentation = "";

    public NginxBlockConfItem(String content) {
        String[] lines = content.split("\n");
        String firstLine = lines[0].trim();

        // 提取缩进 (extract indentation)
        int indentLen = lines[0].length() - lines[0].trim().length();
        if (indentLen > 0) {
            indentation = lines[0].substring(0, indentLen);
        }

        // 分离注释 (separate comments)
        int commentIndex = firstLine.indexOf("#");
        if (commentIndex > 0) {
            comment = firstLine.substring(commentIndex);
            firstLine = firstLine.substring(0, commentIndex).trim();
        }

        // 解析名称和值 (parse name and values)
        String[] split = firstLine.split("\\s+");
        name = split[0];
        for (int i = 1; i < split.length; i++) {
            if (!split[i].equals("{")) {
                values.add(split[i]);
            }
        }

        // 解析子项 (parse sub-items)
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < lines.length - 1; i++) {
            sb.append(lines[i]).append("\n");
        }
        items.addAll(NginxConfParser.parse(sb.toString()));
    }

    /**
     * 创建指定类型的子类实例 (create instance by type)
     * @param content
     *      原始内容
     *      original content
     * @return
     *      对应类型的实例
     *      instance of corresponding type
     */
    public static NginxBlockConfItem createInstance(String content) {
        String firstLine = content.split("\n")[0].trim();
        String blockName = firstLine.split("\\s+")[0].toLowerCase();

        return switch (blockName) {
            case "http" -> new NginxHttpConfItem(content);
            case "server" -> new NginxServerConfItem(content);
            case "location" -> new NginxLocationConfItem(content);
            case "upstream" -> new NginxUpstreamConfItem(content);
            case "events" -> new NginxEventsConfItem(content);
            case "stream" -> new NginxStreamConfItem(content);
            case "map" -> new NginxMapConfItem(content);
            case "geo" -> new NginxGeoConfItem(content);
            case "if" -> new NginxIfConfItem(content);
            case "types" -> new NginxTypesConfItem(content);
            case "limit_except" -> new NginxLimitExceptConfItem(content);
            default -> new NginxBlockConfItem(content);
        };
    }

    @Override
    public List<NginxConfItem> listSubItems() {
        return items;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取块的值
     * Get block's values
     * @return
     *      块的值列表
     *      block's values
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * 获取第一个值
     * Get first value
     * @return
     *      第一个值
     *      first value
     */
    public String getFirstValue() {
        return values.isEmpty() ? null : values.get(0);
    }

    /**
     * 设置块的值
     * Set block's values
     * @param values
     *      块的值列表
     *      block's values
     */
    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * 追加配置项的值
     * Append configuration item's value
     * @param values
     *      配置项的值
     *      configuration item's values
     */
    public void appendValues(String... values) {
        Collections.addAll(this.values, values);
    }

    /**
     * 获取注释
     * Get comment
     * @return
     *      注释内容
     *      comment content
     */
    public String getComment() {
        return comment;
    }

    /**
     * 设置注释
     * Set comment
     * @param comment
     *      注释内容
     *      comment content
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 添加子配置项
     * Add sub-configuration item
     * @param item
     *      子配置项
     *      sub-configuration item
     */
    public void addItem(NginxConfItem item) {
        items.add(item);
    }

    /**
     * 移除子配置项
     * Remove sub-configuration item
     * @param item
     *      子配置项
     *      sub-configuration item
     * @return
     *      是否移除成功
     *      whether removed successfully
     */
    public boolean removeItem(NginxConfItem item) {
        return items.remove(item);
    }

    /**
     * 根据名称查找子配置项
     * Find sub-configuration item by name
     * @param name
     *      配置项名称
     *      configuration item name
     * @return
     *      找到的配置项
     *      found item
     */
    public NginxConfItem getItem(String name) {
        for (NginxConfItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据名称查找所有子配置项
     * Find all sub-configuration items by name
     * @param name
     *      配置项名称
     *      configuration item name
     * @return
     *      找到的配置项列表
     *      found items
     */
    public List<NginxConfItem> getItems(String name) {
        List<NginxConfItem> result = new ArrayList<>();
        for (NginxConfItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * 带缩进的toString方法
     * toString with indentation
     * @param indent
     *      缩进级别
     *      indentation level
     * @return
     *      格式化的字符串
     *      formatted string
     */
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String prefix = getIndent(indent);
        String innerPrefix = getIndent(indent + 1);

        // 注释 (comment)
        if (comment != null && !comment.isEmpty()) {
            sb.append(prefix).append(comment).append("\n");
        }

        // 块开始 (block start)
        sb.append(prefix).append(name);
        if (!values.isEmpty()) {
            for (String v : values) {
                sb.append(" ").append(v);
            }
        }
        sb.append(" {\n");

        // 子项 (sub-items)
        for (NginxConfItem item : items) {
            if (item instanceof NginxEmptyLineConfItem) {
                sb.append("\n");
            } else if (item instanceof NginxBlockConfItem) {
                sb.append(((NginxBlockConfItem) item).toString(indent + 1)).append("\n");
            } else {
                sb.append(innerPrefix).append(item.toString()).append("\n");
            }
        }

        // 块结束 (block end)
        sb.append(prefix).append("}");
        return sb.toString();
    }

    /**
     * 生成缩进字符串
     * Generate indentation string
     */
    protected String getIndent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }
}
