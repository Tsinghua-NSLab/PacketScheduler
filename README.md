# Packet Scheduling

## 网包调度问题

PS目标是,有一系列input的packets, 如何进行重组然后进行dequeue
核心：enqueue和dequeue的操作，中间需要维护的数据结构
目标：吞吐量(低成本下实现高吞吐，effective)、公平性(efficient)、其他因素 stronger delay guarantees, burst-friendly fairness, starvation-free priorization of short flows
场景：纯软件、硬件加速(FPGA, SmartNIC, P4)

思考和讨论问题, 参见[Discussion](./GroupDiscussion/README.md)
Paper内各种工具的使用参见[Tools.md](./Tools.md)

## Project归纳

1. PIFO本质是order, virtual clock可以实现work-conserving, 是否有可能实现混用: 把order变到virtual-clock里, 解决PIFO的work-conserving缺陷? **本质上是Logical Clock和Real Clock之间的区别?** 需要深入阅读以下Logical Clock的知识和Lamport的TLA语言设计
2. 层级性的调度如何解决?

针对1的一种设计是: 前面使用PIFO，后面接着使用TimeWheel数据结构(使用list实现便于插入操作); 从而实现order和time两种要求。

1. 可以使用两个TW, 一个用来维护PIFO结构, 一个用来维护on-time的结构。对于on-time的queue, 时间性的要求更强, 需要更快地发送出去。带来的优势是，对于on-time的pkt良好, 但会存在对于PIFO类型的报文(见缝插针地进行调度)的饥饿问题。这里就可以和Loom的设计比较性能差异(因为Loom需要对pkt进行re-order操作)
2. 此外, 设计的时候不应该per-packet进行，而是per-flow地进行。每到处理1个flow的时候, 就从对应flow中进行dequeue操作(像Loom这样的设计可能带来flow内的pkts乱序问题: 需要实验验证乱序问题带来的较大的影响)

### 针对SP-PIFO的改进

因为SP-PIFO本质是把原来的order映射到更小的range的order(这个映射是动态调整的), 其实我们可以在此基础上增加固定的highest order来降低reversation? 也可以通过让**每个queue通过的pkt数量近似相等来实现最小的total reverse**?

## 文件夹说明

### 代码

./sp-pifo: 基于SPPIFO项目源码的修改

## Software Packet Scheduling

Fair Queueing
Carousel
Eiffel
