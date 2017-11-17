package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.UserItem
import com.lonebytesoft.hamster.pokemongo.repository.ItemRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserItemRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ItemServiceImpl
@Autowired
constructor(
        private val userRepository: UserRepository,
        private val itemRepository: ItemRepository,
        private val userItemRepository: UserItemRepository
) : ItemService {

    override fun getItems(userId: Int): Map<Int, UserItem> {
        val items: MutableMap<Int, UserItem> = userItemRepository.findAllByUserIdEquals(userId)
                .associateByTo(HashMap(), { it.item.id })

        val user = userRepository.findOne(userId)
        itemRepository.findAll()
                .filter { it.id !in items }
                .map { UserItem(0, it, user) }
                .associateByTo(items, { it.item.id })

        return items
    }

}
