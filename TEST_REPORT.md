# AI 代码评审测试报告

- **测试日期**：2026-08-29
- **测试对象**：本仓库（java-bug-test-for-ai），Spring Boot 3.2.5 / Java 17 订单管理演示服务
- **测试方法**：由 AI-A（代码生成方）在代码中预埋 13 个典型 Java 缺陷后提交；由 AI-B（独立评审方，评审前不知晓埋点位置）对全部源码做盲测评审；最后比对预埋清单与评审结果，输出检出率报告。
- **代码规模**：10 个 Java 文件，约 650 行。

---

## 一、预埋缺陷清单（13 个）

| # | 预埋缺陷 | 位置 | 类别 |
|---|---|---|---|
| P1 | `Order.equals` 中 `customerName` 用 `==` 引用比较 | `model/Order.java` | 语言陷阱 |
| P2 | 静态共享 `SimpleDateFormat`，线程不安全 | `util/DateUtils.java` | 并发 |
| P3 | `HashMap` 存库存 + check-then-act 竞态，并发下超卖 | `service/InventoryService.java` | 并发 |
| P4 | SQL 字符串拼接（注入面） | `repository/OrderRepository.java#findByCustomer` | 安全 |
| P5 | `new BigDecimal(double)` 精度陷阱 | `service/PriceCalculator.java#applyDiscount` | 语言陷阱 |
| P6 | `setScale(2)` 未指定 RoundingMode，触发时抛异常 | `service/PriceCalculator.java#applyDiscount` | 语言陷阱 |
| P7 | `Long` 装箱类型 `==` 比较，ID>127 时取消订单静默失效 | `service/OrderService.java#cancelOrder` | 语言陷阱 |
| P8 | `Optional.get()` 无存在性检查（3 处） | `service/OrderService.java#payOrder/cancelOrder/refundOrder` | 健壮性 |
| P9 | 分页 off-by-one：`page * size` 应为 `(page-1) * size` | `service/OrderService.java#listOrders` | 逻辑 |
| P10 | `BigDecimal.equals` scale 敏感比较，退款永不生效 | `service/OrderService.java#refundOrder` | 语言陷阱 |
| P11 | `split(".")` 正则未转义，导入功能全空 | `controller/OrderController.java#importProducts` | 语言陷阱 |
| P12 | `FileWriter` 未关闭：资源泄漏 + 文件内容丢失 | `service/ReportService.java#exportOrder` | 资源管理 |
| P13 | `IOException` 被吞掉仅 printStackTrace，接口假成功 | `service/ReportService.java#exportOrder` | 异常处理 |

## 二、盲测评审结果比对

| 预埋缺陷 | AI-B 是否检出 | 对应评审条目 | 定位准确度 |
|---|---|---|---|
| P1 String `==` | ✅ | Bug 1 | 精确到行，指出 hashCode/equals 语义割裂 |
| P2 静态 SimpleDateFormat | ✅ | Bug 12 | 精确，并给出 ThreadLocal/DateTimeFormatter 修复方案 |
| P3 HashMap 竞态超卖 | ✅ | Bug 7 | 精确，给出并发复现场景与 `compute` 修复 |
| P4 SQL 注入 | ✅ | Bug 8 | 精确，说明当前内存实现的实际风险边界 |
| P5 BigDecimal(double) | ✅ | Bug 5 | 精确，指出 0.05 的实际二进制误差值 |
| P6 setScale 无 RoundingMode | ✅ | Bug 6 | 精确，给出触发样例（0.333） |
| P7 Long `==` | ✅ | Bug 2 | 精确，点出 Long 缓存 -128~127 的机理 |
| P8 Optional.get() | ✅ | Bug 14 | 3 处全部找到，并指出与 getOrder 行为不一致 |
| P9 分页 off-by-one | ✅ | Bug 4 | 精确，指出 page=1&size=10 返回空的复现方式 |
| P10 BigDecimal.equals | ✅ | Bug 3 | 精确，说明 scale=0 与 scale=2 比较失败的机理 |
| P11 split(".") | ✅ | Bug 10 | 精确，指出正则 `.` 匹配任意字符 |
| P12 FileWriter 泄漏 | ✅ | Bug 9 | 精确，同时指出缓冲区未 flush 导致文件为空 |
| P13 异常吞掉 | ✅ | Bug 18 | 精确，指出接口返回假成功 |

### 检出率统计

- **预埋缺陷检出：13 / 13 = 100%**
- 全部 13 个预埋缺陷均被精确定位到文件与方法级别，且评审方对每个缺陷均正确说明了触发场景与出错机理，未出现误报性描述。

