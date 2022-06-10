package com.github.onlyguo.nginx.conf;

import com.github.onlyguo.nginx.utils.PropertiesUtil;

import java.util.Properties;

/**
 * 配置文件公共类
 * Common config content
 * @author gsk
 */
public interface Configure {

    Properties PROPERTIES = PropertiesUtil.read("conf.properties");

    /**
     * 程序或界面显示的语言
     * Language for application or UI display
     */
    String LANGUAGE = PROPERTIES.getOrDefault("language", "zh_cn").toString();

    /**
     * Nginx配置文件路径
     * Nginx config file path
     */
    String CONF_DIR = PROPERTIES.getProperty("conf.path");

    /**
     * 不同语言的消息
     * Message expression configuration in different languages
     */
    interface MESSAGE_TEMPLATE{
        Properties PROPERTIES = PropertiesUtil.read(LANGUAGE + ".properties");

        String CONF_ITEM_UNCHECKED_CAST = PROPERTIES.getProperty("CONF_ITEM_UNCHECKED_CAST");
        String CONF_ITEM_NOF_FOUNT_NAME = PROPERTIES.getProperty("CONF_ITEM_NOF_FOUNT_NAME");
        String CONF_ITEM_NAME_CAN_NOT_EMPTY = PROPERTIES.getProperty("CONF_ITEM_NAME_CAN_NOT_EMPTY");
    }


}
