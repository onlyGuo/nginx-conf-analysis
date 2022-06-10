package com.github.onlyguo.nginx.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Nginx 行类型的配置项
 * Nginx line type configuration item
 * @author gsk
 */
public class NginxInlineConfItem implements NginxConfItem{

    final private String name;

    private List<String> value = new LinkedList<>();

    final private String content;

    public NginxInlineConfItem(String content) {
        this.content = content;
        // 去除空格，并用空字符分割
        // Remove spaces, and split by empty character
        String[] items = content.trim().split("\\s+");
        if (items.length >= 2) {
            this.name = items[0];
            for (int i = 1; i < items.length; i++) {
                if (items[i].endsWith(";")) {
                    items[i] = items[i].substring(0, items[i].length() - 1).trim();
                }
                this.value.add(items[i]);
            }
        } else {
            this.name = items[0];
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
    public List<String> getValue() {
        return value;
    }

    /**
     * 设置配置项的值
     * Set configuration item's value
     * @param value
     *      配置项的值
     *      configuration item's value
     */
    public void setValue(List<String> value) {
        this.value = value;
    }

    /**
     * 追加配置项的值
     * @param value
     *      配置项的值
     *      configuration item's value
     */
    public void appendValue(String... value){
        Collections.addAll(this.value, value);
    }

    /**
     * 获取配置项的内容
     * Get configuration item's content
     * @return
     *      配置项的内容
     *      configuration item's content
     */
    public String getContent() {
        // 取到首个有效字符的位置，忽略空格和空字符以及制表符
        // Get the position of the first valid character, ignore spaces and empty characters and tab characters
        int index = 0;
        while (index < content.length() && (content.charAt(index) == ' ' || content.charAt(index) == '\t')) {
            index++;
        }

        return content.substring(0, index) + name + " " + value + ";";
    }
}
