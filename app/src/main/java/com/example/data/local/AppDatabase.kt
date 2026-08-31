package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlanEntity::class,
        TaskEntity::class,
        MemoryEntity::class,
        FamilyMemberEntity::class,
        BudgetItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    abstract fun taskDao(): TaskDao
    abstract fun memoryDao(): MemoryDao
    abstract fun familyDao(): FamilyDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "niva_app_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            // Seed default family members
            val me = FamilyMemberEntity(name = "Anuradha", relationship = "Me", avatarColorHex = "#5C7862", emoji = "🌸")
            val partner = FamilyMemberEntity(name = "Rahul", relationship = "Partner", avatarColorHex = "#C86D51", emoji = "💼")
            val mother = FamilyMemberEntity(name = "Mummy", relationship = "Mother", avatarColorHex = "#8F7D6B", emoji = "🪷")
            val child = FamilyMemberEntity(name = "Aarav", relationship = "Child", avatarColorHex = "#6E8B99", emoji = "🎨")

            database.familyDao().insertMember(me)
            database.familyDao().insertMember(partner)
            database.familyDao().insertMember(mother)
            database.familyDao().insertMember(child)

            // Seed transparent NIVA memories
            val m1 = MemoryEntity(title = "Family Size", category = "Family", value = "4 members (2 adults, 1 child, 1 elder)")
            val m2 = MemoryEntity(title = "Dietary Preference", category = "Food", value = "Predominantly Vegetarian, Jain meals on Tuesday & Ekadashi")
            val m3 = MemoryEntity(title = "Language Mode", category = "Preferences", value = "Hinglish / English (Warm, concise)")
            val m4 = MemoryEntity(title = "Household Help", category = "Routines", value = "Kamla Didi arrives at 8:30 AM (Cooking & Cleaning)")
            val m5 = MemoryEntity(title = "Monthly Expense Baseline", category = "Money", value = "Approx ₹45,000 / month")

            database.memoryDao().insertMemory(m1)
            database.memoryDao().insertMemory(m2)
            database.memoryDao().insertMemory(m3)
            database.memoryDao().insertMemory(m4)
            database.memoryDao().insertMemory(m5)

            // Seed sample initial plan
            val planId = database.planDao().insertPlan(
                PlanEntity(
                    title = "Weekend Family Lunch & Chores",
                    category = PlanCategory.GUESTS,
                    originalPrompt = "Mummy and Papa visiting on Sunday afternoon. Need light satvik lunch and house prep.",
                    summary = "6 items organized for Sunday family visit.",
                    estimatedBudget = "₹1,200"
                )
            )

            // Seed today's tasks
            database.taskDao().insertAllTasks(
                listOf(
                    TaskEntity(
                        planId = planId,
                        title = "Order fresh Paneer & mint leaves from Blinkit",
                        section = TaskSection.SHOPPING,
                        isCompleted = false,
                        priority = "HIGH",
                        dueDate = "Today",
                        timeEstimate = "10 mins"
                    ),
                    TaskEntity(
                        planId = planId,
                        title = "Tell Kamla Didi to make Paneer Makhani (no garlic) & Phulkas",
                        section = TaskSection.TODAY,
                        isCompleted = false,
                        priority = "HIGH",
                        dueDate = "Today",
                        timeEstimate = "5 mins"
                    ),
                    TaskEntity(
                        planId = planId,
                        title = "Keep clean towels & water jug in guest room",
                        section = TaskSection.HOME,
                        isCompleted = true,
                        priority = "NORMAL",
                        dueDate = "Today",
                        timeEstimate = "15 mins"
                    ),
                    TaskEntity(
                        planId = planId,
                        title = "Pay electricity bill before 6 PM",
                        section = TaskSection.MONEY,
                        isCompleted = false,
                        priority = "HIGH",
                        dueDate = "Today",
                        timeEstimate = "2 mins"
                    )
                )
            )

            // Seed standard budget categories
            val budgetItems = listOf(
                BudgetItemEntity(categoryName = "Groceries & Vegetables", plannedAmount = 14000.0, actualAmount = 8200.0),
                BudgetItemEntity(categoryName = "Domestic Help & Cook", plannedAmount = 8500.0, actualAmount = 8500.0),
                BudgetItemEntity(categoryName = "Utilities & Wifi", plannedAmount = 4500.0, actualAmount = 3100.0),
                BudgetItemEntity(categoryName = "Child & School Activities", plannedAmount = 7000.0, actualAmount = 4000.0),
                BudgetItemEntity(categoryName = "Household & Maintenance", plannedAmount = 3500.0, actualAmount = 1200.0),
                BudgetItemEntity(categoryName = "Personal & Medicines", plannedAmount = 3500.0, actualAmount = 2100.0),
                BudgetItemEntity(categoryName = "Monthly Savings Target", plannedAmount = 10000.0, actualAmount = 10000.0)
            )
            database.budgetDao().insertAll(budgetItems)
        }
    }
}
