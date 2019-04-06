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

    override fun getItems(userId: Int): Collection<UserItem> {
        val user = userRepository.findOne(userId)
        return mergeUserItemsWithBase(
                userItemRepository.findAllByUserIdEquals(userId),
                itemRepository.findAll(),
                { it.item.id },
                { it.id },
                { UserItem(0, it, user, 0) }
        )
    }

}
