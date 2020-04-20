# Loom

- 会议: NSDI'19
- 算法说明: 1. 使用DAG表示Policy abstraction; 2. Programmable Hierarchical PS; 3. OS interface
- 针对问题: 纯software的PS开销过大, 使用NIC加速有imperfect的问题; 需要定义NIC PS和OS/NIC interface。其中**DAG Policy Abstraction**是重点内容, 定义了两种node: scheduling(order)和shaping(rate-limit)。
设计新的NIC来把schedule从OS放到NIC上(支持hierarchical)，
- 实现方式: 设计新的scheduling hierarchy, 新的OS/NIC interface。 shaping的时候可以
- 工具: 设计基于BESS和Domino。Domino: 现有的scheduling tree的compiler; prototype使用BESS(通过修改BESS kernel driver来实现OS/NIC的Loom的interface); 此外Loom还修改了PIFO的C++实现。使用iperf3(测量throughput)和sockperf(测量端到端latency)，使用Spark with the TeraSort benchmark来perform a 25GB shufffle, 使用50ms的窗口来计算throughput。使用CloudLab(cloudlab.us)来进行send data between two servers.
- 可能的后续方向: policy有两种, work conserving的scheduling(确定pkt的relative order)和rate-limiting的shaping(确定pkt的time)。 network-wide的policy across a cluster of servers是一个方向。**Network层面的policy尚不清楚怎么处理(目前的policies都集中在单个node上)。** **如果DAG过大，或者算法过于复杂，就会拒绝策略部署到NIC上: 因此,这里如何进行扁平化操作显得非常重要!**
-TODO: 研究NSDI'11的Sharing the data center network(50); NSDI'13 EyeQ(30); Sigcomm'11 Chatty tenants and the cloud network sharing problem (10); Sigcomm'15 BwE: Flexible, hierarchical bandwidth allocation for WAN distributed computing(32); Sigcomm'15 Silo: Predictable message latency in the Cloud(29); Sigcomm'12 FairCLoud: Sharing the network in cloud computing (41); Sigcomm'07 Cloud control with distributed rate limiting (44).
PSPAT:Software Packet scheduling at hardware speed(46)

## Policy的抽象

Policy可以抽象成DAG。

### Node类型

有两种non-leaf nodes.

1. work-conserving的scheduling nodes, 定义了不同pkt的relative order。通过enqueue和dequeue表达出来。
2. rate-limiting的shaping nodes，决定了pkt的timing。

每个node关联specific的算法，Loom原生设计了Pri、RL、WFQ、LSTF等算法。work-conserving policy可以保证带宽共享，而rate-limiting可以严格管理bandwidth。

### Hierarchy的要求

1. 移除shaping node后构成1棵树
2. shaping node可能是并行的shaping nodes的nested set(嵌套集合: 类似俄罗斯套娃、类似线段树结构)
3. node的parent是scheduling, 那么它只能有一个parent，如果是shaping、则可以有多个parents。
4. traffic聚集在一起来做scheduling后，他们只能separated for shaping。scheduling不可以reorder子节点已经order的pkt，但仍然可以做separate per-destination/per-path的rate-limit。
5. 在进入DAG结构前, 所有的metadata都要准备好: per-socket priorities, socket IDs, process IDs, cgroup names, virtual interface IDs, dest-ip等。

### 评价这种抽象

可以很好地解决在有priority的同时做到rate-limit(比如pFabric的饥饿问题)。
存在1个问题是，policies是local的；在DC中，express network-wide policies across a cluster of servers这种需求是无法被满足的。
还有1个问题是，无法保证efficient，也就是policy is not well mapped onto the unerlaying hardware。为了避免这种情况，设置NIC拒绝non-efficient的policies！

## Loom的设计

### Programmable Scheduling Background

PIFO具有极强的表达性, WFQ可以通过virtual clock来order, TB可以通过计算wall clock transmit time来实现。
在Hierarchy的表达中，pkt进入到leaf-Q，leaf-Q的指针放到parent Q，然后parent Q发现出包了1个leaf-Q就调用leaf-Q。
在Rate limit的表达中，naive的PIFO设计只能针对单个PIFO queue做限速，而不能对PIFO内的各个flow分别做限速。

### Programmable scheduling for NICs

Scheduling Operations的分工：因为在end-host上，NIC需要从memory获得pkt信息，所以metadata不如直接交给OS来完成(不然的话，NIC要从pkt descriptor通过DMA读pkt信息计算rank，然后等到dequeue的时候再用DMA来出包)，由OS通过doorbell告知NIC；这种设计是为了通过增加descriptor和doorbell size来换取占用较少的NIC SRAM。
DAG Rate Limiting：修改PIFO使其能够实现单PIFO下多rate-limit：每个pkt在计算rank之外额外计算wall-clock time，然后正常把pkt压入PIFO中。出包的时候，如果time已过则直接出包，否则就把pkt压到shaping queue中(按照clock压入)，shaping queue中的pkt等到wall-clock的时间达到后再重新压入PIFO中。最坏情况下，per-pkt都需要2次，但这个损失下仍然能够达到100Gbps的限速。还可以采用一些额外的优化，1. 使用flag来表示目前rate limit class是否被限速了, 这样可以直接把pkt放到shaping queue中； 2. 限制traffic class中未完成(outstanding)的数据包的数量。如果Hierarchy过深的话，也会带来问题，但根据一般性分析来说10 PIFOs已经足够。

### OS/NIC interface

1. Batched Doorbells：借用现代的OS/NIC常用的batch操作，16b形成1个doorbell descriptor，32个doorbell恰好1个cacheline，可以在1个PCIe write内实现
2. Scheduling Metadata：讨论segment和packet对应的metadata的处理
3. Discussion：对于各种NIC(ASIC、FPGA、NP、virtual)，Loom都适用；而且对于kernel bypass的DPDK、RDMA、netmap都可以兼容

## TODO

继续阅读以下papers:

1. PSPAT(46), Software packet scheduling at hardware speed
2. Sigcomm'07, Cloud control with distributed rate limiting
3. netmap: a novel framework for fast packet I/O
4. Sigcomm'12, FairCloud: Sharing the network in cloud computing
5. NDSI'14, SENIC: Scalable NIC for end-host rate limiting
