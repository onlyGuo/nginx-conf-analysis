package ink.icoding.nginx;

import ink.icoding.nginx.core.NginxConfig;
import ink.icoding.nginx.entity.NginxBlockConfItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainTest {
    public static void main(String[] args) throws IOException {
        String s = Files.readString(Path.of("nginx.conf"), StandardCharsets.UTF_8);
        NginxConfig parse = NginxConfig.parse(s);
//        NginxBlockConfItem instance = NginxBlockConfItem.createInstance(s);
        System.out.printf("%s", parse);
//        System.out.println(instance);
    }

}
