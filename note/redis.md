redis概述
===

- Redis是一个开源的key-value存储系统。
- 和Memcached类似，它支持存储的value类型相对更多，包括string(字符串)、list(链表)、set(集合)、zset(sorted set --有序集合)和hash（哈希类型）。
- 这些数据类型都支持push/pop、add/remove及取交集并集和差集及更丰富的操作，而且这些操作都是原子性的。在此基础上，Redis支持各种不同方式的排序。
- 与memcached一样，为了保证效率，数据都是缓存在内存中。区别的是Redis会周期性的把更新的数据写入磁盘或者把修改操作写入追加的记录文件。
- 并且在此基础上实现了master-slave(主从)同步



> **单线程 + 多路IO复用**



```shell
export PS1=’[\u@\h \W]\$’

## 修改用户名
groupmod -n chao chaochao

export PS1='[\H@${ip} \t:\w]\$'
## 
## 网络计划位置
/etc/netplan

export ip=`hostname -i|awk '{print $1}'|awk -F'.' '{print $(NF-1)"."$NF}'`
export PS1='[\H@${ip} \t:\w]\$'
## linux终端显示
[chao@10.133 22:39:56:/etc/netplan]$
```



**redis 基本命令**
===



1 关于redis操作的基本命令
===

```shell
## 登录指定主机和端口号的redis服务
redis-server config-file
redis-cli -h host -p port
## redis默认有16个数据库 默认使用的是0号数据库
## 选择数据库
select 1
## 清空当前数据中的所有的key
flushdb 
## 清空所有数据库的中的key
flushall
```



2 关于key的命令
===

```shell
## 查看所有的键
keys *
## 查看key的类型
type key
## 查看key的剩余生存时间
ttl key ## 不存在返回-2 不过时返回-1
## 删除key
del key1 key2
## 设置过期时间
expire key seconds
## 判断key是否存在
exists key

## 重命名key
127.0.0.1:6379> rename s_uion s_union
OK
```



3 字符串的相关操作
===

```shell
## 添加值
set name yangyuqi

## 获取值
get name

## 设置值并设置过期时间
set name konglc EX 60

## 获取值的长度
strlen name

## 不存在则设置值
setnx name konglc

## 设置值的同时设置过期时间
setex sex 60 femail

## 同时设置多个值
mset name yuqi sex femail age 22

##同时获取多个值
mget name sex age

## 不存在则同时设置多个值
msetnx salary 100 work_age 3

## 获取旧值 并设置新值
getset name konglc

## 递增1 原子操作 
set i 1
incr i

## 递增指定的值
incrby i 1000

## 递减1
decr i

##递减指定值
decrby i 1000
```



4 list的相关操作
===

