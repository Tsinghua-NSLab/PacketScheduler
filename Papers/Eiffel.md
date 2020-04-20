# Eiffel

- 会议: NDSI'19
- 算法说明: 观察到packet rank基本有一个specific range, 多数packets有同样的rank, 这使得bucket-based priority queue非常有效。 定义bitmap-hierarchical结构(利用FindFirstSet新指令来加速)
- 针对问题: 网络需要10k数量级的rate limiter而网卡只支持10-128queues(在Introduction里的介绍); 多平台支持等需要我们使用Software来实现PS
- 实现方式: Qdisc, 在Linux Kernel中进行实现，参见[项目链接](https://github.com/saeed/eiffel_linux/tree/working_ffs-based_qdisc)
- TODO: OpenQueue(39)、hClock(19)
- PIEO:
- Carousel:
- PIFO:
- AFQ:
