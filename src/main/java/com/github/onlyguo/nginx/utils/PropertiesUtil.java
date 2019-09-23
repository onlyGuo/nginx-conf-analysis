package com.github.onlyguo.nginx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties 文件操作工具类
 * Properties file manager util
 * @author gsk
 */
public class PropertiesUtil {


    /**
     * 读取指定path下的Properties文件
     * Read properties file in the path
     * @param path
     *      Properties文件路径
     *      Properties file path
     * @return
     */
    public static Properties read(String path){
        Properties properties = new Properties();
        try (InputStream stream = PropertiesUtil.class.getClassLoader().getResourceAsStream(path)){
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}
