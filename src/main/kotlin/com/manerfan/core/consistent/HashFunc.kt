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

import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import java.security.MessageDigest

/**
 * 哈希函数
 */
interface HashFunc {
    fun hash(str: String): Long
}

/**
 * md5哈希
 */
class Md5 : HashFunc {
    private var md5DigestPool: GenericObjectPool<MessageDigest>

    override fun hash(str: String): Long {
        val md5 = md5DigestPool.borrowObject()
        try {
            md5.reset()
            md5.update(str.toByteArray())
            val bytes = md5.digest()

            var h: Long = 0
            bytes.forEach {
                h = h shl 8
                h = h or (it.toLong() and 0xFF)
            }
            return h
        } finally {
            md5DigestPool.returnObject(md5)
        }
    }

    private fun md5DigestPoolFactory(): BasePooledObjectFactory<MessageDigest> {
        return object : BasePooledObjectFactory<MessageDigest>() {
            override fun create() = MessageDigest.getInstance("MD5")
            override fun wrap(messageDigest: MessageDigest) = DefaultPooledObject(messageDigest)
        }
    }

    init {
        val poolConfig = GenericObjectPoolConfig()

        /* 设置池中最大idle，若idle数量大于此值，则会清理多余idle */
        poolConfig.maxIdle = 100
        /* 设置多久没有borrow则设置为idle */
        poolConfig.minEvictableIdleTimeMillis = (5 * 60 * 1000).toLong()

        /* 设置池中最小idle，若idle数量小于此值，则在Evictor定时器中会自动创建idle */
        poolConfig.minIdle = 50
        /* 设置Evictor定时器周期并启动定时器 */
        poolConfig.timeBetweenEvictionRunsMillis = (30 * 1000).toLong()

        /* 设置池中最大数量，若达到上限时borrow，则阻塞 */
        poolConfig.maxTotal = 500

        /* 调用者最大阻塞的时间 */
        poolConfig.maxWaitMillis = (5 * 1000).toLong()

        md5DigestPool = GenericObjectPool(md5DigestPoolFactory(), poolConfig)
        // 初始化池中idle个数到minIdle，提前create，免得在使用时再创建
        // BaseGenericObjectPool.Evictor定时器会定时执行ensureMinIdle，确保idle keypare个数可以达到minIdle
        md5DigestPool.preparePool()
    }
}