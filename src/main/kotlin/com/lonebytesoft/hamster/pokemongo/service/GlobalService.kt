package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.Global

interface GlobalService {

    fun get(userId: Int): Global

    fun save(userId: Int, global: Global)

}
