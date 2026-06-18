package com.tirupati.pos.core.sync

interface RetryPolicy {
    fun nextDelayMillis(retryCount: Int): Long
}

/**
 * Simple exponential backoff policy placeholder
 */
class ExponentialBackoffPolicy(
    private val baseMillis: Long = 1000L,
    private val maxMillis: Long = 60_000L
) : RetryPolicy {
    override fun nextDelayMillis(retryCount: Int): Long {
        val calculated = baseMillis * (1L shl retryCount.coerceAtMost(30))
        return calculated.coerceAtMost(maxMillis)
    }
}
