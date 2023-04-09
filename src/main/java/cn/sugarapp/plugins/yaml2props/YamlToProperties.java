package cn.sugarapp.plugins.yaml2props;


import java.util.Map;
import java.util.TreeMap;
import org.yaml.snakeyaml.Yaml;

/**
 * @author zm9913@sina.cn
 */
public class YamlToProperties {
    TreeMap<String, Map<String, Object>> config;

    public YamlToProperties(String contents){
        Yaml yaml = new Yaml();
        config = yaml.loadAs(contents, TreeMap.class);

    }

    public String convert(){
        return toProperties(config);
    }

    private static String toProperties(TreeMap<String, Map<String, Object>> config) {

        StringBuilder sb = new StringBuilder();

        for (String key : config.keySet()) {

            sb.append(toString(key, config.get(key)));
        }

        return sb.toString();
    }

    private static String toString(String key, Object o) {

        StringBuilder sb = new StringBuilder();
        if (o instanceof Map) {
            Map<String, Object> map = (Map) o;
            for (String mapKey : map.keySet()) {

                if (map.get(mapKey) instanceof Map) {
                    sb.append(toString(String.format("%s.%s", key, mapKey), map.get(mapKey)));
                } else {
                    sb.append(String.format("%s.%s=%s%n", key, mapKey, map.get(mapKey).toString()));
                }
            }

        }else{
            sb.append(String.format("%s=%s%n", key, o));
        }

        return sb.toString();
    }
}
