package com.github.onlyguo.nginx.entity;

import com.github.onlyguo.nginx.conf.Configure;

import java.util.ArrayList;
import java.util.List;

/**
 * Nginx 配置项的基础接口
 * Basic interface for Nginx configuration items
 * @author gsk
 */
public interface NginxConfItem {

    /**
     * 该配置项的文本表现形式(用于调试或写出配置文件)
     * Text representation of this configuration item (for debugging or writing out configuration files)
     * @return
     *      String
     */
    @Override
    String toString();

    /**
     * 通过泛型类型获取本对象的指定类型实例
     * Get the specified type instance of this object through a generic type
     * @param clazz
     *      需要返回的对象类型
     *      The type of object that needs to be returned
     * @param <T>
     *      对象泛型
     *      Object generic
     * @return
     *      类型转换后的实例
     *      You need instance of the type
     */
    default <T extends NginxConfItem> T typeof(Class<? extends NginxConfItem> clazz){
        if (getClass() == clazz){
            return (T) this;
        }
        throw new ClassCastException(
                String.format(Configure.MESSAGE_TEMPLATE.CONF_ITEM_UNCHECKED_CAST, toString(), clazz));
    }

    /**
     * 列出该配置项中的子配置
     * List the sub configurations in this configuration items
     * @return
     *      子配置项列表
     *      sub-configurations
     */
    List<NginxConfItem> listSubItems();

    /**
     * 查找符合指定名称或键的子配置项
     * Find sub-configuration items that match the specified name or key
     * @param confName
     *      The name of the child configuration item
     * @return
     *      子配置项列表
     *      sub-configuration
     */
    default NginxConfItem find(String confName){
        if (null == confName || confName.trim().length() == 0){
            throw new IllegalArgumentException(
                    String.format(Configure.MESSAGE_TEMPLATE.CONF_ITEM_NAME_CAN_NOT_EMPTY, confName));
        }
        for (NginxConfItem item: listSubItems()){
            if (item.getName().equalsIgnoreCase(confName)){
                return item;
            }
        }
        return null;
    }

    /**
     * 查找所有符合指定名称或键的子配置项
     * Find all sub-configuration items that match the specified name or key
     * @param confName
     *      The name of the child configuration item
     * @return
     *      子配置项列表
     */
    default List<NginxConfItem> findAll(String confName){
        if (null == confName || confName.trim().length() == 0){
            throw new IllegalArgumentException(
                    String.format(Configure.MESSAGE_TEMPLATE.CONF_ITEM_NAME_CAN_NOT_EMPTY, confName));
        }
        List<NginxConfItem> items = new ArrayList<>();
        for (NginxConfItem item: listSubItems()){
            if (item.getName().equalsIgnoreCase(confName)){
                items.add(item);
            }
        }
        return items;
    }

    /**
     * 获取该配置项的名称
     * Get the name of the configuration item
     * @return
     *      该配置项的名称
     *      The name of the configuration item
     */
    String getName();

}
