package com.github.onlyguo.nginx;

import com.github.onlyguo.nginx.conf.Configure;
import com.github.onlyguo.nginx.core.NginxConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainTest {
    public static void main(String[] args) throws IOException {
        NginxConfig parse = NginxConfig.parse(Files.readString(Path.of(Configure.CONF_DIR), StandardCharsets.UTF_8));
        System.out.printf("%s", parse.getItems());
    }

}
