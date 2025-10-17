package com.sky.oa.proxy.factory;



import com.sky.base.utils.LogUtils;
import com.sky.oa.R;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * properties文件的读取工具
 *
 * @author Administrator
 */
public class PropertiesReader {


    public Map<String, String> getProperties() {

        Properties props = new Properties();
        Map<String, String> map = new HashMap<String, String>();
        try {
            String packageName = "/" + R.class.getName().replace('.', '/');

            packageName = packageName.substring(0, packageName.length() - 1);
            InputStream in = getClass().getResourceAsStream("/assets/type.properties");
//			InputStream in = PropertiesReader.class.getResourceAsStream("/assets/type.properties");
//			InputStream is = context.getAssets().open("type.properties");
//			InputStream is = context.getResources().openRawResource(R.raw.type);
            props.load(in);
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String property = props.getProperty(key);
                map.put(key, property);
//				System.out.println(key + "  " + property);
            }
        } catch (Exception e) {
            LogUtils.d(e.toString());
        }
        return map;
    }
}
