package com.bysj_backend.config;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.util.calendar.ZoneInfo;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



public class OwnSimpleDateFormatSerializer extends SimpleDateFormatSerializer {

    private final String pattern;

    public OwnSimpleDateFormatSerializer(String pattern) {
        super(pattern);
        this.pattern = pattern;
    }


    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object == null) {
            serializer.out.writeNull();
        } else {
            Date date = null;
            // 改造内容 增加类型判断进行处理
            if (object instanceof LocalDateTime) {
                LocalDateTime localDateTime = (LocalDateTime) object;
                date = Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
            } else {
                date = (Date)object;
            }
            SimpleDateFormat format = new SimpleDateFormat(this.pattern, Locale.SIMPLIFIED_CHINESE);
            TimeZone timeZone = new ZoneInfo();
            timeZone.setRawOffset(28800000);
            timeZone.setID("Asia/Shanghai");
            format.setTimeZone(timeZone);
            String text = format.format(date);
            serializer.write(text);
        }
    }
}

