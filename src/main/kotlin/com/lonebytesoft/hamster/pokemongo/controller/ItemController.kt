package com.lonebytesoft.hamster.pokemongo.controller

import com.lonebytesoft.hamster.pokemongo.service.ItemService
import com.lonebytesoft.hamster.pokemongo.view.ItemView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class ItemController
@Autowired
constructor(
        private val itemService: ItemService
) {

    @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf("/item/{userId}"))
    fun getItems(@PathVariable userId: Int): Map<Int, ItemView> {
        return itemService.getItems(userId)
                .mapValues {
                    val userItem = it.value
                    val item = userItem.item
                    ItemView(
                            userId,
                            item.id, item.name, item.probability,
                            userItem.have
                    )
                }
    }

}
