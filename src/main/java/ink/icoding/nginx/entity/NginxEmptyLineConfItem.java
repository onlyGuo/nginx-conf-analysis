package ink.icoding.nginx.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Nginx 空行类型的配置项
 * Nginx empty line type configuration item
 * @author gsk
 */
public class NginxEmptyLineConfItem implements NginxConfItem {

    @Override
    public String toString() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<NginxConfItem> listSubItems() {
        return new LinkedList<>();
    }
}
