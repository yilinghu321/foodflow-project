package com.foodflow.utils;

import com.foodflow.constant.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;


@Slf4j
public class BeanHelper {

    public static <T> T copyProperties(Object source, Class<T> target) {
        try {
            if (source == null) return null;
            T t = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e){
            log.error("Data copy error", target.getName(), e);
            throw new RuntimeException(MessageConstant.DATA_TRANSFER_ERROR);
        }
    }
}
