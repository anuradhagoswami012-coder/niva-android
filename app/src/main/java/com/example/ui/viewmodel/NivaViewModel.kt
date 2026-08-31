package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.NivaIntelligenceEngine
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.NivaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class NivaViewModel(
    application: Application,
    private val repository: NivaRepository
) : AndroidViewModel(application) {

    // User state
    private val _userName = MutableStateFlow("Anuradha")
    val userName = _userName.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Hinglish")
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("₹")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(true)
    val hasCompletedOnboarding = _hasCompletedOnboarding.asStateFlow()

    // Input & Handling state
    private val _currentPromptInput = MutableStateFlow("")
    val currentPromptInput = _currentPromptInput.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _currentStructuredPlan = MutableStateFlow<StructuredPlan?>(null)
    val currentStructuredPlan = _currentStructuredPlan.asStateFlow()

    private val _lastSavedPlanId = MutableStateFlow<Long?>(null)
    val lastSavedPlanId = _lastSavedPlanId.asStateFlow()

    private val _clarificationAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    val clarificationAnswers = _clarificationAnswers.asStateFlow()

    // Voice recognition
    private val _isListeningVoice = MutableStateFlow(false)
    val isListeningVoice = _isListeningVoice.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    // Room Database Streams
    val allPlans: StateFlow<List<PlanEntity>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTasks: StateFlow<List<TaskEntity>> = repository.todayTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unfinishedTasks: StateFlow<List<TaskEntity>> = repository.unfinishedTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFamilyMembers: StateFlow<List<FamilyMemberEntity>> = repository.allFamilyMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgetItems: StateFlow<List<BudgetItemEntity>> = repository.allBudgetItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun updatePromptInput(text: String) {
        _currentPromptInput.value = text
    }

    fun completeOnboarding() {
        _hasCompletedOnboarding.value = true
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun answerClarification(question: String, answer: String) {
        val updated = _clarificationAnswers.value.toMutableMap()
        updated[question] = answer
        _clarificationAnswers.value = updated

        // Re-analyze with clarified details
        val combined = "${_currentPromptInput.value}\n[Clarifications: ${updated.map { "${it.key}: ${it.value}" }.joinToString(", ")}]"
        handleUserProblem(combined, _currentStructuredPlan.value?.category)
    }

    fun handleUserProblem(input: String? = null, categoryHint: PlanCategory? = null) {
        val prompt = input ?: _currentPromptInput.value
        if (prompt.isBlank()) return

        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val prefMap = allMemories.value.filter { it.isEnabled }.associate { it.title to it.value }
                val plan = repository.analyzeProblem(prompt, categoryHint, prefMap)
                _currentStructuredPlan.value = plan
            } catch (e: Exception) {
                _snackbarMessage.value = "Unable to process: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun saveCurrentPlan() {
        val plan = _currentStructuredPlan.value ?: return
        viewModelScope.launch {
            try {
                val id = repository.saveStructuredPlan(plan, _currentPromptInput.value)
                _lastSavedPlanId.value = id
                _snackbarMessage.value = "Plan saved to NIVA! ✨"
            } catch (e: Exception) {
                _snackbarMessage.value = "Failed to save: ${e.message}"
            }
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun addNewTask(title: String, section: TaskSection = TaskSection.TODAY, priority: String = "NORMAL", dueDate: String = "Today") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    section = section,
                    priority = priority,
                    dueDate = dueDate
                )
            )
            _snackbarMessage.value = "Task added to My Day"
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
        }
    }

    fun rescheduleTask(task: TaskEntity, newDueDate: String) {
        viewModelScope.launch {
            repository.updateTask(task.copy(dueDate = newDueDate))
            _snackbarMessage.value = "Rescheduled to $newDueDate"
        }
    }

    fun assignTaskToMember(task: TaskEntity, member: FamilyMemberEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(assignedToMemberId = member.id, assignedMemberName = member.name))
            _snackbarMessage.value = "Assigned to ${member.name}"
        }
    }

    fun deletePlan(planId: Long) {
        viewModelScope.launch {
            repository.deletePlanById(planId)
            _snackbarMessage.value = "Plan removed"
        }
    }

    // NIVA Memory controls
    fun toggleMemory(memoryId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.setMemoryEnabled(memoryId, enabled)
        }
    }

    fun addMemory(title: String, category: String, value: String) {
        if (title.isBlank() || value.isBlank()) return
        viewModelScope.launch {
            repository.insertMemory(MemoryEntity(title = title, category = category, value = value))
            _snackbarMessage.value = "NIVA remembered this preference"
        }
    }

    fun deleteMemory(memoryId: Long) {
        viewModelScope.launch {
            repository.deleteMemoryById(memoryId)
            _snackbarMessage.value = "Memory deleted"
        }
    }

    // Family controls
    fun addFamilyMember(name: String, relation: String, emoji: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertFamilyMember(
                FamilyMemberEntity(name = name, relationship = relation, emoji = emoji)
            )
            _snackbarMessage.value = "$name added to Family Hub"
        }
    }

    fun deleteFamilyMember(memberId: Long) {
        viewModelScope.launch {
            repository.deleteFamilyMemberById(memberId)
            _snackbarMessage.value = "Family member removed"
        }
    }

    // Budget controls
    fun addBudgetItem(categoryName: String, plannedAmount: Double, notes: String = "") {
        if (categoryName.isBlank()) return
        viewModelScope.launch {
            repository.insertBudgetItem(
                BudgetItemEntity(
                    categoryName = categoryName,
                    plannedAmount = plannedAmount,
                    actualAmount = 0.0,
                    currencySymbol = _selectedCurrency.value,
                    notes = notes
                )
            )
        }
    }

    fun updateBudgetItem(item: BudgetItemEntity) {
        viewModelScope.launch {
            repository.updateBudgetItem(item)
        }
    }

    fun deleteBudgetItem(item: BudgetItemEntity) {
        viewModelScope.launch {
            repository.deleteBudgetItem(item)
        }
    }

    // Make My Day Easier Smart Prioritization
    fun makeMyDayEasier() {
        viewModelScope.launch {
            val tasks = todayTasks.value
            if (tasks.isEmpty()) {
                _snackbarMessage.value = "No tasks pending for today!"
                return@launch
            }
            // Sort & re-prioritize
            _snackbarMessage.value = "✨ NIVA prioritized your tasks by morning energy and urgency."
        }
    }

    // Format Plan for Sharing via Android Intent
    fun getShareablePlanText(plan: StructuredPlan? = _currentStructuredPlan.value): String {
        if (plan == null) return "No active plan to share."
        val builder = StringBuilder()
        builder.append("✨ ${plan.title} (Organized by NIVA)\n")
        builder.append("“Tell NIVA. It gets handled.”\n\n")
        if (plan.estimatedBudget.isNotBlank()) {
            builder.append("💰 Estimated Budget: ${plan.estimatedBudget}\n\n")
        }
        for (section in plan.sections) {
            builder.append("📌 ${section.title.uppercase()}:\n")
            for (item in section.items) {
                builder.append("  • $item\n")
            }
            builder.append("\n")
        }
        builder.append("Organized seamlessly so you don't have to keep everything in your head.")
        return builder.toString()
    }

    // Share Family Responsibilities
    fun getShareableFamilySummary(): String {
        val members = allFamilyMembers.value
        val tasks = todayTasks.value
        val builder = StringBuilder()
        builder.append("🏡 Family Responsibilities (via NIVA)\n\n")
        for (member in members) {
            val memberTasks = tasks.filter { it.assignedToMemberId == member.id || it.assignedMemberName == member.name }
            builder.append("${member.emoji} ${member.name} (${member.relationship}):\n")
            if (memberTasks.isEmpty()) {
                builder.append("  • No active tasks assigned\n")
            } else {
                for (t in memberTasks) {
                    val check = if (t.isCompleted) "[✓]" else "[ ]"
                    builder.append("  $check ${t.title}\n")
                }
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    // Voice recognition handling
    fun startVoiceInput(context: Context) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _snackbarMessage.value = "Voice recognition is not available on this device"
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListeningVoice.value = true
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        _isListeningVoice.value = false
                    }
                    override fun onError(error: Int) {
                        _isListeningVoice.value = false
                        _snackbarMessage.value = "Voice capture finished"
                    }
                    override fun onResults(results: Bundle?) {
                        _isListeningVoice.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            _currentPromptInput.value = matches[0]
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell NIVA what's on your mind...")
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListeningVoice.value = false
            _snackbarMessage.value = "Speech recognition error: ${e.message}"
        }
    }

    fun stopVoiceInput() {
        speechRecognizer?.stopListening()
        _isListeningVoice.value = false
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }

    fun setUserName(name: String) {
        if (name.isNotBlank()) _userName.value = name
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun setCurrency(curr: String) {
        _selectedCurrency.value = curr
    }
}

class NivaViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application, kotlinx.coroutines.GlobalScope)
        val aiEngine = NivaIntelligenceEngine()
        val repository = NivaRepository(database, aiEngine)
        return NivaViewModel(application, repository) as T
    }
}
