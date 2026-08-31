package com.example.data.repository

import com.example.data.ai.NivaIntelligenceEngine
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class NivaRepository(
    private val database: AppDatabase,
    private val aiEngine: NivaIntelligenceEngine
) {
    val allPlans: Flow<List<PlanEntity>> = database.planDao().getAllActivePlans()
    val todayTasks: Flow<List<TaskEntity>> = database.taskDao().getTodayTasks()
    val unfinishedTasks: Flow<List<TaskEntity>> = database.taskDao().getUnfinishedTasks()
    val allMemories: Flow<List<MemoryEntity>> = database.memoryDao().getAllMemories()
    val allFamilyMembers: Flow<List<FamilyMemberEntity>> = database.familyDao().getAllMembers()
    val allBudgetItems: Flow<List<BudgetItemEntity>> = database.budgetDao().getAllBudgetItems()

    fun getTasksForPlan(planId: Long): Flow<List<TaskEntity>> = database.taskDao().getTasksForPlan(planId)
    fun getTasksForMember(memberId: Long): Flow<List<TaskEntity>> = database.taskDao().getTasksForMember(memberId)

    suspend fun analyzeProblem(
        prompt: String,
        categoryHint: PlanCategory? = null,
        preferences: Map<String, String> = emptyMap()
    ): StructuredPlan {
        return aiEngine.analyzeAndCreatePlan(prompt, categoryHint, preferences)
    }

    suspend fun saveStructuredPlan(
        structuredPlan: StructuredPlan,
        originalPrompt: String
    ): Long {
        val plan = PlanEntity(
            title = structuredPlan.title,
            category = structuredPlan.category,
            originalPrompt = originalPrompt,
            summary = structuredPlan.empatheticIntro,
            estimatedBudget = structuredPlan.estimatedBudget,
            createdAt = System.currentTimeMillis()
        )
        val planId = database.planDao().insertPlan(plan)

        val taskEntities = mutableListOf<TaskEntity>()
        for (section in structuredPlan.sections) {
            for (item in section.items) {
                taskEntities.add(
                    TaskEntity(
                        planId = planId,
                        title = item,
                        section = section.section,
                        dueDate = if (section.section == TaskSection.TOMORROW) "Tomorrow" else "Today",
                        priority = if (section.section == TaskSection.TODAY || section.section == TaskSection.LAST_MINUTE) "HIGH" else "NORMAL"
                    )
                )
            }
        }
        database.taskDao().insertAllTasks(taskEntities)
        return planId
    }

    suspend fun updateTask(task: TaskEntity) = database.taskDao().updateTask(task)
    suspend fun insertTask(task: TaskEntity) = database.taskDao().insertTask(task)
    suspend fun deleteTask(task: TaskEntity) = database.taskDao().deleteTask(task)
    suspend fun deleteTaskById(taskId: Long) = database.taskDao().deleteTaskById(taskId)

    suspend fun deletePlanById(planId: Long) {
        database.taskDao().deleteTasksByPlanId(planId)
        database.planDao().deletePlanById(planId)
    }

    suspend fun updatePlan(plan: PlanEntity) = database.planDao().updatePlan(plan)

    // Memory operations
    suspend fun insertMemory(memory: MemoryEntity) = database.memoryDao().insertMemory(memory)
    suspend fun deleteMemory(memory: MemoryEntity) = database.memoryDao().deleteMemory(memory)
    suspend fun deleteMemoryById(id: Long) = database.memoryDao().deleteMemoryById(id)
    suspend fun setMemoryEnabled(id: Long, enabled: Boolean) = database.memoryDao().setMemoryEnabled(id, enabled)

    // Family operations
    suspend fun insertFamilyMember(member: FamilyMemberEntity) = database.familyDao().insertMember(member)
    suspend fun deleteFamilyMember(member: FamilyMemberEntity) = database.familyDao().deleteMember(member)
    suspend fun deleteFamilyMemberById(id: Long) = database.familyDao().deleteMemberById(id)

    // Budget operations
    suspend fun insertBudgetItem(item: BudgetItemEntity) = database.budgetDao().insertBudgetItem(item)
    suspend fun updateBudgetItem(item: BudgetItemEntity) = database.budgetDao().updateBudgetItem(item)
    suspend fun deleteBudgetItem(item: BudgetItemEntity) = database.budgetDao().deleteBudgetItem(item)
}
