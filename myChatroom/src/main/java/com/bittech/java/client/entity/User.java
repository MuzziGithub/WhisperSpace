package com.bittech.java.client.entity;

import lombok.Data;

import java.util.Set;

/**
 * 实体类
 */
@Data
public class User {
    //在所有EO/PO类中基本类型必须使用包装类
    //例如：int的默认值为0，但在表中，假设id字段可以为null,如果设置时未设置id这个值，插入到数据库，插入的值应该是0，即插了一个无效数据。
    // 但如果是integer的化话，integer的默认值是null,如果设置时不设置id的值，则插入到数据库中插入的是null,而且如果设置id属性不能为null的话，那么再次插入null值就会报错

    private Integer id;
    //varchar对于Java来说就是string类型
    private String userName;
    private String password;
    private String brief;
    private Set<String> userNames;

}
