# java-bug-test-for-ai

订单管理演示服务（Spring Boot 3.2 / Java 17），用于测试 AI 代码评审能力。

## 模块

- `controller` REST 接口
- `service` 订单 / 库存 / 价格 / 报告业务逻辑
- `repository` 内存版订单仓储
- `model` 领域模型

## 运行

```bash
mvn spring-boot:run
```

## 说明

本仓库代码由 AI 生成，包含若干预置缺陷，用于评测另一个 AI 评审工具的找 bug 能力。详见 `TEST_REPORT.md`。
