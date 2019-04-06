package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.UserFamily
import com.lonebytesoft.hamster.pokemongo.repository.FamilyRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserFamilyRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FamilyServiceImpl
@Autowired
constructor(
        private val userRepository: UserRepository,
        private val familyRepository: FamilyRepository,
        private val userFamilyRepository: UserFamilyRepository
): FamilyService {
    
    override fun getFamilies(userId: Int): Collection<UserFamily> {
        val user = userRepository.findOne(userId)
        return mergeUserItemsWithBase(
                userFamilyRepository.findAllByUserIdEquals(userId),
                familyRepository.findAll(),
                { it.family.number },
                { it.number },
                { UserFamily(0, it, user, 0) }
        )
    }
    
}