```shell
 ## 往列表的左边放入数据
 lpush stu yangyuqi chadan konglc
 ## 获取指定范围的数据
 lrange stu 0 -1
 ## 设置列表中指定下标位置的元素的值
 lset stu 0 chaochao
 ## 从列表中移除指定的元素
 lrem stu 1 chadan
 ## 获取列表中元素的个数
llen stu

## 在指定的元素后面插入元素
linsert stu after yangyuqi xiaoyuq

## 从左边弹出一个元素  没有元素的会阻塞
blmove score1 score2 left left 60

## 从右边弹出一个元素  没有元素的会阻塞
blmove score1 score2 right right 60

## 移除并获取列表中的第一个元素 没有元素则会阻塞  BRPOP
 blpop score1 60
 
 ## 从一个列表中弹出一个元素 放到另一个列表中 如果源列表中没有数据就阻塞
 BRPOPLPUSH source destination timeout
 
 BRPOPLPUSH provider consumner 120
 
 lrange consumner 0 -1
 
 ## 移除并获取列表中的第一个元素
 lpop list1 1 ## 元素的个数
 
 ## 移除并获取最后一个元素
 rpop name
 
 ## 移除列表中的最后一个元素并放在另一个列表中
 RPOPLPUSH name name1
 lrange name1 0 -1
 
 ## 从后面追加元素
 127.0.0.1:6379> lrange name 0 -1
1) "konglz"
2) "lvyi"

127.0.0.1:6379> rpush name yangyuqi qiqi xiaoyuqi
(integer) 5

127.0.0.1:6379> lrange name 0 -1
1) "konglz"
2) "lvyi"
3) "yangyuqi"
4) "qiqi"
5) "xiaoyuqi"

## 当键存在时追加元素
 RPUSHX name konglz chaochao lingchao
 127.0.0.1:6379> lrange name 0 -1
1) "konglz"
2) "lvyi"
3) "yangyuqi"
4) "qiqi"
5) "xiaoyuqi"
6) "konglz"
7) "chaochao"
8) "lingchao"

## 返回某个元素的位置
192.168.10.134:6379> lpos names konglc
(integer) 2

## 键存在的时候才放入值
192.168.10.134:6379> lpushx names chengdu suzhou qinhuangdao 
(integer) 9


### 
192.168.10.134:6379> lrange names 0 -1
1) "qinhuangdao"
2) "suzhou"
3) "chengdu"
4) "mysql"
5) "redis"
6) "kafaka"
7) "Yuqi"
8) "chao"
9) "konglc"

## 从指定位置截取链表中的元素
192.168.10.134:6379> ltrim names 0 2
OK
192.168.10.134:6379> lrange names 0 -1
1) "qinhuangdao"
2) "suzhou"
3) "chengdu"

## 存在则在最后加入数据
192.168.10.134:6379> rpushx names huawei
(integer) 5
192.168.10.134:6379> lrange names 0 -1
1) "ysuSchool"
2) "inovance"
3) "suzhou"
4) "chengdu"
5) "huawei"
```



5 set数据类型的相关操作
===

```shell
## 添加元素 set中的元素不能重复
127.0.0.1:6379> sadd score 100 200 400 200 400
(integer) 3

## 获取集合中所有的元素
127.0.0.1:6379> smembers score
1) "100"
2) "200"
3) "400"

## 获取集合中成员的个数
127.0.0.1:6379> scard score
(integer) 3

## 判断指定的元素是否是集合中成员
127.0.0.1:6379> sismember score 100
(integer) 1

## 差集
127.0.0.1:6379> sdiff score score1
1) "100"
2) "200"
3) "400"

## 将差集保存在一个key中
127.0.0.1:6379> smembers score
1) "100"
2) "200"
3) "400"
127.0.0.1:6379> smembers score1
1) "1"
2) "2"
3) "4"
4) "200"
5) "400"
127.0.0.1:6379> smembers score
1) "100"
2) "200"
3) "400"
127.0.0.1:6379> sdiffstore s_diff score score1
(integer) 1
127.0.0.1:6379> smembers s_diff
1) "100"

## 求交集
127.0.0.1:6379> sinter score score1
1) "200"
2) "400"

## 求交集并保存在一个key中
127.0.0.1:6379> sinterstore s_inter score score1
(integer) 2
127.0.0.1:6379> smembers s_inter
1) "200"
2) "400"

## 求并集
127.0.0.1:6379> sunion score score1
1) "1"
2) "2"
3) "4"
4) "100"
5) "200"
6) "400"

## 求并集并保存在一个key中
127.0.0.1:6379> sunionstore s_uion score score1
(integer) 6
127.0.0.1:6379> smembers s_union
(empty array)
127.0.0.1:6379> rename s_uion s_union
OK
127.0.0.1:6379> smembers s_union
1) "1"
2) "2"
3) "4"
4) "100"
5) "200"
6) "400"

### Redis Sscan 命令用于迭代集合中键的元素，Sscan 继承自 Scan。
127.0.0.1:6379> sscan s_union 0 match * count 10
1) "0"
2) 1) "1"
   2) "2"
   3) "4"
   4) "100"
   5) "200"
   6) "400"
```



