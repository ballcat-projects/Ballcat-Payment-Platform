package live.lingting.payment.biz;

import cn.hutool.core.convert.Convert;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author lingting 2020/4/17 11:49
 */
@Slf4j
@RequiredArgsConstructor
@Component("live.lingting.payment.biz.Redis")
public class Redis {

	private final StringRedisTemplate template;

	/*
	 * common ----------------------------------------------------------
	 */

	/**
	 * @author lingting 2020-04-29 15:04:35
	 */
	public StringRedisTemplate getTemplate() {
		return template;
	}

	/**
	 * @author lingting 2020-04-29 15:04:39
	 */
	public HashOperations<String, Object, Object> getHash() {
		return template.opsForHash();
	}

	/**
	 * @author lingting 2020-04-29 15:04:40
	 */
	public ValueOperations<String, String> getValue() {
		return template.opsForValue();
	}

	public ListOperations<String, String> getList() {
		return template.opsForList();
	}

	public SetOperations<String, String> getSet() {
		return template.opsForSet();
	}

	public boolean hasKey(String key) {
		return template.hasKey(key);
	}

	/**
	 * 设置过期时间
	 * @param time 时间，单位 秒
	 * @author lingting 2020-07-28 13:28:41
	 */
	public boolean expire(String key, long time) {
		return template.expire(key, time, TimeUnit.SECONDS);
	}

	/**
	 * 获取过期时间
	 * @return 过期时间
	 */
	public long getExpire(String key) {
		return template.getExpire(key);
	}

	/**
	 * 获取所有符合指定表达式的 key
	 * @param pattern 表达式
	 * @return java.util.Set<java.lang.String>
	 * @author lingting 2020-04-27 15:44:09
	 */
	public Set<String> keys(String pattern) {
		return template.keys(pattern);
	}

	/*
	 * lua 脚本 ----------------------------------------------------------
	 */

	/**
	 * @author lingting 2021-04-26 11:36
	 */
	public <T> T execute(RedisCallback<T> action) {
		return template.execute(action);
	}

