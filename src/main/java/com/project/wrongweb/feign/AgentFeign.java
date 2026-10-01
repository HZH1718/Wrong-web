package com.project.wrongweb.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Agent 服务 Feign 客户端。
 *
 * <p>本次只保留基础脚手架，不实现 AI Agent 业务。</p>
 */
@FeignClient(name = "agent-service", url = "${feign.agent.url}")
public interface AgentFeign {

    // TODO 【需要我实现：Agent Feign 接口方法，摘要：后续在这里声明调用 Python Agent 服务的接口，例如会话、解题、审题、知识点分析等】
}
