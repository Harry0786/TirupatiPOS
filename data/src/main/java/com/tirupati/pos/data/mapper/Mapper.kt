package com.tirupati.pos.data.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}
