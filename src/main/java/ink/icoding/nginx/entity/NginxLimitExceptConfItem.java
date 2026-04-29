package ink.icoding.nginx.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Nginx limit_except 块配置
 * Limit except block configuration
 *
 * 用于限制 HTTP 方法
 * Used for limiting HTTP methods
 *
 * 格式 (format):
 * limit_except GET POST {
 *     allow 192.168.1.0/24;
 *     deny all;
 * }
 *
 * @author gsk
 */
public class NginxLimitExceptConfItem extends NginxBlockConfItem {

    public NginxLimitExceptConfItem(String content) {
        super(content);
    }

    /**
     * 获取允许的 HTTP 方法
     * Get allowed HTTP methods
     * @return
     *      HTTP 方法列表
     *      HTTP methods list
     */
    public List<String> getAllowedMethods() {
        return new ArrayList<>(values);
    }

    /**
     * 设置允许的 HTTP 方法
     * Set allowed HTTP methods
     * @param methods
     *      HTTP 方法
     *      HTTP methods
     */
    public void setAllowedMethods(String... methods) {
        values.clear();
        for (String method : methods) {
            values.add(method);
        }
    }

    /**
     * 添加允许的 HTTP 方法
     * Add allowed HTTP method
     * @param method
     *      HTTP 方法
     *      HTTP method
     */
    public void addAllowedMethod(String method) {
        values.add(method);
    }

    /**
     * 获取 allow 规则
     * Get allow rules
     * @return
     *      allow 地址列表
     *      allow addresses
     */
    public List<String> getAllowRules() {
        List<String> rules = new ArrayList<>();
        for (NginxConfItem item : getItems("allow")) {
            if (item instanceof NginxInlineConfItem) {
                rules.add(((NginxInlineConfItem) item).getValue());
            }
        }
        return rules;
    }

    /**
     * 添加 allow 规则
     * Add allow rule
     * @param address
     *      IP 地址或 CIDR
     *      IP address or CIDR
     */
    public void addAllowRule(String address) {
        NginxInlineConfItem item = new NginxInlineConfItem("allow " + address + ";");
        addItem(item);
    }

    /**
     * 获取 deny 规则
     * Get deny rules
     * @return
     *      deny 地址列表
     *      deny addresses
     */
    public List<String> getDenyRules() {
        List<String> rules = new ArrayList<>();
        for (NginxConfItem item : getItems("deny")) {
            if (item instanceof NginxInlineConfItem) {
                rules.add(((NginxInlineConfItem) item).getValue());
            }
        }
        return rules;
    }

    /**
     * 添加 deny 规则
     * Add deny rule
     * @param address
     *      IP 地址或 CIDR (或 all)
     *      IP address or CIDR (or all)
     */
    public void addDenyRule(String address) {
        NginxInlineConfItem item = new NginxInlineConfItem("deny " + address + ";");
        addItem(item);
    }

    /**
     * 是否允许指定方法
     * Whether method is allowed
     * @param method
     *      HTTP 方法
     *      HTTP method
     * @return
     *      是否允许
     *      whether allowed
     */
    public boolean isMethodAllowed(String method) {
        for (String allowedMethod : values) {
            if (allowedMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
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

        // limit_except 块开始
        sb.append(prefix).append("limit_except");
        if (!values.isEmpty()) {
            for (String v : values) {
                sb.append(" ").append(v);
            }
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
