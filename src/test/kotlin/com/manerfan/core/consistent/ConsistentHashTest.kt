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

import org.junit.jupiter.api.Test

class ConsistentHashTest {
    @Test
    fun simpleTest() {
        val a = HostPortPhysicalNode("A", "192.169.1.1", 8080)
        val b = HostPortPhysicalNode("B", "192.169.1.2", 8080)
        val c = HostPortPhysicalNode("C", "192.168.1.13", 8888)

        val consistentHash = ConsistentHashHelper.create<HostPortPhysicalNode>()
                .withNodes(listOf(a, b))
                .build()

        println("=========")
        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))

        println("=========")
        consistentHash.add(c)

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("=========")

        println("=========")
        consistentHash.remove(b.hashKey())

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("=========")

        println("=========")
        consistentHash.remove(a.hashKey())

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("=========")

        println("=========")
        consistentHash.remove(c.hashKey())

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("=========")
    }

    @Test
    fun simpleVirtualTest() {
        val a = HostPortPhysicalNode("A", "192.169.1.1", 8080)
        val b = HostPortPhysicalNode("B", "192.169.1.2", 8080)
        val c = HostPortPhysicalNode("C", "192.168.1.13", 8888)

        val consistentHash = ConsistentHashHelper.create<HostPortPhysicalNode>()
                .withNodes(listOf(a, b), 2)
                .build()

        println("=========")
        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("A: ${consistentHash.replicasNum(a.hashKey())}")
        println("B: ${consistentHash.replicasNum(b.hashKey())}")

        println("=========")
        consistentHash.add(c, -1)

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("A: ${consistentHash.replicasNum(a.hashKey())}")
        println("B: ${consistentHash.replicasNum(b.hashKey())}")
        println("C: ${consistentHash.replicasNum(c.hashKey())}")
        println("=========")

        println("=========")
        consistentHash.remove(b.hashKey())

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("A: ${consistentHash.replicasNum(a.hashKey())}")
        println("B: ${consistentHash.replicasNum(b.hashKey())}")
        println("C: ${consistentHash.replicasNum(c.hashKey())}")
        println("=========")

        println("=========")
        consistentHash.remove(a.hashKey())

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("A: ${consistentHash.replicasNum(a.hashKey())}")
        println("B: ${consistentHash.replicasNum(b.hashKey())}")
        println("C: ${consistentHash.replicasNum(c.hashKey())}")
        println("=========")

        println("=========")
        consistentHash.remove(c.hashKey())

        println(consistentHash.getNode("manerfan"))
        println(consistentHash.getNode("linxi"))
        println(consistentHash.getNode("linzhiyao"))
        println("A: ${consistentHash.replicasNum(a.hashKey())}")
        println("B: ${consistentHash.replicasNum(b.hashKey())}")
        println("C: ${consistentHash.replicasNum(c.hashKey())}")
        println("=========")
    }
}