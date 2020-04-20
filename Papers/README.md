# 文献阅读

文献阅读放在了OneNote: 网络研究/流量控制/已有项目代码下。

NSDI'20上的两篇文章, CalQueue和SP-PIFO都是在探究如何使用P4(资源有限)来实现Packet Scheudling。
NSDI'19上的Loom则是针对在NIC上做Packet Scheduling(使用软件NIC来做模拟), 里面使用了Domino来实现scheduling trees
NSDI'19上的Eiffel

## TODO

- [x] Loom的深入阅读理解
- [ ] Eiffel的阅读理解
- [ ] 阅读关于DC的tenant fairness的文章, Ether: Providing both Interactive Service and Fairness in Multi-Tenant Datacenters
- [ ] 阅读Hierarchical Policies的文章, Ward: Implementing Arbitrary Hierarchical Policies using Packet Resubmit in Programmable Switches

## Future work

- [ ] 研究express network-wide policies across a cluster of servers(注意参考Loom文章里的内容), 可以参考paper: Sigcomm'07, Cloud control with distributed rate limiting
- [ ] Loom中提到，拒绝non-efficient的policies。可以通过计算，评价1个policy是否足够efficient？
- [ ] UPS证明了PIFO可以表示任意的scheduling algorithm?(Loom中提到这一点)
- [ ] 是否有可能利用时间片的概念, 把所有的pkt都转化成real-time?
- [ ] Loom设计的带有rate-limit的PIFO的限速准确性?
- [ ] 把Calendar Queue不设置为physical的时间，而是per-pkt出包后就旋转时间片?
- [ ] 似乎使用PIFO可以直接表示出来HTB??? 换言之, 每个pkt的rank应该如何计算? 如何根据total scenario做调整?

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
