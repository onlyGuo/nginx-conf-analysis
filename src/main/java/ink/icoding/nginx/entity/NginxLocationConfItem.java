package ink.icoding.nginx.entity;

/**
 * Nginx location 块配置
 * Location block configuration
 *
 * 支持的格式 (supported formats):
 * - location /path { ... }
 * - location = /exact { ... }
 * - location ~ \.php$ { ... }
 * - location ~* \.(jpg|png)$ { ... }
 * - location ^~ /prefix { ... }
 * - location @named { ... }
 *
 * @author gsk
 */
public class NginxLocationConfItem extends NginxBlockConfItem {

    /**
     * 匹配修饰符 (match modifier)
     * =  : 精确匹配 (exact match)
     * ~  : 区分大小写正则 (case-sensitive regex)
     * ~* : 不区分大小写正则 (case-insensitive regex)
     * ^~ : 前缀匹配，不再检查正则 (prefix match, no regex)
     * 无  : 前缀匹配 (prefix match)
     * @  : 命名位置 (named location)
     */
    private String modifier;

    /**
     * 匹配路径/模式 (match path/pattern)
     */
    private String path;

    public NginxLocationConfItem(String content) {
        super(content);
        parseLocationValues();
    }

    /**
     * 解析 location 的修饰符和路径
     * Parse location's modifier and path
     */
    private void parseLocationValues() {
        if (values.size() == 1) {
            // location @named { ... }
            String v = values.get(0);
            if (v.startsWith("@")) {
                modifier = "@";
                path = v;
            } else {
                modifier = "";
                path = v;
            }
        } else if (values.size() >= 2) {
            modifier = values.get(0);
            path = values.get(1);
        }
    }

    /**
     * 获取匹配修饰符
     * Get match modifier
     * @return 修饰符 (modifier)
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 设置匹配修饰符
     * Set match modifier
     * @param modifier 修饰符 (modifier)
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
        if (values.isEmpty()) {
            values.add(modifier);
        } else {
            values.set(0, modifier);
        }
    }

    /**
     * 获取匹配路径/模式
     * Get match path/pattern
     * @return 路径/模式 (path/pattern)
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置匹配路径/模式
     * Set match path/pattern
     * @param path 路径/模式 (path/pattern)
     */
    public void setPath(String path) {
        this.path = path;
        if (values.size() < 2) {
            values.add(path);
        } else {
            values.set(1, path);
        }
    }

    /**
     * 是否为正则匹配
     * Whether it's regex match
     * @return 是否为正则 (is regex)
     */
    public boolean isRegex() {
        return "~".equals(modifier) || "~*".equals(modifier);
    }

    /**
     * 是否为精确匹配
     * Whether it's exact match
     * @return 是否为精确匹配 (is exact)
     */
    public boolean isExact() {
        return "=".equals(modifier);
    }

    /**
     * 是否为命名位置
     * Whether it's named location
     * @return 是否为命名位置 (is named)
     */
    public boolean isNamed() {
        return "@".equals(modifier);
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

        // location 块开始
        sb.append(prefix).append("location");
        if (modifier != null && !modifier.isEmpty()) {
            sb.append(" ").append(modifier);
        }
        if (path != null && !path.isEmpty()) {
            sb.append(" ").append(path);
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
