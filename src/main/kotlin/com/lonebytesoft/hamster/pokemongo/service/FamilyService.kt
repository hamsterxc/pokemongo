package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.UserFamily

interface FamilyService {

    fun getFamilies(userId: Int): Collection<UserFamily>

}