6 redis有序集合命令(sorted set)
===

```shell
## 添加元素
zadd name 100 konglc 

## 通过分数返回有序集合指定区间内的成员
127.0.0.1:6379> zrange name 0 -1 withscores
1) "konglc"
2) "100"
3) "yangyuq"
4) "200"


127.0.0.1:6379> zrange name 0 -1 withscores
1) "lele"
2) "50"
3) "konglc"
4) "100"
5) "yangyuq"
6) "200"

##返回有序集合中成员的个数
127.0.0.1:6379> zcard name
(integer) 3


## 计算在有序集合中指定区间分数的成员数
127.0.0.1:6379> zcount name 50 100
(integer) 2
```



7 redis 哈希(hash)
===

```shell
## 设置hash某个字段的值
127.0.0.1:6379> hset stu1 name konglc score 100 salary 15000
(integer) 3

## 删除一个或多个hash字段
127.0.0.1:6379> hdel stu1 sex 
(integer) 1

## 判断hash中某个字段是否存在
127.0.0.1:6379> hexists stu1 mail
(integer) 0

## 获取某个hash字段的值
127.0.0.1:6379> hget stu1 name
"konglc"

## 获取一个键中所有的字段和值
127.0.0.1:6379> hgetall stu1
1) "name"
2) "konglc"
3) "score"
4) "100"
5) "salary"
6) "15000"

## 给hash中某个字段的值增加指定数值
127.0.0.1:6379> HINCRBY stu1 score 100
(integer) 200
127.0.0.1:6379> hget stu1 score
"200"

## 获取hash中所有的字段
127.0.0.1:6379> hkeys stu1
1) "name"
2) "score"
3) "salary"

## 获取hash中字段的个数
127.0.0.1:6379> hlen stu1
(integer) 3

## 获取给定的所有hash字段的值
127.0.0.1:6379> hmget stu1 name score
1) "konglc"
2) "200"

## 同时设置多个字段的值
127.0.0.1:6379> hmset stu1 age 27 school ysu
OK
127.0.0.1:6379> hgetall stu1
 1) "name"
 2) "konglc"
 3) "score"
 4) "200"
 5) "salary"
 6) "15000"
 7) "age"
 8) "27"
 9) "school"
10) "ysu"


127.0.0.1:6379> hscan stu1 1 match *
1) "0"
2)  1) "name"
    2) "konglc"
    3) "score"
    4) "200"
    5) "salary"
    6) "15000"
    7) "age"
    8) "27"
    9) "school"
   10) "ysu"
   
 ## 不存在才设置值
 127.0.0.1:6379> hsetnx stu1 girl yangyuqi
(integer) 1
127.0.0.1:6379> hgetall stu1
 1) "name"
 2) "konglc"
 3) "score"
 4) "200"
 5) "salary"
 6) "15000"
 7) "age"
 8) "27"
 9) "school"
10) "ysu"
11) "girl"
12) "yangyuqi"

## 获取hash中某个字段的长度
127.0.0.1:6379> hstrlen stu1 name
(integer) 6
```



Redis 集群模式
===

```shell
redis-cli -h 192.168.10.132 -p 6000 cluster nodes
redis-cli -h 192.168.10.132 -p 6000 -c dbsize

## 获取键的插槽
192.168.10.132:6002> cluster keyslot user
(integer) 5474

## 获取槽中的键
192.168.10.132:6002> CLUSTER COUNTKEYSINSLOT 5474
(integer) 3

## 获取槽中的键
192.168.10.132:6002> CLUSTER GETKEYSINSLOT 5474 10
1) "k1{user}"
2) "k2{user}"
3) "k3{user}"

## 获取槽中键的个数
192.168.10.132:6002> CLUSTER COUNTKEYSINSLOT 5474
(integer) 3

```

