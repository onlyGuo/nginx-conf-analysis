package ink.icoding.nginx.core;

import ink.icoding.nginx.entity.NginxBlockConfItem;
import ink.icoding.nginx.entity.NginxCommentsConfItem;
import ink.icoding.nginx.entity.NginxConfItem;
import ink.icoding.nginx.entity.NginxEmptyLineConfItem;
import ink.icoding.nginx.entity.NginxInlineConfItem;

import java.util.LinkedList;
import java.util.List;

public class NginxConfParser {

    /**
     * 解析Nginx配置文件
     * Parse Nginx configuration file
     * @param content
     *      Nginx配置文件内容
     *      Nginx configuration file content
     * @return
     *      解析后的Nginx配置文件元素列表
     *      Parsed Nginx configuration file element list
     */
    public static List<NginxConfItem> parse(String content) {
        // 标准化换行符
        content = content.replace("\r\n", "\n");

        List<NginxConfItem> items = new LinkedList<>();
        String[] lines = content.split("\n");
        StringBuilder block = new StringBuilder();
        int inBlock = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();

            // 跳过空行在块内的情况
            if (trimmed.isEmpty() && inBlock > 0) {
                block.append("\n");
                continue;
            }

            if (inBlock > 0) {
                block.append(line).append("\n");

                // 检测块开始 (detect block start)
                if (!trimmed.startsWith("#") && isBlockStart(trimmed)) {
                    inBlock++;
                }
                // 检测块结束 (detect block end)
                else if (!trimmed.startsWith("#") && trimmed.equals("}")) {
                    inBlock--;
                    if (inBlock == 0) {
                        items.add(NginxBlockConfItem.createInstance(block.toString()));
                        block = new StringBuilder();
                    }
                }
            } else {
                // 空行 (empty line)
                if (trimmed.isEmpty()) {
                    items.add(new NginxEmptyLineConfItem());
                }
                // 注释行 (comment line)
                else if (trimmed.startsWith("#")) {
                    items.add(new NginxCommentsConfItem(line));
                }
                // 块开始 (block start)
                else if (isBlockStart(trimmed)) {
                    block.append(line).append("\n");
                    inBlock++;
                }
                // 普通配置行 (normal config line)
                else {
                    // 处理多行值（如 gzip_types）- 以分号结尾才算完整
                    if (!trimmed.endsWith(";") && !trimmed.endsWith("{")) {
                        // 继续读取下一行直到遇到分号
                        StringBuilder multiLine = new StringBuilder(line);
                        while (i + 1 < lines.length) {
                            i++;
                            String nextLine = lines[i];
                            String nextTrimmed = nextLine.trim();
                            // 如果下一行是空行或注释，停止合并
                            if (nextTrimmed.isEmpty() || nextTrimmed.startsWith("#")) {
                                break;
                            }
                            multiLine.append(" ").append(nextTrimmed);
                            if (nextTrimmed.endsWith(";")) {
                                break;
                            }
                        }
                        items.add(new NginxInlineConfItem(multiLine.toString()));
                    } else {
                        items.add(new NginxInlineConfItem(line));
                    }
                }
            }
        }

        // 处理未闭合的块 (handle unclosed blocks)
        if (inBlock > 0) {
            items.add(new NginxInlineConfItem(block.toString()));
        }

        return items;
    }

    /**
     * 判断是否为块开始 (check if line starts a block)
     * 支持: server {, location / {, if ($var) {, map $var $name { 等
     */
    public static boolean isBlockStart(String trimmedLine) {
        // 移除行内注释 (remove inline comments)
        String line = trimmedLine;
        int commentIndex = line.indexOf("#");
        if (commentIndex > 0) {
            line = line.substring(0, commentIndex).trim();
        }

        // 以 { 结尾 (ends with {)
        if (!line.endsWith("{")) {
            return false;
        }

        // 检查是否为注释行 (check if it's a comment)
        if (line.startsWith("#")) {
            return false;
        }

        // 检查是否为空块开始 (check for empty block like "{")
        if (line.equals("{")) {
            return true;
        }

        // 检查 { 前是否有内容 (check content before {)
        String beforeBrace = line.substring(0, line.length() - 1).trim();
        return !beforeBrace.isEmpty();
    }
}
