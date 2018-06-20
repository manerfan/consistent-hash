# 一致性哈希 consistent-hash

Implementing Consistent Hashing in Kotlin

Java Kotlin实现的一致性哈希工具

## 简单示例

```kotlin
val a = HostPortPhysicalNode("A", "192.169.1.1", 8080)
val b = HostPortPhysicalNode("B", "192.169.1.2", 8080)
val c = HostPortPhysicalNode("C", "192.168.1.13", 8888)

val consistentHash = ConsistentHashHelper.create<HostPortPhysicalNode>()
        .withNodes(listOf(a, b))
        .build()

consistantHash.getNode("manerfan")
consistantHash.getNode("linxi")

consistentHash.add(c)
consistantHash.getNode("linxi")

consistantHash.remove(a.hashKey())
consistantHash.getNode("manerfan")
```

## 自定义物理节点

实现`PhysicalNode`接口及`hashKey`方法，ConsistantHash将通过hashKey的返回计算节点的哈希值

## 自定义哈希函数

实现`HashFunc`接口及`hash`方法

## ConsistentHashHelper

使用`ConsistentHashHelper`快速构造`ConsistentHash`

`withHash(Hash)`方法用于指定自定义哈希函数

`withNodes(Collection<PhysicalNode>[, ReplicasNum])`用于在构造`ConsistentHash`时初始化节点，同时可指定每个节点的副本个数

```kotlin
val a = MyPhysicalNode("192.169.1.1", 8080)
val b = MyPhysicalNode("192.169.1.2", 8080)

val consistentHash = ConsistentHashHelper.create<MyPhysicalNode>()
        .withHash(customHash)
        .withNodes(listOf(a, b))
        .build()
```

## ConsistentHash

`add(PhysicalNode[, ReplicasNum])` 动态增加节点，同时可指定副本个数 

`remove(key)` 动态删除节点

`getNode(key)` 计算该key应该落到的物理节点



