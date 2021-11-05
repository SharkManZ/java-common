package ru.shark.home.common;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import ru.shark.home.common.dao.util.ConverterUtil;

import java.util.Arrays;

@SpringBootConfiguration
public class CommonTestConfiguration {

    @Bean
    public ConverterUtil getConverterUtil() {
        return new ConverterUtil();
    }

    @Bean
    public Mapper getMapper() {
        DozerBeanMapper mapper = new DozerBeanMapper();

        return mapper;
    }
}
