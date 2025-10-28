package com.sky.oa.gson;

import androidx.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sky.base.utils.LogUtils;
import com.sky.oa.data.model.ActivityEntity;
import com.sky.oa.data.model.ActivityModel;

import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by SKY on 2017/9/27 22:03 九月.
 */
public class GsonUtils {
    private static Gson gson;

    private GsonUtils() {
    }

    private void gsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        //多名称字段匹配与字段过滤相关方法
        //1.@SerializedName，匹配字段，最常用
        //2.@Expose，过滤字段最常用
        builder.excludeFieldsWithoutExposeAnnotation();//需要与@Expose搭配使用
        //3.Modifier，过滤字段
        builder.excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);//排除这些修饰符修饰的属性
        //4.setFieldNamingPolicy与setFieldNamingStrategy，既能过滤也能匹配，级别低于@SerializedName。
        // @SerializedName注解拥有最高优先级，在加有@SerializedName注解的字段上FieldNamingStrategy不生效！
        builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
        builder.setFieldNamingStrategy(new FieldNamingStrategy() {
            @Override
            public String translateName(Field f) {
                String name = f.getName();//对应字段的名称，如需要更换名称则自行处理
                if ("name".equals(name)) {
                    return "activityName";
                } else if ("component".equals(name)) {
                    return "componentName";
                } else if ("des".equals(name)) {
                    return "describe";
                } else if ("image".equals(name)) {
                    return "img";
                } else if ("version".equals(name)) {
                    return "version";
                } else {
                    LogUtils.i("name==" + name);
                    return name;
                }
            }
        });
        //5.自定义排除字段，true为排除，可根据具体的字段具体排除，也可直接排除类。
        builder.addSerializationExclusionStrategy(new ExclusionStrategy() {//序列化时排除
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        builder.addDeserializationExclusionStrategy(new ExclusionStrategy() {//序列化时排除
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        //6.根据版本来排除
        builder.setVersion(1);//与@Since(1.2)或@Until(1.4)搭配使用才有效果

        //7.@JsonAdapter，排除和过滤皆可，对单个类进行单独适配，可支持JsonSerializer与JsonDeserializer，TypeADapter，TypeAdapterFactory
        //自定义的序列化JsonSerializer与反序列化JsonDeserializer，定点适配，局限性很大，需要做费控判定。适用于单独解析特定的json语句
        builder.registerTypeAdapter(ActivityModel.class, new ActivitySerial());//自定义的序列化
        builder.registerTypeAdapter(ActivityModel.class, new ActivityListSerial());//自定义列表序列化
        builder.registerTypeAdapter(new TypeToken<List<ActivityModel>>() {
        }.getRawType(), new ActivityListDeserial());//自定义反序列化，fromJson时会用，注意实体类不匹配的话会崩溃

        //TypeAdapter，重写read与write方法,定点适配，局限性很大，需要做费控判定。也只适用于单独解析特定的json数据
        builder.registerTypeAdapter(String.class, new StringNullAdapter());//不支持继承，但支持泛型
        builder.registerTypeHierarchyAdapter(Number.class, new StringNullAdapter());//支持继承，但不支持泛型
        builder.registerTypeAdapter(ActivityEntity.class, new ActivityTypeAdapter());

        
        //TypeAdapterFactory
        builder.registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<String>());
        builder.registerTypeAdapterFactory(NullStringToEmptyAdapterFactory.getInstance());
        builder.registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, new StringNullAdapter()));

        //其他常用方法
        builder.serializeNulls();//属性值为空时输出 key:null
        builder.setPrettyPrinting();//tojson输出时，格式化json字符串
        builder.setDateFormat("yyyy-MM-dd");// 设置日期时间格式，另有2个重载方法,在序列化和反序化时均生效，个人感觉没什么用
        builder.disableInnerClassSerialization();// 禁此序列化内部类
        builder.generateNonExecutableJson();//生成不可执行的Json（多了 )]}' 这4个字符）
        builder.disableHtmlEscaping();//禁止转义html标签
    }

    public static <T> T fromJson(String json, Class<T> type) throws JsonIOException, JsonSyntaxException {
        return gson().fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson().fromJson(json, type);
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return gson().fromJson(reader, typeOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        return gson().fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return gson().fromJson(json, typeOfT);
    }

    public static String toJson(Object src) {
        return gson().toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return gson().toJson(src, typeOfSrc);
    }

    public static Gson gson() {
        if (gson == null) {
            gson = newInstance();
        }
        return gson;
    }

    public static Gson newInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return newInstance(gsonBuilder);
    }

    public static Gson newInstance(@NonNull GsonBuilder gsonBuilder) {
//        gsonBuilder.registerTypeAdapterFactory(WbgTypeAdapterFactory.newInstance());
        return gsonBuilder.create();
    }
}