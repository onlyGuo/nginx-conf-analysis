package ink.icoding.nginx.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Nginx 行类型的配置项
 * Nginx line type configuration item
 * @author gsk
 */
public class NginxInlineConfItem implements NginxConfItem {

    final private String name;

    private String value;

    final private String content;

    private String comment;

    public NginxInlineConfItem(String content) {
        this.content = content;
        this.comment = "";
        String trimmed = content.trim();

        // 处理行内注释 (handle inline comments)
        int commentIndex = trimmed.indexOf("#");
        String mainPart = trimmed;
        if (commentIndex > 0) {
            mainPart = trimmed.substring(0, commentIndex).trim();
        }

        // 分离 name 和 value
        int spaceIndex = mainPart.indexOf(' ');
        if (spaceIndex > 0) {
            this.name = mainPart.substring(0, spaceIndex);
            this.value = mainPart.substring(spaceIndex + 1).trim();
            // 移除末尾的分号 (remove trailing semicolon)
            if (this.value.endsWith(";")) {
                this.value = this.value.substring(0, this.value.length() - 1).trim();
            }
        } else {
            this.name = mainPart.replace(";", "").trim();
            this.value = "";
        }
    }

    /**
     * 创建带有注释的配置项 (create with comment)
     */
    public NginxInlineConfItem(String content, String comment) {
        this.content = content;
        this.comment = comment;
        String trimmed = content.trim();

        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex > 0) {
            this.name = trimmed.substring(0, spaceIndex);
            this.value = trimmed.substring(spaceIndex + 1).trim();
            if (this.value.endsWith(";")) {
                this.value = this.value.substring(0, this.value.length() - 1).trim();
            }
        } else {
            this.name = trimmed.replace(";", "").trim();
            this.value = "";
        }
    }

    @Override
    public List<NginxConfItem> listSubItems() {
        return new LinkedList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取配置项的值
     * Get configuration item's value
     * @return
     *      配置项的值
     *      configuration item's value
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置配置项的值
     * Set configuration item's value
     * @param value
     *      配置项的值
     *      configuration item's value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 追加配置项的值
     * Append configuration item's value
     * @param value
     *      配置项的值
     *      configuration item's value
     */
    public void appendValue(String value) {
        if (this.value.isEmpty()) {
            this.value = value;
        } else {
            this.value = this.value + " " + value;
        }
    }

    /**
     * 获取配置项的内容
     * Get configuration item's content
     * @return
     *      配置项的内容
     *      configuration item's content
     */
    public String getContent() {
        return content;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (value != null && !value.isEmpty()) {
            sb.append(" ").append(value);
        }
        sb.append(";");
        // 添加注释 (add comment)
        if (comment != null && !comment.isEmpty()) {
            sb.append(" ").append(comment);
        }
        return sb.toString();
    }
}
