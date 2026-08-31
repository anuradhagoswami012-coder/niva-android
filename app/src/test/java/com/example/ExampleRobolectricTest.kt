package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.NivaIntelligenceEngine
import com.example.data.model.PlanCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NIVA", appName)
  }

  @Test
  fun `niva local intelligence engine generates guest plan`() {
    val engine = NivaIntelligenceEngine()
    val plan = engine.generateLocalStructuredPlan(
      userInput = "10 guests are coming tomorrow for dinner",
      categoryHint = PlanCategory.GUESTS,
      preferences = emptyMap()
    )
    assertNotNull(plan)
    assertEquals(PlanCategory.GUESTS, plan.category)
    assertTrue(plan.title.contains("10 Guests"))
    assertTrue(plan.sections.isNotEmpty())
  }

  @Test
  fun `niva local intelligence engine parses brain dump`() {
    val engine = NivaIntelligenceEngine()
    val plan = engine.generateLocalStructuredPlan(
      userInput = "Tomorrow I need vegetables, call mummy, pay electricity bill, get clothes ironed",
      categoryHint = PlanCategory.EMPTY_HEAD,
      preferences = emptyMap()
    )
    assertNotNull(plan)
    assertEquals(PlanCategory.EMPTY_HEAD, plan.category)
    assertTrue(plan.sections.isNotEmpty())
  }
}

