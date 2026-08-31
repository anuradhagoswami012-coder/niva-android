package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class NivaIntelligenceEngine {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeAndCreatePlan(
        userInput: String,
        categoryHint: PlanCategory? = null,
        userPreferences: Map<String, String> = emptyMap()
    ): StructuredPlan = withContext(Dispatchers.IO) {
        val trimmed = userInput.trim()

        // 1. Check if Gemini API is available and usable
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "null") {
            try {
                val geminiPlan = callGeminiForStructuredPlan(trimmed, categoryHint, userPreferences, apiKey)
                if (geminiPlan != null) {
                    return@withContext geminiPlan
                }
            } catch (e: Exception) {
                // Fall back to built-in intelligence engine
            }
        }

        // 2. Built-in Local AI Engine (High accuracy for Indian household, guests, cooking, and brain-dump domains)
        return@withContext generateLocalStructuredPlan(trimmed, categoryHint, userPreferences)
    }

    private fun callGeminiForStructuredPlan(
        userInput: String,
        categoryHint: PlanCategory?,
        userPreferences: Map<String, String>,
        apiKey: String
    ): StructuredPlan? {
        val prompt = """
            You are NIVA, an intelligent, warm, non-judgmental, calming life and household assistant designed around mental load reduction.
            Tagline: "Tell NIVA. It gets handled."
            Emotional promise: "You don't have to keep everything in your head."

            User input (in English, Hindi, or Hinglish):
            "$userInput"

            Category Hint: ${categoryHint?.name ?: "AUTO_DETECT"}
            Known User Preferences: $userPreferences

            Analyze the user's input. If vital information is missing for a complex guest or party request, note questions.
            Return a pure JSON response in this exact format (no markdown fences, just JSON):
            {
              "title": "Short clear title",
              "category": "GUESTS" or "COOKING" or "EMPTY_HEAD" or "MONEY" or "OCCASION" or "HELP_NOW" or "GENERAL",
              "empatheticIntro": "1-2 warm sentences reassuring the user that NIVA has organized it.",
              "estimatedBudget": "₹1,500" (optional or estimated),
              "missingQuestions": ["Question 1", "Question 2"] (empty if sufficient information exists),
              "sections": [
                {
                  "section": "TODAY" or "MENU" or "QUANTITIES" or "PREPARATION" or "COOKING_ORDER" or "SERVING" or "LAST_MINUTE" or "SHOPPING" or "HOME" or "FAMILY" or "MONEY" or "LATER",
                  "title": "Section Title",
                  "items": ["Specific actionable task 1", "Specific actionable task 2"]
                }
              ]
            }
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
                put("responseMimeType", "application/json")
            })
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseText = response.body?.string() ?: return null
        val responseObj = JSONObject(responseText)
        val candidates = responseObj.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null

        var textContent = parts.getJSONObject(0).optString("text", "")
        if (textContent.startsWith("```json")) {
            textContent = textContent.removePrefix("```json").removeSuffix("```").trim()
        } else if (textContent.startsWith("```")) {
            textContent = textContent.removePrefix("```").removeSuffix("```").trim()
        }

        val planJson = JSONObject(textContent)
        val title = planJson.optString("title", "Organized Plan")
        val categoryStr = planJson.optString("category", "GENERAL")
        val category = try { PlanCategory.valueOf(categoryStr) } catch (e: Exception) { PlanCategory.GENERAL }
        val empatheticIntro = planJson.optString("empatheticIntro", "I've organized this for you. Take a breath.")
        val estimatedBudget = planJson.optString("estimatedBudget", "")

        val missingQList = mutableListOf<String>()
        val missingArray = planJson.optJSONArray("missingQuestions")
        if (missingArray != null) {
            for (i in 0 until missingArray.length()) {
                missingQList.add(missingArray.getString(i))
            }
        }

        val sectionsList = mutableListOf<PlanSectionData>()
        val sectionsArray = planJson.optJSONArray("sections")
        if (sectionsArray != null) {
            for (i in 0 until sectionsArray.length()) {
                val secObj = sectionsArray.getJSONObject(i)
                val secTypeStr = secObj.optString("section", "TODAY")
                val secType = try { TaskSection.valueOf(secTypeStr) } catch (e: Exception) { TaskSection.TODAY }
                val secTitle = secObj.optString("title", secType.displayName)
                val itemsList = mutableListOf<String>()
                val itemsArray = secObj.optJSONArray("items")
                if (itemsArray != null) {
                    for (j in 0 until itemsArray.length()) {
                        itemsList.add(itemsArray.getString(j))
                    }
                }
                if (itemsList.isNotEmpty()) {
                    sectionsList.add(PlanSectionData(secType, secTitle, itemsList))
                }
            }
        }

        return StructuredPlan(
            title = title,
            category = category,
            empatheticIntro = empatheticIntro,
            sections = sectionsList,
            estimatedBudget = estimatedBudget,
            missingQuestions = missingQList
        )
    }

    /**
     * Highly tuned, culturally authentic offline intelligence engine.
     * Understands English, Hindi, and Hinglish across guests, recipes, brain dumps, money, and emergencies.
     */
    fun generateLocalStructuredPlan(
        userInput: String,
        categoryHint: PlanCategory?,
        preferences: Map<String, String>
    ): StructuredPlan {
        val lower = userInput.lowercase()

        // 1. Guests coming / Gathering detection
        val isGuestRequest = categoryHint == PlanCategory.GUESTS ||
                lower.contains("guest") || lower.contains("mehmaan") || lower.contains("log aa rahe") ||
                lower.contains("people are coming") || lower.contains("dinner party") || lower.contains("lunch party")

        // 2. Cooking / Recipe detection
        val isCookingRequest = categoryHint == PlanCategory.COOKING ||
                lower.contains("cook") || lower.contains("recipe") || lower.contains("kya banau") ||
                lower.contains("paneer") || lower.contains("sabzi") || lower.contains("ingredients") ||
                lower.contains("dinner tonight") || lower.contains("lunch")

        // 3. Brain dump / Empty my head detection
        val isBrainDump = categoryHint == PlanCategory.EMPTY_HEAD ||
                lower.contains("empty") || lower.contains("too much to do") || lower.contains("organize everything") ||
                lower.contains("dimaag") || lower.contains("head is full") || userInput.split(",", "\n", " and ", " aur ").size >= 4

        // 4. Money / Budget detection
        val isMoneyRequest = categoryHint == PlanCategory.MONEY ||
                lower.contains("budget") || lower.contains("money") || lower.contains("kharcha") ||
                lower.contains("salary") || lower.contains("payday") || lower.contains("expense")

        // 5. Help Me Now / Quick action detection
        val isHelpNow = categoryHint == PlanCategory.HELP_NOW ||
                lower.contains("emergency") || lower.contains("urgent") || lower.contains("in 30 min") ||
                lower.contains("fast") || lower.contains("jaldi") || lower.contains("cleanup")

        return when {
            isGuestRequest -> generateGuestPlan(userInput, lower)
            isCookingRequest -> generateCookingPlan(userInput, lower)
            isBrainDump -> generateBrainDumpPlan(userInput, lower)
            isMoneyRequest -> generateMoneyPlan(userInput, lower)
            isHelpNow -> generateHelpNowPlan(userInput, lower)
            else -> generateGeneralPlan(userInput, lower)
        }
    }

    private fun generateGuestPlan(original: String, lower: String): StructuredPlan {
        // Extract guest count
        val numberRegex = Regex("(\\d+)\\s*(guests|people|log|persons)?")
        val match = numberRegex.find(lower)
        val guestCount = match?.groupValues?.get(1)?.toIntOrNull() ?: 8

        val isJain = lower.contains("jain") || lower.contains("no onion") || lower.contains("bina lehsun")
        val isNonVeg = lower.contains("chicken") || lower.contains("mutton") || lower.contains("non veg") || lower.contains("fish")
        val isLunch = lower.contains("lunch") || lower.contains("dopahar")
        val mealType = if (isLunch) "Lunch" else "Dinner"

        // Check if missing crucial info (e.g. if input was just "10 guests are coming tomorrow")
        val missingQuestions = mutableListOf<String>()
        if (!lower.contains("lunch") && !lower.contains("dinner") && !lower.contains("snack") && !lower.contains("shaam")) {
            missingQuestions.add("Lunch or dinner?")
        }
        if (!lower.contains("veg") && !lower.contains("jain") && !lower.contains("non-veg") && !lower.contains("chicken")) {
            missingQuestions.add("Vegetarian, non-vegetarian, or Jain?")
        }
        if (!lower.contains("₹") && !lower.contains("rs") && !lower.contains("budget") && !lower.contains("rupees")) {
            missingQuestions.add("Approximate budget?")
        }

        val estimatedBudget = "₹${guestCount * 280}"

        val menuItems = if (isJain) {
            listOf(
                "Shahi Paneer (Satvik Jain Gravy with Cashew-Curd Base)",
                "Jeera Aloo (Cumin infused boiled potatoes with fresh coriander)",
                "Yellow Dal Tadka (Hing-Jeera tempered)",
                "Steamed Basmati Rice & Soft Butter Phulkas",
                "Boondi & Pomegranate Raita",
                "Gulab Jamun or Mango Kheer"
            )
        } else if (isNonVeg) {
            listOf(
                "Dhaba Style Butter Chicken / Chicken Curry",
                "Kadai Paneer (for veg option)",
                "Dal Makhani slow simmered",
                "Jeera Rice & Tawa Laccha Parathas",
                "Mixed Veg Mint Raita & Masala Onions",
                "Kesar Phirni"
            )
        } else {
            listOf(
                "Paneer Butter Masala / Matar Paneer",
                "Dum Aloo or Mix Vegetable Sabzi",
                "Dal Makhani or Yellow Tadka Dal",
                "Jeera Rice & Fresh Hot Phulkas / Naan",
                "Cucumber Mint Raita & Papad",
                "Warm Gulab Jamun with vanilla ice cream"
            )
        }

        val quantities = listOf(
            "Paneer: ${(guestCount * 0.12).coerceAtLeast(0.5)} kg",
            "Atta (Wheat Flour): ${(guestCount * 0.1).coerceAtLeast(0.5)} kg (approx ${guestCount * 3} rotis)",
            "Basmati Rice: ${(guestCount * 0.08).coerceAtLeast(0.4)} kg",
            "Curd/Dahi: ${(guestCount * 0.1).coerceAtLeast(0.5)} kg for raita & gravy",
            "Fresh Coriander & Green Chillies: 1 bunch",
            "Cold drinks / Welcome drink (Nimbu Pani / Chaas): ${(guestCount * 0.25).coerceAtLeast(1.5)} Litres"
        )

        val prepTimeline = listOf(
            "T-4 Hours: Soak dals, prepare ginger-chilli paste, peel and chop vegetables.",
            "T-3 Hours: Knead soft dough (atta) with a spoon of milk, cover with damp cloth.",
            "T-2 Hours: Cook gravies and dal. Keep in covered serveware.",
            "T-1 Hour: Set dining table, clean glasses, fill water carafes, chill welcome drinks.",
            "T-15 Mins: Fry papad, garnish dishes with coriander and fresh cream, start hot rotis."
        )

        val lastMinuteChecklist = listOf(
            "Check guest bathroom: fresh hand towel, liquid soap, extra tissue roll.",
            "Keep welcome drink tray and coasters ready near entrance.",
            "Turn on pleasant ambient background music at low volume.",
            "Light a mild fragrance candle or agarbatti in the living room."
        )

        return StructuredPlan(
            title = "$guestCount Guests $mealType Hosting Plan",
            category = PlanCategory.GUESTS,
            empatheticIntro = "Everything for your $guestCount guests is organized step-by-step. You don't have to keep all the timings in your head.",
            estimatedBudget = estimatedBudget,
            missingQuestions = missingQuestions,
            sections = listOf(
                PlanSectionData(TaskSection.MENU, "Curated Menu ($mealType)", menuItems),
                PlanSectionData(TaskSection.QUANTITIES, "Estimated Quantities & Groceries", quantities),
                PlanSectionData(TaskSection.PREPARATION, "Preparation Timeline", prepTimeline),
                PlanSectionData(TaskSection.LAST_MINUTE, "Serving & Last-Minute Checklist", lastMinuteChecklist)
            )
        )
    }

    private fun generateCookingPlan(original: String, lower: String): StructuredPlan {
        // Detect mentioned ingredients
        val hasPaneer = lower.contains("paneer")
        val hasCapsicum = lower.contains("capsicum") || lower.contains("shimla")
        val hasPotato = lower.contains("potato") || lower.contains("aloo")
        val hasCurd = lower.contains("curd") || lower.contains("dahi")
        val hasAtta = lower.contains("atta") || lower.contains("roti") || lower.contains("wheat")
        val hasPoha = lower.contains("poha")
        val hasUpma = lower.contains("sooji") || lower.contains("suji") || lower.contains("upma")

        val menuItems = mutableListOf<String>()
        val missingItems = mutableListOf<String>()

        if (hasPaneer && hasCapsicum) {
            menuItems.add("Kadai Paneer Shimla Mirch (Wok-tossed in whole crushed spices)")
        } else if (hasPaneer) {
            menuItems.add("Paneer Bhurji / Matar Paneer")
        }

        if (hasPotato) {
            menuItems.add("Crispy Jeera Aloo with roasted cumin & amchur")
        }

        if (hasCurd) {
            menuItems.add("Whisked Tadka Raita or Boondi Raita")
        }

        if (hasAtta || menuItems.isNotEmpty()) {
            menuItems.add("Soft Ghee Phulkas (Rotis)")
        }

        if (menuItems.isEmpty()) {
            if (hasPoha) {
                menuItems.add("Kanda Batata Poha with roasted peanuts and lemon")
            } else if (hasUpma) {
                menuItems.add("Vegetable Rava Upma with mustard and curry leaf tempering")
            } else {
                menuItems.add("Comforting Dal Khichdi with Ghee & Roasted Papad")
                menuItems.add("Spiced Aloo Methi / Seasonal Sabzi")
                menuItems.add("Fresh Kachumber Salad")
            }
        }

        missingItems.add("Fresh green coriander (Dhaniya) for final garnish")
        missingItems.add("1 Lemon for fresh zest")
        missingItems.add("Ginger-garlic paste (or ginger only if Jain)")

        val steps = listOf(
            "1. Chop vegetables uniformly (cubes for paneer/capsicum).",
            "2. Make dry spice mix (coriander seeds, dry red chilli, cumin).",
            "3. Sauté spices and toss the main sabzi in 15 minutes on medium flame.",
            "4. Whisk cold curd with roasted cumin powder and rock salt for raita.",
            "5. Make hot phulkas right before serving."
        )

        return StructuredPlan(
            title = "Tonight’s Crafted Menu",
            category = PlanCategory.COOKING,
            empatheticIntro = "Based on your kitchen ingredients, here is a delicious, balanced meal plan with zero guesswork.",
            estimatedBudget = "₹0 (Using pantry ingredients)",
            missingQuestions = emptyList(),
            sections = listOf(
                PlanSectionData(TaskSection.MENU, "Recommended Menu", menuItems),
                PlanSectionData(TaskSection.SHOPPING, "Missing Essentials (Optional)", missingItems),
                PlanSectionData(TaskSection.COOKING_ORDER, "Quick Cooking Sequence (25 Mins)", steps)
            )
        )
    }

    private fun generateBrainDumpPlan(original: String, lower: String): StructuredPlan {
        // Split text by commas, newlines, "and", "aur", "then"
        val rawTokens = original.split(Regex("[,;\\n]+|\\band\\b|\\baur\\b|\\bthen\\b|\\bphir\\b", RegexOption.IGNORE_CASE))
            .map { it.trim() }
            .filter { it.length > 2 }

        val todayTasks = mutableListOf<String>()
        val tomorrowTasks = mutableListOf<String>()
        val shoppingTasks = mutableListOf<String>()
        val homeTasks = mutableListOf<String>()
        val familyTasks = mutableListOf<String>()
        val moneyTasks = mutableListOf<String>()
        val laterTasks = mutableListOf<String>()

        for (token in rawTokens) {
            val tLower = token.lowercase()
            when {
                tLower.contains("buy") || tLower.contains("order") || tLower.contains("vegetables") ||
                        tLower.contains("grocer") || tLower.contains("sabzi") || tLower.contains("milk") ||
                        tLower.contains("paneer") || tLower.contains("shop") -> {
                    shoppingTasks.add(token.replaceFirstChar { it.uppercase() })
                }
                tLower.contains("pay") || tLower.contains("bill") || tLower.contains("bank") ||
                        tLower.contains("fee") || tLower.contains("transfer") || tLower.contains("money") -> {
                    moneyTasks.add(token.replaceFirstChar { it.uppercase() })
                }
                tLower.contains("mummy") || tLower.contains("papa") || tLower.contains("call") ||
                        tLower.contains("doctor") || tLower.contains("medicine") || tLower.contains("child") ||
                        tLower.contains("school") || tLower.contains("husband") || tLower.contains("wife") -> {
                    familyTasks.add(token.replaceFirstChar { it.uppercase() })
                }
                tLower.contains("clean") || tLower.contains("iron") || tLower.contains("maid") ||
                        tLower.contains("cook") || tLower.contains("room") || tLower.contains("laundry") ||
                        tLower.contains("clothes") || tLower.contains("house") -> {
                    homeTasks.add(token.replaceFirstChar { it.uppercase() })
                }
                tLower.contains("tomorrow") || tLower.contains("kal") -> {
                    tomorrowTasks.add(token.replaceFirstChar { it.uppercase() })
                }
                tLower.contains("sunday") || tLower.contains("next week") || tLower.contains("later") -> {
                    laterTasks.add(token.replaceFirstChar { it.uppercase() })
                }
                else -> {
                    todayTasks.add(token.replaceFirstChar { it.uppercase() })
                }
            }
        }

        // Ensure defaults if empty
        if (todayTasks.isEmpty() && shoppingTasks.isEmpty() && homeTasks.isEmpty()) {
            todayTasks.add("Review and prioritize organized items")
        }

        val sections = mutableListOf<PlanSectionData>()
        if (todayTasks.isNotEmpty()) sections.add(PlanSectionData(TaskSection.TODAY, "Today’s Priorities", todayTasks))
        if (tomorrowTasks.isNotEmpty()) sections.add(PlanSectionData(TaskSection.TOMORROW, "Tomorrow", tomorrowTasks))
        if (shoppingTasks.isNotEmpty()) sections.add(PlanSectionData(TaskSection.SHOPPING, "Shopping & Errands", shoppingTasks))
        if (homeTasks.isNotEmpty()) sections.add(PlanSectionData(TaskSection.HOME, "Home & Chores", homeTasks))
        if (familyTasks.isNotEmpty()) sections.add(PlanSectionData(TaskSection.FAMILY, "Family & Health", familyTasks))
        if (moneyTasks.isNotEmpty()) sections.add(PlanSectionData(TaskSection.MONEY, "Money & Bills", moneyTasks))
        if (laterTasks.isNotEmpty()) sections.add(PlanSectionData(TaskSection.LATER, "This Weekend / Later", laterTasks))

        return StructuredPlan(
            title = "Uncluttered Brain Dump Plan",
            category = PlanCategory.EMPTY_HEAD,
            empatheticIntro = "I’ve organized everything into clean, actionable buckets. You don’t have to hold all of it in your head anymore.",
            estimatedBudget = "",
            missingQuestions = emptyList(),
            sections = sections
        )
    }

    private fun generateMoneyPlan(original: String, lower: String): StructuredPlan {
        return StructuredPlan(
            title = "Household Money Allocation",
            category = PlanCategory.MONEY,
            empatheticIntro = "Here is a balanced household allocation to keep expenses peaceful without stress before month-end.",
            estimatedBudget = "₹45,000 Total Monthly Budget",
            missingQuestions = emptyList(),
            sections = listOf(
                PlanSectionData(TaskSection.MONEY, "Essential Fixed Expenses (50%)", listOf(
                    "Domestic Help & Cook salary: ₹8,500",
                    "Electricity, Gas & Water bills: ₹4,500",
                    "Child schooling / tuition / classes: ₹7,000",
                    "Society Maintenance / Wifi: ₹3,500"
                )),
                PlanSectionData(TaskSection.SHOPPING, "Variable Living Expenses (30%)", listOf(
                    "Weekly Vegetables & Groceries allocation: ₹14,000",
                    "Emergency Medicines & Medical buffer: ₹3,500",
                    "Household supplies & miscellaneous: ₹3,000"
                )),
                PlanSectionData(TaskSection.TODAY, "Peace of Mind Savings (20%)", listOf(
                    "Auto-transfer to Emergency Fund / SIP: ₹10,000 on 1st of month"
                ))
            )
        )
    }

    private fun generateHelpNowPlan(original: String, lower: String): StructuredPlan {
        val isDinner30 = lower.contains("dinner") || lower.contains("30 min") || lower.contains("30 minute")
        val isClean = lower.contains("clean") || lower.contains("guests arriving") || lower.contains("urgent")

        val title = if (isDinner30) "⚡ 30-Minute Dinner Rapid Plan" else "🚨 Quick 20-Minute House Reset"
        val intro = "Take one deep breath. Let’s tackle this step-by-step in quick, easy wins."

        val steps = if (isDinner30) {
            listOf(
                "Minute 0-5: Put pressure cooker on for Jeera Rice / Khichdi or boil water for pasta.",
                "Minute 5-15: Sauté quick Paneer Bhurji / Egg Bhurji or spiced potatoes in one pan.",
                "Minute 15-25: Toast bread or warm ready rotis with ghee.",
                "Minute 25-30: Slice cucumber & tomatoes, serve hot with pickle and curd."
            )
        } else {
            listOf(
                "Minute 0-5: Grab a laundry basket and dump all stray clutter from the living room couch and tables.",
                "Minute 5-10: Plump sofa cushions and straighten the center table runner.",
                "Minute 10-15: Wipe down guest bathroom counter, put out fresh dry towel.",
                "Minute 15-20: Spray room freshener and dim harsh overhead lights to soft warm lighting."
            )
        }

        return StructuredPlan(
            title = title,
            category = PlanCategory.HELP_NOW,
            empatheticIntro = intro,
            estimatedBudget = "",
            missingQuestions = emptyList(),
            sections = listOf(
                PlanSectionData(TaskSection.TODAY, "Step-by-Step Rapid Action", steps)
            )
        )
    }

    private fun generateGeneralPlan(original: String, lower: String): StructuredPlan {
        return StructuredPlan(
            title = "Actionable Life Plan",
            category = PlanCategory.GENERAL,
            empatheticIntro = "I’ve turned what’s on your mind into organized, bite-sized tasks.",
            estimatedBudget = "",
            missingQuestions = emptyList(),
            sections = listOf(
                PlanSectionData(TaskSection.TODAY, "Immediate Next Steps", listOf(
                    "Step 1: Focus on the single highest priority task first.",
                    "Step 2: Clear quick 2-minute errands off the list.",
                    "Step 3: Delegate secondary items to family members."
                )),
                PlanSectionData(TaskSection.THIS_WEEK, "Follow-Up Actions", listOf(
                    "Schedule reminders for upcoming deadlines.",
                    "Check off completed tasks in NIVA to keep your mind clear."
                ))
            )
        )
    }
}
