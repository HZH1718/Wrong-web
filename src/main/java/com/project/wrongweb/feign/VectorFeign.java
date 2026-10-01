package com.project.wrongweb.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * 向量服务 Feign 客户端。
 *
 * <p>本次只保留基础脚手架，不实现向量检索与向量写入业务。</p>
 */
@FeignClient(name = "vector-service", url = "${feign.vector.url}")
public interface VectorFeign {

    // TODO 【需要我实现：Vector Feign 接口方法，摘要：后续在这里声明调用 Python 向量服务的接口，例如题目向量化、相似题检索、向量写入与删除】
}
