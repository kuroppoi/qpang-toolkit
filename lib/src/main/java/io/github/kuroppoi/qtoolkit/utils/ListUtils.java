package io.github.kuroppoi.qtoolkit.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ListUtils {
    
    public static <T> void add(List<T> list, int index, T element) {
        if(index >= 0 && index <= list.size()) {
            list.add(index, element);
        }
    }
    
    public static <T> T remove(List<T> list, int index) {
        if(isIndexInRange(list, index)) {
            return list.remove(index);
        }
        
        return null;
    }
    
    public static <T> T remove(List<T> list, Predicate<T> predicate) {
        Iterator<T> it = list.iterator();
        
        while(it.hasNext()) {
            T t = it.next();
            
            if(predicate.test(t)) {
                it.remove();
                return t;
            }
        }
        
        return null;
    }
    
    public static <T> T get(List<T> list, int index) {
        return get(list, index, null);
    }
    
    public static <T> T get(List<T> list, int index, T def) {
        return isIndexInRange(list, index) ? list.get(index) : def;
    }
    
    public static <T> T get(List<T> list, Predicate<T> predicate) {
        return get(list, predicate, null);
    }
    
    public static <T> T get(List<T> list, Predicate<T> predicate, T def) {
        for(T t : list) {
            if(predicate.test(t)) {
                return t;
            }
        }
        
        return def;
    }
    
    public static <T> List<T> getAll(List<T> list, Predicate<T> predicate) {
        List<T> ret = new ArrayList<>(list);
        ret.removeIf(predicate.negate());
        return ret;
    }
    
    public static boolean isIndexInRange(List<?> list, int index) {
        return index >= 0 && index < list.size();
    }
}
