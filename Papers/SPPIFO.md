# SP-PIFO

- 会议: NSDI'20
- 算法说明: 使用多FIFO队列来模拟PIFO
- 实现方式: 软件-Java(基于NetBench)来比较schedule schemes, 对比FIFO、gradient-based algorithm(rank排序效果、inversion的数量), 进而分析出SP-PIFO的设计空间; 硬件-P4(基于MiniNet)来进行实际实验(200LOC-$P4_{16}$),对比了LSTF、STFQ、FIFO+, 目标是: 最小化FCTs和提高fairness。为了比较FCT, 使用的traffic model是pFabric web application和data mining(流到达是遵循Poisson分布), 其中pFabric是基于remaining flow sizes来最小化FCT(要使用PIFO或SP-PIFO), 同时比较了传统TCP和DCTCP算法。 为了比较fairness across flows, 在PIFO/SP-PIFO之上实现STFQ(Start-Time Fair Queueing), 进而分析不同flow sizes和queues数量下的performance, 比对的基线是AFQ。
- 使用工具: Moongen(流量产生)、pFabric、P4等
- 优点: 减少PIFO开销, 其他的各项实验本质上是PIFO的好处(比如pFabric减小FCT等)
- 缺点: 继承了PIFO的缺点(不能rate-limit egress throughput,进而不能实现non-work-conserving scheduling algorithm; 不能直接实现hierarchical scheduling; 但是可以通过多个PIFO模拟hierarchical)。 SP-PIFO可能存在queue数量和accuracy之间的tradeoff(目前switch支持32 queues/port)。 可能存在对抗流量作为attack
- 可能的后续方向: 多个PIFO模拟hierarchical, 在PIFO文章中有提到可以通过recirculate packet(多次access queues)来实现, 但是如何减少对性能的影响未能解决。Hierarchical的核心问题在于，插入一个新报文后，所有pkt的顺序会发生变化（而且存在不定的变化结果！）；需要研究hierarchical结构对原始的pkt queue的影响(在CalQueue文章中也提到了这一点: PIFO使得pkt在enqueue之后relative order不能变化, 使得PIFO不能实现pFabric的starvation prevention technique) **SP-PIFO中的theoretical差别: 充分模拟PIFO和性能之间(多个queue)的trade-off** **SP-PIFO存在对抗流量: SP-PIFO本质是对rank distribution的模拟, 存在对抗性流量** **Facilitate(促进) PIFO，由于硬件改进带来的可以在enqueue时更精准地预测unpifoness**
- TODO: 研究**LSTF**、STFQ、FIFO+、AFQ、FDPA、PIAS、EDF, PIFO不能实现non-work-conserving scheduling?
