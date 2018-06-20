/*
 *    Copyright [manerfan] [manerfan.china@gmail.com]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.manerfan.core.consistent

import java.util.*

/**
 * 一致性哈希工具工厂类
 */
class ConsistentHashHelper<T : PhysicalNode> {
    private var hashFunc: HashFunc? = null
    private var nodes: Collection<T>? = null
    private var replicas: Int = 0

    companion object {
        fun <U : PhysicalNode> create() = ConsistentHashHelper<U>()
    }

    /**
     * 使用自定义hash函数，默认md5
     */
    fun withHash(hashFunc: HashFunc): ConsistentHashHelper<T> {
        this.hashFunc = hashFunc
        return this
    }

    /**
     * 初始化节点
     */
    fun withNodes(nodes: Collection<T>, replicas: Int = 1): ConsistentHashHelper<T> {
        this.nodes = nodes
        this.replicas = replicas
        return this
    }

    /**
     * 构建一致性哈希工具
     */
    fun build(): ConsistentHash<T> {
        var consistentHash = ConsistentHash<T>(hashFunc ?: Md5())
        nodes?.let { nodes -> nodes.forEach { node -> consistentHash.add(node, replicas) } }
        return consistentHash
    }
}

/**
 * 一致性哈希工具
 */
class ConsistentHash<T : PhysicalNode>(private val hashFunc: HashFunc) {
    private var ring: SortedMap<Long, VirtualNode<T>> = TreeMap()

    /**
     * 添加节点
     *
     * @param node          (真实)节点
     * @param replicas      副本节点个数，默认1
     */
    fun add(node: T, replicas: Int = 1) {
        val existingReplicas = replicasNum(node.hashKey())

        (0..(if (replicas < 1) 0 else replicas - 1)).forEach {
            val vNode = VirtualNode(node, existingReplicas + it)
            ring[hashFunc.hash(vNode.hashKey())] = vNode
        }
    }

    /**
     * 移除(所有)节点
     */
    fun remove(key: String) {
        ring.keys
                .filter { ring[it]?.matches(key) ?: false }
                .forEach { ring.remove(it) }
    }

    /**
     * 根据key获取真实节点
     */
    fun getNode(key: String): T? {
        if (ring.isEmpty()) {
            return null
        }

        val hashKey = hashFunc.hash(key)
        val tail = ring.tailMap(hashKey)

        return ring[if (tail.isEmpty()) ring.firstKey() else tail.lastKey()]?.parent
    }

    fun replicasNum(key: String) = ring.values.filter { it.matches(key) }.size
}