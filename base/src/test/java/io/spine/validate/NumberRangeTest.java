/*
 * Copyright 2019, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.validate;

import com.google.protobuf.DoubleValue;
import com.google.protobuf.Message;
import io.spine.test.validate.MaxExclusiveNumberFieldValue;
import io.spine.test.validate.MaxInclusiveNumberFieldValue;
import io.spine.test.validate.MaxNumberFieldValue;
import io.spine.test.validate.MinExclusiveNumberFieldValue;
import io.spine.test.validate.MinInclusiveNumberFieldValue;
import io.spine.test.validate.MinNumberFieldValue;
import io.spine.validate.given.MessageValidatorTestEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.validate.MessageValidatorTest.MESSAGE_VALIDATOR_SHOULD;
import static io.spine.validate.given.MessageValidatorTestEnv.EQUAL_MAX;
import static io.spine.validate.given.MessageValidatorTestEnv.EQUAL_MIN;
import static io.spine.validate.given.MessageValidatorTestEnv.GREATER_MAX_MSG;
import static io.spine.validate.given.MessageValidatorTestEnv.GREATER_THAN_MAX;
import static io.spine.validate.given.MessageValidatorTestEnv.GREATER_THAN_MIN;
import static io.spine.validate.given.MessageValidatorTestEnv.LESS_THAN_MIN;
import static io.spine.validate.given.MessageValidatorTestEnv.LESS_THAN_MIN_MSG;
import static io.spine.validate.given.MessageValidatorTestEnv.VALUE;

@DisplayName(MESSAGE_VALIDATOR_SHOULD + "analyze (min) and (max) options and")
class NumberRangeTest extends MessageValidatorTest {

    @Test
    @DisplayName("Consider number field is valid if no number options set")
    void considerNumberFieldIsValidIfNoNumberOptionsSet() {
        Message nonZeroValue = DoubleValue.newBuilder()
                                          .setValue(5)
                                          .build();
        assertValid(nonZeroValue);
    }

    @Test
    @DisplayName("find out that number is greater than decimal min inclusive")
    void findOutThatNumberIsGreaterThanDecimalMinInclusive() {
        minNumberTest(GREATER_THAN_MIN, true, true);
    }

    @Test
    @DisplayName("find out that number is equal to decimal min inclusive")
    void findOutThatNumberIsEqualToDecimalMinInclusive() {
        minNumberTest(EQUAL_MIN, true, true);
    }

    @Test
    @DisplayName("find out that number is less than decimal min inclusive")
    void findOutThatNumberIsLessThanDecimalMinInclusive() {
        minNumberTest(LESS_THAN_MIN, true, false);
    }

    @Test
    @DisplayName("find out that number is grated than decimal min NOT inclusive")
    void findOutThatNumberIsGreaterThanDecimalMinNotInclusive() {
        minNumberTest(GREATER_THAN_MIN, false, true);
    }

    @Test
    @DisplayName("find out that number is equal to decimal min NOT inclusive")
    void findOutThatNumberIsEqualToDecimalMinNotInclusive() {
        minNumberTest(EQUAL_MIN, false, false);
    }

    @Test
    @DisplayName("find out that number is less than decimal min NOT inclusive")
    void findOutThatNumberIsLessThanDecimalMinNotInclusive() {
        minNumberTest(LESS_THAN_MIN, false, false);
    }

    @Test
    @DisplayName("provide one valid violation if number is less than decimal min")
    void provideOneValidViolationIfNumberIsLessThanDecimalMin() {
        minNumberTest(LESS_THAN_MIN, true, false);
        assertSingleViolation(LESS_THAN_MIN_MSG, VALUE);
    }

    @Test
    @DisplayName("find out that number is greater than decimal max inclusive")
    void findOutThatNumberIsGreaterThanDecimalMaxInclusive() {
        maxNumberTest(GREATER_THAN_MAX, true, false);
    }

    @Test
    @DisplayName("find out that number is equal to decimal max inclusive")
    void findOutThatNumberIsEqualToDecimalMaxInclusive() {
        maxNumberTest(EQUAL_MAX, true, true);
    }

    @Test
    @DisplayName("find out that number is less than decimal max inclusive")
    void findOutThatNumberIsLessThanDecimalMaxInclusive() {
        maxNumberTest(MessageValidatorTestEnv.LESS_THAN_MAX, true, true);
    }

    @Test
    @DisplayName("find out that number is greated than decimal max NOT inclusive")
    void findOutThatNumberIsGreaterThanDecimalMaxNotInclusive() {
        maxNumberTest(GREATER_THAN_MAX, false, false);
    }

    @Test
    @DisplayName("find out that number is equal to decimal max NOT inclusive")
    void findOutThatNumberIsEqualToDecimalMaxNotInclusive() {
        maxNumberTest(EQUAL_MAX, false, false);
    }

    @Test
    @DisplayName("find out that number is less than decimal max NOT inclusive")
    void findOutThatNumberIsLessThanDecimalMaxNotInclusive() {
        maxNumberTest(MessageValidatorTestEnv.LESS_THAN_MAX, false, true);
    }

    @Test
    @DisplayName("provide one valid violation if number is greater than decimal max")
    void provideOneValidViolationIfNumberIsGreaterThanDecimalMax() {
        maxNumberTest(GREATER_THAN_MAX, true, false);
        assertSingleViolation(GREATER_MAX_MSG, VALUE);
    }

    @Test
    @DisplayName("find out that number is greater than min")
    void findOutThatNumberIsGreaterThanMin() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(GREATER_THAN_MIN)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that number is equal to min")
    void findOutThatNumberIsEqualToMin() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(EQUAL_MIN)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that number is less than min")
    void findOutThatNumberIsLessThanMin() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(LESS_THAN_MIN)
                                                     .build();
        assertNotValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if number is less than min")
    void provideOneValidViolationIfNumberIsLessThanMin() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(LESS_THAN_MIN)
                                                     .build();
        assertSingleViolation(msg, LESS_THAN_MIN_MSG, VALUE);
    }

    @Test
    @DisplayName("find out that number is greater than max inclusive")
    void findOutThatNumberIsGreaterThanMaxInclusive() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(GREATER_THAN_MAX)
                                                     .build();
        assertNotValid(msg);
    }

    @Test
    @DisplayName("find out that number is equal to max inclusive")
    void findOutThatNumberIsEqualToMaxInclusive() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(EQUAL_MAX)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that number is less than max inclusive")
    void findOutThatNumberIsLessThanMaxInclusive() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(MessageValidatorTestEnv.LESS_THAN_MAX)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if number is greater than max")
    void provideOneValidViolationIfNumberIsGreaterThanMax() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(GREATER_THAN_MAX)
                                                     .build();
        assertSingleViolation(msg, GREATER_MAX_MSG, VALUE);
    }

    private void minNumberTest(double value, boolean inclusive, boolean valid) {
        Message msg = inclusive
                      ? MinInclusiveNumberFieldValue
                              .newBuilder()
                              .setValue(value)
                              .build()
                      : MinExclusiveNumberFieldValue
                              .newBuilder()
                              .setValue(value)
                              .build();
        validate(msg);
        assertIsValid(valid);
    }

    private void maxNumberTest(double value, boolean inclusive, boolean valid) {
        Message msg = inclusive
                      ? MaxInclusiveNumberFieldValue
                              .newBuilder()
                              .setValue(value)
                              .build()
                      : MaxExclusiveNumberFieldValue
                              .newBuilder()
                              .setValue(value)
                              .build();
        validate(msg);
        assertIsValid(valid);
    }
}
