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

### A new scheduling hierarchy

### OS/NIC interface
