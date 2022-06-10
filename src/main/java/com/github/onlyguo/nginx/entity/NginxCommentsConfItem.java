package com.github.onlyguo.nginx.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Nginx 注释类型的配置文件
 * Nginx comments type configuration file
 * @author gsk
 */
public class NginxCommentsConfItem implements NginxConfItem {

    private String comment;

    /**
     * 通过conf文件中的一段注释内容创建Nginx注释类型的配置文件
     * Create Nginx comments type configuration file from conf file's one line comment content
     * @param comment
     *      conf文件中的一段注释内容
     *      conf file's one line comment content
     */
    public NginxCommentsConfItem(String comment) {
        this.comment = comment;
    }

    @Override
    public String getName() {
        return comment.trim().substring(1).trim();
    }

    @Override
    public String toString() {
        return comment;
    }

    @Override
    public List<NginxConfItem> listSubItems() {
        return new LinkedList<>();
    }

    /**
     * 重新设置注释内容
     * Reset comment content
     * @param comment
     *      新的注释内容
     *      new comment content
     */
    public void setComment(String comment) {
        this.comment = this.comment.substring(0, this.comment.indexOf("#") + 1) + comment;
    }
}
