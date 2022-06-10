package com.github.onlyguo.nginx.core;

import com.github.onlyguo.nginx.entity.NginxBlockConfItem;
import com.github.onlyguo.nginx.entity.NginxCommentsConfItem;
import com.github.onlyguo.nginx.entity.NginxConfItem;
import com.github.onlyguo.nginx.entity.NginxInlineConfItem;

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
        content = content.replaceAll("(\n|\r\n)+\\s+\\{", " {");
        List<NginxConfItem> items = new LinkedList<>();
        String[] lines = content.split("\n");
        StringBuilder block = new StringBuilder();
        int inBlock = 0;
        for (String line : lines) {
            if (inBlock > 0){
                block.append(line).append("\n");
                if (line.trim().matches("^\\s*([a-z]|[A-Z]|_|[0-9]|\\s)+\\s*\\{\\s*$")) {
                    inBlock++;
                }else if (line.trim().startsWith("}")){
                    inBlock--;
                    if (inBlock == 0){
                        items.add(new NginxBlockConfItem(block.toString()));
                        block = new StringBuilder();
                    }
                }
            }else{
                if (line.trim().startsWith("#")) {
                    items.add(new NginxCommentsConfItem(line));
                }else if (line.trim().matches("^\\s*(.|\\s)+\\s*\\{\\s*$")) {
                    block.append(line).append("\n");
                    inBlock++;
                } else{
                    items.add(new NginxInlineConfItem(line));
                }
            }
        }
        return items;
    }
}
