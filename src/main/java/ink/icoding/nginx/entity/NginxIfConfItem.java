package ink.icoding.nginx.entity;

/**
 * Nginx if 块配置
 * If block configuration
 *
 * 用于条件判断
 * Used for conditional judgment
 *
 * 格式 (format):
 * if ($request_uri ~* "\.(gif|jpg|jpeg)$") { ... }
 * if ($http_user_agent ~ MSIE) { ... }
 *
 * @author gsk
 */
public class NginxIfConfItem extends NginxBlockConfItem {

    /**
     * 条件表达式 (condition expression)
     */
    private String condition;

    public NginxIfConfItem(String content) {
        super(content);
        parseCondition();
    }

    /**
     * 解析条件表达式
     * Parse condition expression
     */
    private void parseCondition() {
        if (!values.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String v : values) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(v);
            }
            condition = sb.toString();
            // 移除外层括号 (remove outer parentheses)
            if (condition.startsWith("(") && condition.endsWith(")")) {
                condition = condition.substring(1, condition.length() - 1).trim();
            }
        }
    }

    /**
     * 获取条件表达式
     * Get condition expression
     * @return
     *      条件表达式
     *      condition expression
     */
    public String getCondition() {
        return condition;
    }

    /**
     * 设置条件表达式
     * Set condition expression
     * @param condition
     *      条件表达式
     *      condition expression
     */
    public void setCondition(String condition) {
        this.condition = condition;
        values.clear();
        values.add("(" + condition + ")");
    }

    /**
     * 获取条件变量
     * Get condition variable
     * @return
     *      条件变量
     *      condition variable
     */
    public String getConditionVariable() {
        if (condition != null && condition.startsWith("$")) {
            int spaceIndex = condition.indexOf(' ');
            if (spaceIndex > 0) {
                return condition.substring(0, spaceIndex);
            }
            return condition;
        }
        return null;
    }

    /**
     * 获取条件操作符
     * Get condition operator
     * @return
     *      条件操作符
     *      condition operator
     */
    public String getConditionOperator() {
        if (condition != null) {
            if (condition.contains("~*")) {
                return "~*";
            } else if (condition.contains("~")) {
                return "~";
            } else if (condition.contains("=")) {
                return "=";
            } else if (condition.contains("!~*")) {
                return "!~*";
            } else if (condition.contains("!~")) {
                return "!~";
            } else if (condition.contains("!=")) {
                return "!=";
            }
        }
        return null;
    }

    /**
     * 获取条件值
     * Get condition value
     * @return
     *      条件值
     *      condition value
     */
    public String getConditionValue() {
        if (condition != null) {
            String operator = getConditionOperator();
            if (operator != null) {
                int opIndex = condition.indexOf(operator);
                if (opIndex >= 0) {
                    String afterOp = condition.substring(opIndex + operator.length()).trim();
                    // 移除引号 (remove quotes)
                    if (afterOp.startsWith("\"") && afterOp.endsWith("\"")) {
                        afterOp = afterOp.substring(1, afterOp.length() - 1);
                    }
                    return afterOp;
                }
            }
        }
        return null;
    }

    /**
     * 是否为正则条件
     * Whether it's regex condition
     * @return
     *      是否为正则
     *      whether it's regex
     */
    public boolean isRegexCondition() {
        String operator = getConditionOperator();
        return "~".equals(operator) || "~*".equals(operator) || "!~".equals(operator) || "!~*".equals(operator);
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

        // if 块开始
        sb.append(prefix).append("if");
        if (condition != null) {
            sb.append(" (").append(condition).append(")");
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
