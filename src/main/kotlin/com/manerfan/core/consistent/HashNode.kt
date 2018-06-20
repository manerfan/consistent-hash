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

/**
 * 真实节点
 */
interface PhysicalNode {
    fun hashKey(): String
}

/**
 * 虚拟节点
 *
 * @param parent    真实节点
 * @param replica   虚拟节点id
 */
data class VirtualNode<out T : PhysicalNode>(val parent: T, private val replica: Int) : PhysicalNode {
    override fun hashKey() = "${parent.hashKey()}#$replica"
    fun matches(key: String) = parent.hashKey() == key
}

/**
 * 常规的服务节点
 *
 * @param name  服务名称
 * @param host  服务host/ip
 * @param port  服务port
 */
data class HostPortPhysicalNode(val name: String, val host: String, val port: Int) : PhysicalNode {
    override fun hashKey() = "$name:$host:$port"
}

