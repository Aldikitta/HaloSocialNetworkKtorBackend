package com.aldikitta.data.repository.skill

import com.aldikitta.data.models.Skill

interface SkillRepository {
    suspend fun getSkills(): List<Skill>
}