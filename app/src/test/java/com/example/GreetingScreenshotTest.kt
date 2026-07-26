package com.example

import com.example.util.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressionEvaluatorTest {

  @Test
  fun testBasicArithmetic() {
    val result = ExpressionEvaluator.evaluate("(12.5 + 4.25) * 2")
    assertEquals(33.5, result, 0.001)
  }

  @Test
  fun testFeetInchesFormatting() {
    val ftIn = ExpressionEvaluator.formatFeetInches(67.375)
    assertEquals("5' 7 3/8\"", ftIn)
  }
}