	@Nullable
	public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
		return execute(action, exposeConnection, false);
	}

	@Nullable
	public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
		return template.execute(action, exposeConnection, pipeline);
	}

	public <T> T execute(SessionCallback<T> session) {
		return template.execute(session);
	}

	public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
		return template.execute(script, keys, args);
	}

	public <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer, RedisSerializer<T> resultSerializer,
			List<String> keys, Object... args) {
		return template.execute(script, argsSerializer, resultSerializer, keys, args);
	}

	/*
	 * pipelined 操作 ----------------------------------------------------------
	 */

	/**
	 * @author lingting 2021-04-26 11:36
	 */
	public List<Object> executePipelined(SessionCallback<?> session) {
		return template.executePipelined(session);
	}

	public List<Object> executePipelined(SessionCallback<?> session, @Nullable RedisSerializer<?> resultSerializer) {
		return template.executePipelined(session, resultSerializer);
	}

	public List<Object> executePipelined(RedisCallback<?> action) {
		return template.executePipelined(action);
	}

	public List<Object> executePipelined(RedisCallback<?> action, @Nullable RedisSerializer<?> resultSerializer) {
		return template.executePipelined(action, resultSerializer);
	}

	/*
	 * string ----------------------------------------------------------
	 */

	/**
	 * @author lingting 2020-04-29 15:04:48
	 */
	public String get(String key) {
		return getValue().get(key);
	}

	public void set(String key, String val) {
		getValue().set(key, val);
	}

	/**
	 * 缓存数据
	 * @param key key
	 * @param val val
	 * @param second 过期时间 单位：秒
	 * @author lingting 2020-04-22 11:38:13
	 */
	public void set(String key, String val, long second) {
		if (second > 0) {
			getValue().set(key, val, second, TimeUnit.SECONDS);
		}
	}

	/**
	 * 缓存数据
	 * @param key key
	 * @param val val
	 * @param instant 在指定时间过期
	 * @author lingting 2021-02-02 10:31
	 */
	public void set(String key, String val, Instant instant) {
		getValue().set(key, val);
		getTemplate().expireAt(key, instant);
	}

	public boolean delete(String key) {
		return template.delete(key);
	}

	public long delete(Collection<String> keys) {
		return template.delete(keys);
	}

	/**
	 * 如果 key 不存在，则设置 key为 val 并设置过期时间
	 * @param key key
	 * @param value val
	 * @return boolean
	 * @author lingting 2020-04-29 15:56:51
	 */
	public boolean setIfAbsent(String key, String value) {
		Boolean b = getValue().setIfAbsent(key, value);
		return b != null && b;
	}

	/**
	 * 如果key存在则设置
	 * @param key key
	 * @param value 值
	 * @param time 过期时间, 单位 秒
	 * @return boolean
	 * @author lingting 2021-01-18 14:59
	 */
	public boolean setIfAbsent(String key, String value, long time) {
		Boolean b = getValue().setIfAbsent(key, value, Duration.ofSeconds(time));
		return b != null && b;
	}

	public List<String> multiGet(Collection<String> keys) {
		List<String> list = getValue().multiGet(keys);
		return list == null ? new ArrayList<>() : new ArrayList<>(list);
	}

	/**
	 * 给 key +1
	 *
	 * @author lingting 2020-05-13 11:04:17
	 */
	public Long increment(String key) {
		return getValue().increment(key);
	}

	/**
	 * 给 key 增加 指定数值
	 *
	 * @author lingting 2020-05-13 11:04:17
	 */
	public Long increment(String key, long delta) {
		return getValue().increment(key, delta);
	}

	public Long incrementAndExpire(String key, long time) {
		return incrementAndExpire(key, 1, time);
	}

	public Long incrementAndExpire(String key, long delta, long time) {
		Long increment = getValue().increment(key, delta);
		expire(key, time);
		return increment;
	}

	/*
	 * list ----------------------------------------------------------
	 */

	/**
	 * @author lingting 2020-04-29 15:04:52
	 */
	public List<String> listGet(String key) {
		return getList().range(key, 0, listSize(key) - 1);
	}

	/**
	 * 获取指定值在指定key中的索引
	 *
	 * @author lingting 2020-12-17 11:06
	 */
	public Long listIndexOf(String key, String val) {
		return getList().indexOf(key, val);
	}

	/**
	 * 获知指定key中指定索引的值
	 *
	 * @author lingting 2020-12-17 11:07
	 */
	public String listIndex(String key, long index) {
		return getList().index(key, index);
	}

	public Long listRemove(String key, String val) {
		return listRemove(key, 1, val);
	}

	/**
	 * @param count 删除多少个
	 * @author lingting 2020-12-16 19:13
	 */
	public Long listRemove(String key, long count, String val) {
		return getList().remove(key, count, val);
	}

	public long listSize(String key) {
		Long size = getList().size(key);
		return size == null ? 0 : size;
	}

	private long listSet(String key, List<?> list) {
		long l = 0;
		for (Object o : list) {
			l += listLeftPush(key, Convert.toStr(o));
		}
		return l;
	}

	/**
	 * 插入list 并设置过期时间
	 * @param key key
	 * @param list list 值
	 * @param time 过期时间
	 * @return long
	 * @author lingting 2020-04-22 15:22:02
	 */
	public long listSet(String key, List<?> list, long time) {
		long l = listSet(key, list);
		expire(key, time);
		return l;
	}

	/**
	 * 插入列表
	 * @param key key
	 * @param val val
	 * @author lingting 2020-04-22 15:18:07
	 */
	public Long listLeftPush(String key, String val) {
		return getList().leftPush(key, val);
	}

	public String listPop(String key) {
		return getList().rightPop(key);
	}

	/*
	 * hash ----------------------------------------------------------
	 */

	/**
	 * @author lingting 2020-04-29 15:05:41
	 */
	public void hashSet(String key, String field, String value) {
		getHash().put(key, field, value);
	}

	/**
	 * 获取 指定 key 中 指定 field 的值
	 * @param key key
	 * @param field field
	 * @return java.lang.Object
	 * @author lingting 2020-04-22 17:17:33
	 */
	public String hashGet(String key, String field) {
		Object o = getHash().get(key, field);
		return o == null ? null : o.toString();
	}

	/**
	 * 移除指定 key中的 字段
	 * @param key key
	 * @param fields 字段
	 * @return java.lang.Long
	 * @author lingting 2020-12-21 10:16
	 */
	public Long hashDelete(String key, String... fields) {
		return getHash().delete(key, (Object[]) fields);
	}

	/*
	 * set -----------------------------------------------------------
	 */

	/**
	 * set中添加数据
	 *
	 * @author lingting 2020-12-17 10:50
	 */
	public Long setAdd(String key, String... values) {
		return getSet().add(key, values);
	}

	/**
	 * 获取集合中元素数量
	 *
	 * @author lingting 2020-12-17 10:51
	 */
	public Long setSize(String key) {
		return getSet().size(key);
	}

	/**
	 * 随机弹出一个元素
	 *
	 * @author lingting 2020-12-17 10:52
	 */
	public String setPop(String key) {
		return getSet().pop(key);
	}

	/**
	 * 移除集合中的元素
	 *
	 * @author lingting 2020-12-17 10:55
	 */
	public Long setRemove(String key, String... values) {
		return getSet().remove(key, (Object[]) values);
	}

	public Object evalLua(String lua, List<String> key, Object... argv) {
		Object[] arg = Arrays.stream(argv).map(String::valueOf).toArray(String[]::new);

		try {
			DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(lua, String.class);
			return template.execute(redisScript, new StringRedisSerializer(), new StringRedisSerializer(), key, arg);
		}
		catch (Exception e) {
			log.error("redis evalLua execute fail:lua[{}]", lua, e);
			return "false";
		}
	}

}