## 三、超出预埋的额外发现（8 个）

评审方在 13 个预埋点之外，还发现了 8 个代码生成时未刻意设计、但确实存在的问题：

| # | 额外发现 | 严重程度 | 评价 |
|---|---|---|---|
| E1 | export 接口 path 参数未校验，任意路径文件写入（路径穿越） | 高 | 真实安全风险，确属设计疏漏 |
| E2 | createOrder 忽略库存扣减结果，库存不足仍下单成功 | 高 | 真实业务逻辑缺陷 |
| E3 | refundOrder 缺少状态机校验，PENDING 订单可被退款 | 中 | 真实状态流转缺陷 |
| E4 | 重复支付时 paidAt 为 null 导致 NPE | 中 | 真实健壮性缺陷 |
| E5 | findByCustomer 对 customerName=null 的订单 NPE | 中 | 真实边界缺陷 |
| E6 | equals/hashCode 基于可变字段，集合语义漂移 | 低 | 合理的设计观点 |
| E7 | items List 引用逸出（可变共享） | 低 | 合理的防御性拷贝建议 |
| E8 | size 无上界 + page*size 可能 int 溢出 | 低 | 合理的健壮性风险 |

## 四、评审方给出的严重程度分布

| 严重程度 | 数量 |
|---|---|
| 严重/高 | 11 |
| 中 | 6 |
| 低 | 4 |
| **合计** | **21** |

## 五、结论

1. **盲测检出率 100%**：13 个预埋缺陷全部被独立评审 AI 找出，且均定位到文件/方法级别并给出了正确的触发机理分析。
2. **预埋之外的产出可观**：额外发现 8 个真实问题（含 1 个高危安全漏洞：任意路径文件写入），说明评审能力不局限于"找埋点"，具备一般性的代码审查价值。
3. **无误报**：评审报告未出现将正常代码错误指控为 bug 的情况（风格类建议已被排除在 bug 清单之外）。
4. **可改进点**：本次测试样本较小（约 650 行、单模块）；预埋缺陷均为 Java 领域较典型的模式化陷阱，属于 AI 的优势区。后续可扩充为多模块、跨文件调用链、并发时序（需要真正并发复现）以及业务规则型缺陷，进一步测试评审深度。

---

## 六、修复记录（2026-08-29 追加）

针对评审发现的全部 21 个问题（13 个预埋 + 8 个额外），修复方案如下：

| 文件 | 修复内容 |
|---|---|
| `model/Order.java` | equals/hashCode 改为基于业务键 id，字段比较统一用 `Objects.equals`（P1、E6） |
| `util/DateUtils.java` | 改用不可变且线程安全的 `java.time.DateTimeFormatter`，parse 增加 null/blank 保护（P2） |
| `service/InventoryService.java` | 改用 `ConcurrentHashMap`，扣减改为 `compute` 内原子完成 check-then-act，消除超卖（P3） |
| `repository/OrderRepository.java` | 删除 SQL 字符串拼接，过滤改 `customerName.equals(...)` 消除 NPE，参数判空（P4、E5） |
| `service/PriceCalculator.java` | `new BigDecimal(double)` → `BigDecimal.valueOf`；`setScale(2)` → `setScale(2, RoundingMode.HALF_UP)`；lineTotal 增加单价判空（P5、P6） |
| `service/OrderService.java` | `findById().get()` → `getOrder()`（P8）；删除 `Long ==` 判断（P7）；分页改 `(page-1)*size`、long 防溢出、按 id 排序、size 上限 100（P9、E8）；BigDecimal 比较改 `compareTo`（P10）；下单改为先扣库存、失败逐项回滚（E2）；退款增加 PAID 状态校验与足额校验（E3）；items 防御性拷贝（E7） |
| `service/ReportService.java` | 改 `Files.write` 自动管理资源；IOException 转为业务异常上抛，消除假成功（P12、P13） |
| `controller/OrderController.java` | `split(".")` → `split("\\.")`（P11）；payOrder 使用 service 返回值并判空 paidAt（E4）；export 去掉客户端可控 path，改为服务端固定 reports/ 目录生成（E1） |

**修复后复检**：由独立 AI 评审代理对修复后的代码做静态复检，结论为"通过"——21 项修复全部到位、方法签名与调用一致、无编译错误、未引入新缺陷；复检中提出的 4 处低风险小瑕疵（未使用 import、parseToInstant 空指针、lineTotal 判空、退款静默分支）也已一并清理。

*报告生成：WorkBuddy · 2026-08-29*
