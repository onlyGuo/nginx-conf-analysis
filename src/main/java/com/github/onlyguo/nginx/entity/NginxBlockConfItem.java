package com.github.onlyguo.nginx.entity;

import com.github.onlyguo.nginx.core.NginxConfParser;

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
    final private String name;

    private List<String> value = new LinkedList<>();

    private List<NginxConfItem> items = new LinkedList<>();

    public NginxBlockConfItem(String content) {
        String[] lines = content.split("\n");
        String[] split = lines[0].trim().split("\\s+");
        name = split[0];
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                if (split[i].trim().equals("{")) {
                    continue;
                }
                value.add(split[i]);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < lines.length - 1; i++) {
            sb.append(lines[i]).append("\n");
        }
        items.addAll(NginxConfParser.parse(sb.toString()));
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
     * Get block's value
     * @return
     *      块的值
     *      block's value
     */
    public List<String> getValue() {
        return value;
    }

    /**
     * 设置块的值
     * Set block's value
     * @param value
     *      块的值
     *      block's value
     */
    public void setValue(List<String> value) {
        this.value = value;
    }

    /**
     * 追加配置项的值
     * Append configuration item's value
     * @param value
     *      配置项的值
     *      configuration item's value
     */
    public void appendValue(String... value) {
        Collections.addAll(this.value, value);
    }
}
