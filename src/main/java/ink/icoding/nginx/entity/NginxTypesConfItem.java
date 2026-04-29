package ink.icoding.nginx.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nginx types 块配置
 * Types block configuration
 *
 * 用于 MIME 类型映射
 * Used for MIME type mapping
 *
 * 格式 (format):
 * types {
 *     text/html                             html htm shtml;
 *     text/css                              css;
 *     application/javascript                js;
 *     image/jpeg                            jpeg jpg;
 * }
 *
 * @author gsk
 */
public class NginxTypesConfItem extends NginxBlockConfItem {

    public NginxTypesConfItem(String content) {
        super(content);
    }

    /**
     * 获取所有 MIME 类型映射
     * Get all MIME type mappings
     * @return
     *      MIME 类型映射 (mime type -> extensions)
     *      MIME type mappings (mime type -> extensions)
     */
    public Map<String, String> getMimeTypes() {
        Map<String, String> mimeTypes = new LinkedHashMap<>();
        for (NginxConfItem item : items) {
            if (item instanceof NginxInlineConfItem) {
                NginxInlineConfItem inlineItem = (NginxInlineConfItem) item;
                mimeTypes.put(inlineItem.getName(), inlineItem.getValue());
            }
        }
        return mimeTypes;
    }

    /**
     * 根据扩展名获取 MIME 类型
     * Get MIME type by extension
     * @param extension
     *      文件扩展名
     *      file extension
     * @return
     *      MIME 类型
     *      MIME type
     */
    public String getMimeTypeByExtension(String extension) {
        for (NginxConfItem item : items) {
            if (item instanceof NginxInlineConfItem) {
                NginxInlineConfItem inlineItem = (NginxInlineConfItem) item;
                String value = inlineItem.getValue();
                if (value != null) {
                    String[] extensions = value.split("\\s+");
                    for (String ext : extensions) {
                        if (ext.equalsIgnoreCase(extension)) {
                            return inlineItem.getName();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据 MIME 类型获取扩展名
     * Get extensions by MIME type
     * @param mimeType
     *      MIME 类型
     *      MIME type
     * @return
     *      扩展名列表
     *      extensions list
     */
    public String getExtensionsByMimeType(String mimeType) {
        NginxConfItem item = getItem(mimeType);
        if (item instanceof NginxInlineConfItem) {
            return ((NginxInlineConfItem) item).getValue();
        }
        return null;
    }

    /**
     * 添加 MIME 类型
     * Add MIME type
     * @param mimeType
     *      MIME 类型
     *      MIME type
     * @param extensions
     *      文件扩展名
     *      file extensions
     */
    public void addMimeType(String mimeType, String extensions) {
        NginxInlineConfItem item = new NginxInlineConfItem(mimeType + " " + extensions + ";");
        addItem(item);
    }

    /**
     * 移除 MIME 类型
     * Remove MIME type
     * @param mimeType
     *      MIME 类型
     *      MIME type
     * @return
     *      是否移除成功
     *      whether removed successfully
     */
    public boolean removeMimeType(String mimeType) {
        return items.removeIf(item -> mimeType.equals(item.getName()));
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

        // types 块开始
        sb.append(prefix).append("types {\n");

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
