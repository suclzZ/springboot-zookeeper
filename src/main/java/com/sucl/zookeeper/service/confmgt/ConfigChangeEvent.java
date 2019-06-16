package com.sucl.zookeeper.service.confmgt;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/**
 * @author sucl
 * @date 2019/6/4
 */
@Data
public class ConfigChangeEvent extends ApplicationEvent {
    private String config;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ConfigChangeEvent(Object source) {
        super(source);
        this.config = Objects.toString(source,null);
    }

}
