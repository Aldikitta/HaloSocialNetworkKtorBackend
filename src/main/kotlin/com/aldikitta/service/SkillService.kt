package com.aldikitta.service

import com.aldikitta.data.models.Skill
import com.aldikitta.data.repository.skill.SkillRepository

class SkillService(
    private val repository: SkillRepository
) {
    suspend fun getSkills(): List<Skill> {
        return repository.getSkills()
    }
}