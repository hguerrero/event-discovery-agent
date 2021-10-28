package com.event.discovery.agent.framework.model;

import lombok.Data;

import java.util.List;

@Data
public class SempListResponse<T> {
    private List<T> data;
    private List<Object> links;
    private Meta meta;

}
