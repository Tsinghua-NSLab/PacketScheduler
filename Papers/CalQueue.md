# CalQueue

- 会议: NSDI'20
- 算法说明: 解决PIFO的动态性问题(需要动态调节priority)
- 针对问题: 很多schedule算法(如WFQ)翻译成PIFO的order时会发现映射成rank出现"infinite"
- 实现方式: 使用Physical Calendar Queue(Queue向前移动的时候要根据时间前进)可以implement work-conserving schemes(如EDF)和non-work(如Leaky Bucket Filter、Jitter-EDD、Stop-and-Go)，而Logical Calendar Queue对应work-conserving(LSTF、WFQ、SRPT)。文章里说明了如何用CQ来实现WFQ、EDF和LBF。
- 使用工具: CalQueue结构；使用3个case study验证性能(deadline-aware的co-flow/flow调度、FQ的variant--burstiness容忍进而可以在fairness和FCT之间平衡、pFabric的变种--通过逐渐增加all入队pkt的priority可以防止长流starve)。流量模型复用了pFabric(Sigcomm'13)的data mining workload。使用mptcp-htsim simulator来仿真pkt执行情况。
- 优点: 使用多个队列CalQueue解决单个queue不足的问题, 和MLFQ有些类似。可以理解成Carousel和PIFO的组合。
- 缺点: 存在feasiblity vs. accuracy的tradeoff(单个queue内存在inversion); 显著存在的问题是单次enqueue的range受限(不能任意插入某个rank的pkt----使用ACL切换的方法可以解决这个问题)
- 可能的后续方向: 现代schedule, 粗粒度 queue-level priority, 或者细粒度packet-level(PIFO)。文章里有提到，可以再使用SP-PIFO来reduce inversion; 处理bucket不足可以使用一个单独的queue然后在合适的时候(get close to their service time)做recirculate, 也可以使用hierarchical结构来增加queue数量。可以使用类似PC规则的排列插入(Network Algorithmic)，来实现更好的更新算法！(一个queue就可以实现多bucket的情况, 但是算法复杂度仍为log(N))。 **PIFO本质是order, virtual clock可以实现work-conserving, 是否有可能实现混用: 把order变到virtual-clock里, 解决PIFO的work-conserving缺陷?** **此外, hierarchical问题解决?**
- TODO: 研究**LSTF(Least Slack Time First)**、STFQ、FIFO+、AFQ、FDPA、PIAS、EDF， Calendar Queues
