package com.example.data

object AchievementCatalog {
    val initialAchievements = listOf(
        AchievementRecord(
            id = "first_step",
            title = "First Step",
            description = "Selesaikan level pertamamu di BrainQuest 3D",
            icon = "flag",
            maxProgress = 1
        ),
        AchievementRecord(
            id = "brain_starter",
            title = "Brain Starter",
            description = "Selesaikan 5 level laboratorium otak",
            icon = "psychology",
            maxProgress = 5
        ),
        AchievementRecord(
            id = "puzzle_master",
            title = "Puzzle Master",
            description = "Taklukkan 20 level dengan pemikiran analitis",
            icon = "extension",
            maxProgress = 20
        ),
        AchievementRecord(
            id = "master_mind",
            title = "Master Mind",
            description = "Selesaikan seluruh 30 level Brain Lab 3D",
            icon = "workspace_premium",
            maxProgress = 30
        ),
        AchievementRecord(
            id = "no_mistake",
            title = "No Mistake",
            description = "Selesaikan sebuah level tanpa kehilangan satu pun nyawa",
            icon = "favorite",
            maxProgress = 1
        ),
        AchievementRecord(
            id = "speed_brain",
            title = "Speed Brain",
            description = "Pecahkan puzzle dalam waktu kurang dari 30 detik",
            icon = "bolt",
            maxProgress = 1
        ),
        AchievementRecord(
            id = "daily_dedication",
            title = "Daily Dedication",
            description = "Selesaikan Tantangan Harian untuk pertama kalinya",
            icon = "event",
            maxProgress = 1
        ),
        AchievementRecord(
            id = "streak_fire",
            title = "Streak Fire",
            description = "Capai 3 hari streak berturut-turut",
            icon = "local_fire_department",
            maxProgress = 3
        ),
        AchievementRecord(
            id = "coin_collector",
            title = "Coin Tycoon",
            description = "Kumpulkan total 500 Coin laboratorium",
            icon = "monetization_on",
            maxProgress = 500
        ),
        AchievementRecord(
            id = "perfect_stars",
            title = "Bintang Sempurna",
            description = "Raih 3 bintang pada 10 level berbeda",
            icon = "star",
            maxProgress = 10
        )
    )
}
