# 文献阅读

文献阅读放在了OneNote: 网络研究/流量控制/已有项目代码下。

NSDI'20上的两篇文章, CalQueue和SP-PIFO都是在探究如何使用P4(资源有限)来实现Packet Scheudling。
NSDI'19上的Loom则是针对在NIC上做Packet Scheduling(使用软件NIC来做模拟)
NSDI'19上的Eiffel

## TODO

- [ ] Loom的深入阅读理解
- [ ] Eiffel的阅读理解

## Future work

- [ ] 研究express network-wide policies across a cluster of servers(注意参考Loom文章里的内容)
- [ ] Loom中提到，拒绝non-efficient的policies。可以通过计算，评价1个policy是否足够efficient？
- [ ] UPS证明了PIFO可以表示任意的scheduling algorithm?(Loom中提到这一点)
- [ ] 是否有可能利用时间片的概念, 把所有的pkt都转化成real-time?

### OpenQueue

- 会议: ICNP'17

### hClock

- 会议: EuroSys'13
这篇文章非常重要, 里面详细阐述了Hierarchical PS的相关设计思想和说明!
定义了tag和Clock(Reservation, Limit, Shared)来控制层级性的scheduler的调度: 有点像把HTB变成clock控制

- CBQ算法
- Virtual Clock说明
- HFSC算法
- HTB算法

### Universal Packet Scheduling

- 会议: NSDI'16
- 目标: UPS的定义在于, 理论上可以replay any schedule, 实际上是可以achieve不同的performance objectives(fairness, tail latency, FCT等, 而且是能够replay已知的最好的scheduling算法)。
