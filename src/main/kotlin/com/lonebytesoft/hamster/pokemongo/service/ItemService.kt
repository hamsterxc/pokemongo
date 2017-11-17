package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.UserItem

interface ItemService {

    fun getItems(userId: Int): Map<Int, UserItem>

}
