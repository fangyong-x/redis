package com.fang.jedispubsub;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JedisTest {
    private Jedis jedis;

    @Before
    public void setRedis(){
        jedis = new Jedis("127.0.0.1", 6379);
        System.out.println("redis服务连接好了");
    }
    /**
     * Redis操作字符串
     */
    @Test
    public void testString(){
        //添加数据
        jedis.set("one", "1");
        jedis.set("one", "2");  //重复插入会覆盖，不是报错
        System.out.println(jedis.get("one"));
        //向key为name的值后面加上数据 ---拼接
        jedis.append("one", "2");
        System.out.println("拼接后:" + jedis.get("one"));
        jedis.incr("one"); //只能对能转为整数型的进行递增，其他类型会报错
        System.out.println("递增后: " + jedis.get("one"));
        //删除某个键值对
        jedis.del("one");
        System.out.println("删除后:" + jedis.get("one"));
        /**
         * redis服务连接好了
         2
         拼接后:22
         递增后: 23
         删除后:null
         */
    }
    /**
     * Redis操作map
     */
    @Test
    public void testMap() {
        //添加数据
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "chx");
        map.put("age", "100");
        map.put("email", "***@outlook.com");
        jedis.hmset("user", map);
        //取出user中的name，结果是一个泛型的List
        //第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key是可变参数
        List<String> list = jedis.hmget("user", "name", "age", "email");
        System.out.println(list);

        //删除map中的某个键值
        jedis.hdel("user", "age");
        System.out.println("age:" + jedis.hmget("user", "age")); //因为删除了，所以返回的是null
        System.out.println("user的键中存放的值的个数:" + jedis.hlen("user")); //返回key为user的键中存放的值的个数2
        System.out.println("是否存在key为user的记录:" + jedis.exists("user"));//是否存在key为user的记录 返回true
        System.out.println("user对象中的所有key:" + jedis.hkeys("user"));//返回user对象中的所有key
        System.out.println("user对象中的所有value:" + jedis.hvals("user"));//返回map对象中的所有value

        //拿到key，再通过迭代器得到值
        Iterator<String> iterator = jedis.hkeys("user").iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            System.out.println(key + ":" + jedis.hmget("user", key));
        }
        jedis.del("user");
        System.out.println("删除后是否存在key为user的记录:" + jedis.exists("user"));//是否存在key为user的记录
        /**
         * redis服务连接好了
         [chx, 100, ***@outlook.com]
         age:[null]
         user的键中存放的值的个数:2
         是否存在key为user的记录:true
         user对象中的所有key:[name, email]
         user对象中的所有value:[chx, ***@outlook.com]
         name:[chx]
         email:[***@outlook.com]
         删除后是否存在key为user的记录:false
         */

    }

    /**
     * jedis操作List
     */
    @Test
    public void testList(){
        //移除javaFramwork所所有内容
        jedis.del("javaFramwork");
        //存放数据
        jedis.lpush("javaFramework","spring");
        jedis.lpush("javaFramework","springMVC");
        jedis.lpush("javaFramework","mybatis");
        //取出所有数据,jedis.lrange是按范围取出
        //第一个是key，第二个是起始位置，第三个是结束位置
        System.out.println("长度:"+jedis.llen("javaFramework"));
        //jedis.llen获取长度，-1表示取得所有
        System.out.println("javaFramework:"+jedis.lrange("javaFramework",0,-1));

        jedis.del("javaFramework");
        System.out.println("删除后长度:"+jedis.llen("javaFramework"));
        System.out.println(jedis.lrange("javaFramework",0,-1));
        /**
         * redis服务连接好了
         长度:3
         javaFramework:[mybatis, springMVC, spring]
         删除后长度:0
         []
         */
    }

    /**
     * jedis操作Set
     */
    @Test
    public void testSet(){
        //添加
        jedis.sadd("user","chenhaoxiang");
        jedis.sadd("user","hu");
        jedis.sadd("user","chen");
        jedis.sadd("user","xiyu");
        jedis.sadd("user","chx");
        jedis.sadd("user","are");
        //移除user集合中的元素are
        jedis.srem("user","are");
        System.out.println("user中的value:"+jedis.smembers("user"));//获取所有加入user的value
        System.out.println("chx是否是user中的元素:"+jedis.sismember("user","chx"));//判断chx是否是user集合中的元素
        System.out.println("集合中的一个随机元素:"+jedis.srandmember("user"));//返回集合中的一个随机元素
        System.out.println("user中元素的个数:"+jedis.scard("user"));
        /**
         * redis服务连接好了
         user中的value:[hu, chen, chenhaoxiang, chx, xiyu]
         chx是否是user中的元素:true
         集合中的一个随机元素:xiyu
         user中元素的个数:5
         */
    }
    /**
     * 排序
     */
    @Test
    public void test(){
        jedis.del("number");//先删除数据，再进行测试
        jedis.rpush("number","4");//将一个或多个值插入到列表的尾部(最右边)
        jedis.rpush("number","5");
        jedis.rpush("number","3");

        jedis.lpush("number","9");//将一个或多个值插入到列表头部
        jedis.lpush("number","1");
        jedis.lpush("number","2");
        System.out.println(jedis.lrange("number",0,jedis.llen("number")));
        System.out.println("排序:"+jedis.sort("number"));
        System.out.println(jedis.lrange("number",0,-1));//不改变原来的排序
        jedis.del("number");//测试完删除数据

        /*
         redis服务连接好了
         [2, 1, 9, 4, 5, 3]
         排序:[1, 2, 3, 4, 5, 9]
         [2, 1, 9, 4, 5, 3]
         */
    }
}
