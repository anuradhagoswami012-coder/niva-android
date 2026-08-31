package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter
    fun fromPlanCategory(value: PlanCategory): String = value.name

    @TypeConverter
    fun toPlanCategory(value: String): PlanCategory = try {
        PlanCategory.valueOf(value)
    } catch (e: Exception) {
        PlanCategory.GENERAL
    }

    @TypeConverter
    fun fromTaskSection(value: TaskSection): String = value.name

    @TypeConverter
    fun toTaskSection(value: String): TaskSection = try {
        TaskSection.valueOf(value)
    } catch (e: Exception) {
        TaskSection.TODAY
    }
}

@Dao
interface PlanDao {
    @Query("SELECT * FROM saved_plans WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllActivePlans(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM saved_plans WHERE id = :planId")
    suspend fun getPlanById(planId: Long): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity): Long

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Delete
    suspend fun deletePlan(plan: PlanEntity)

    @Query("DELETE FROM saved_plans WHERE id = :planId")
    suspend fun deletePlanById(planId: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM plan_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM plan_tasks WHERE planId = :planId ORDER BY id ASC")
    fun getTasksForPlan(planId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM plan_tasks WHERE section = 'TODAY' OR dueDate = 'Today' ORDER BY isCompleted ASC, createdAt DESC")
    fun getTodayTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM plan_tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getUnfinishedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM plan_tasks WHERE assignedToMemberId = :memberId ORDER BY isCompleted ASC")
    fun getTasksForMember(memberId: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM plan_tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM plan_tasks WHERE planId = :planId")
    suspend fun deleteTasksByPlanId(planId: Long)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM niva_memory ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM niva_memory WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("UPDATE niva_memory SET isEnabled = :enabled WHERE id = :id")
    suspend fun setMemoryEnabled(id: Long, enabled: Boolean)
}

@Dao
interface FamilyDao {
    @Query("SELECT * FROM family_members ORDER BY id ASC")
    fun getAllMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: FamilyMemberEntity): Long

    @Update
    suspend fun updateMember(member: FamilyMemberEntity)

    @Delete
    suspend fun deleteMember(member: FamilyMemberEntity)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteMemberById(id: Long)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget_items ORDER BY id ASC")
    fun getAllBudgetItems(): Flow<List<BudgetItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItem(item: BudgetItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BudgetItemEntity>)

    @Update
    suspend fun updateBudgetItem(item: BudgetItemEntity)

    @Delete
    suspend fun deleteBudgetItem(item: BudgetItemEntity)

    @Query("DELETE FROM budget_items")
    suspend fun clearBudget()
}
