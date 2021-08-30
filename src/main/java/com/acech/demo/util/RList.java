package com.acech.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * 一个基于redis-template的简易list工具
 *
 * @author wahcen@163.com
 * @date 2021/8/29 16:43
 */
public class RList<T> implements List<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String storeKey;
    private final Class<T> objClass;
    private final ObjectMapper objectMapper;

    public RList(RedisTemplate<String, Object> redisTemplate, String storeKey, Class<T> objClass, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.storeKey = storeKey;
        this.objClass = objClass;
        this.objectMapper = objectMapper;
    }

    private ListOperations<String, Object> ops() {
        return redisTemplate.opsForList();
    }

    @Override
    public int size() {
        Long size = ops().size(storeKey);
        if (size != null) {
            return Math.toIntExact(size);
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (objClass.isAssignableFrom(o.getClass())) {
            List<Object> list = ops().range(storeKey, 0, size());
            if (list != null) {
                return list.stream().map(item -> objectMapper.convertValue(item, objClass)).collect(Collectors.toList()).contains(o);
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        List<Object> list = ops().range(storeKey, 0, size());
        if (list != null) {
            return list.stream().map(item -> objectMapper.convertValue(item, objClass)).iterator();
        }
        return Collections.emptyIterator();
    }

    @Override
    public Object[] toArray() {
        List<Object> list = ops().range(storeKey, 0, size());
        if (list != null) {
            return list.stream().map(item -> objectMapper.convertValue(item, objClass)).toArray();
        }
        return new Object[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1> T1[] toArray(T1[] a) {
        List<Object> list = ops().range(storeKey, 0, size());
        if (list != null) {
            return (T1[]) Arrays.copyOf(list.stream().map(item -> objectMapper.convertValue(item, objClass)).toArray(), size(), a.getClass());
        }
        return (T1[]) new Object[0];
    }

    @Override
    public boolean add(T t) {
        return ops().rightPush(storeKey, t) != null;
    }

    @Override
    public boolean remove(Object o) {
        return ops().remove(storeKey, 0, o) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        List<Object> list = ops().range(storeKey, 0, size());
        if (list != null) {
            List<T> typedList = list.stream().map(item -> objectMapper.convertValue(item, objClass)).collect(Collectors.toList());
            return typedList.containsAll(c);
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return ops().rightPushAll(storeKey, c) != null;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (c != null && !c.isEmpty()) {
            T pivot = get(index);
            c.forEach(v -> ops().rightPush(storeKey, pivot, v));
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c != null && !c.isEmpty()) {
            c.forEach(this::remove);
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection<?> c) {
        if (c != null && !c.isEmpty()) {
            List<T> list = new ArrayList<>((Collection<? extends T>) c);
            List<Object> range = ops().range(storeKey, 0, size());
            if (range != null && !range.isEmpty()) {
                list.removeAll(range.stream().map(v -> objectMapper.convertValue(v, objClass)).collect(Collectors.toList()));
                clear();
                ops().rightPushAll(storeKey, list);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.redisTemplate.delete(storeKey);
    }

    @Override
    public T get(int index) {
        Object o = ops().index(storeKey, index);
        if (o != null) {
            return objectMapper.convertValue(o, objClass);
        }
        return null;
    }

    @Override
    public T set(int index, T element) {
        T old = get(index);
        ops().set(storeKey, index, element);
        return old;
    }

    @Override
    public void add(int index, T element) {
        ops().rightPush(storeKey, get(index), element);
    }

    @Override
    public T remove(int index) {
        T t = get(index);
        if (t != null) {
            ops().remove(storeKey, 0, t);
            return t;
        }
        return null;
    }

    @Override
    public int indexOf(Object o) {
        List<Object> range = ops().range(storeKey, 0, size());
        if (range != null) {
            return range.indexOf(o);
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        List<Object> range = ops().range(storeKey, 0, size());
        if (range != null) {
            return range.lastIndexOf(o);
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        List<Object> range = ops().range(storeKey, 0, size());
        if (range != null) {
            return range.stream().map(v -> objectMapper.convertValue(v, objClass)).collect(Collectors.toList()).listIterator();
        }
        return Collections.emptyListIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        List<Object> range = ops().range(storeKey, 0, size());
        if (range != null) {
            return range.stream().map(v -> objectMapper.convertValue(v, objClass)).collect(Collectors.toList()).listIterator(index);
        }
        return Collections.emptyListIterator();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        List<Object> range = ops().range(storeKey, 0, size());
        if (range != null) {
            return range.stream().map(v -> objectMapper.convertValue(v, objClass)).collect(Collectors.toList()).subList(fromIndex, toIndex);
        }
        return Collections.emptyList();
    }
}
