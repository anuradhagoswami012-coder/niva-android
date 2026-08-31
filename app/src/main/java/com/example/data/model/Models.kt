package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlanCategory(val displayName: String, val iconEmoji: String) {
    GENERAL("General", "✨"),
    GUESTS("Guests Coming", "👥"),
    COOKING("Meal & Cooking", "🍲"),
    EMPTY_HEAD("Brain Dump", "🧠"),
    MONEY("Money & Budget", "💰"),
    OCCASION("Occasions", "🎉"),
    HELP_NOW("Rapid Action", "🚨"),
    HOUSEHOLD("Household", "🏡")
}

enum class TaskSection(val displayName: String) {
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    THIS_WEEK("This Week"),
    SHOPPING("Shopping List"),
    HOME("Home & Chores"),
    FAMILY("Family"),
    MONEY("Money"),
    MENU("Menu"),
    QUANTITIES("Quantities & Groceries"),
    PREPARATION("Preparation Timeline"),
    COOKING_ORDER("Cooking Order"),
    SERVING("Serving Checklist"),
    LAST_MINUTE("Last-Minute Prep"),
    LATER("Later")
}

@Entity(tableName = "saved_plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: PlanCategory = PlanCategory.GENERAL,
    val originalPrompt: String = "",
    val summary: String = "",
    val estimatedBudget: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false
)

@Entity(tableName = "plan_tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long? = null, // null for standalone daily tasks
    val title: String,
    val section: TaskSection = TaskSection.TODAY,
    val isCompleted: Boolean = false,
    val priority: String = "NORMAL", // "HIGH", "NORMAL", "LOW"
    val assignedToMemberId: Long? = null,
    val assignedMemberName: String? = null,
    val dueDate: String = "Today",
    val timeEstimate: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "niva_memory")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // e.g. "Family Size", "Food Preferences", "Language", "Budget", "Household"
    val value: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationship: String, // e.g. "Me", "Partner", "Mother", "Father", "Child", "Other"
    val avatarColorHex: String = "#5C7862",
    val emoji: String = "👤",
    val notes: String = ""
)

@Entity(tableName = "budget_items")
data class BudgetItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryName: String,
    val plannedAmount: Double,
    val actualAmount: Double = 0.0,
    val currencySymbol: String = "₹",
    val notes: String = ""
)

data class StructuredPlan(
    val title: String,
    val category: PlanCategory,
    val empatheticIntro: String,
    val sections: List<PlanSectionData>,
    val estimatedBudget: String = "",
    val missingQuestions: List<String> = emptyList(),
    val actionableNextStep: String = ""
)

data class PlanSectionData(
    val section: TaskSection,
    val title: String,
    val items: List<String>
)
