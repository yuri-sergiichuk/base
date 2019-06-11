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

package io.spine.validate.option;

import io.spine.test.validate.NumRanges;
import io.spine.test.validate.RangesHolder;
import io.spine.validate.MessageValidatorTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static io.spine.validate.MessageValidatorTest.MESSAGE_VALIDATOR_SHOULD;

@DisplayName(MESSAGE_VALIDATOR_SHOULD + "analyze (range) option and find out that")
final class RangeTest extends MessageValidatorTest {

    @Nested
    @DisplayName("integers")
    final class Integers {

        @DisplayName("fit into the defined range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#validHours")
        void fitIntoRange(int hour) {
            NumRanges msg = hourRange(hour);
            assertValid(msg);
        }

        @DisplayName("fit into the defined external constraint range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#validHalfDayHours")
        void fitIntoExternalConstraintRange(int hour) {
            NumRanges msg = hourRange(hour);
            assertValid(holderOf(msg));
        }

        @DisplayName("do not fit into the defined range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#invalidHours")
        void doNotFitIntoRange(int hour) {
            NumRanges msg = hourRange(hour);
            assertNotValid(msg);
        }

        @DisplayName("do not fit into the defined external constraint range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#invalidHalfDayHours")
        void doNotFitIntoExternalConstraintRange(int hour) {
            NumRanges msg = hourRange(hour);
            assertNotValid(holderOf(msg));
        }

        private NumRanges hourRange(int hour) {
            return validRange().setHour(hour)
                               .build();
        }
    }

    @Nested
    @DisplayName("longs")
    final class Longs {

        @DisplayName("fit into the defined range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#validMinutes")
        void fitIntoRange(long minute) {
            NumRanges msg = minuteRange(minute);
            assertValid(msg);
        }

        @DisplayName("fit into the defined external constraint range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#validHalfHourMinutes")
        void fitIntoExternalConstraintRange(long minute) {
            NumRanges msg = minuteRange(minute);
            assertValid(holderOf(msg));
        }

        @DisplayName("do not fit into the defined range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#invalidMinutes")
        void doNotFitIntoRange(long minute) {
            NumRanges msg = minuteRange(minute);
            assertNotValid(msg);
        }

        @DisplayName("do not fit into the defined external constraint range")
        @ParameterizedTest
        @MethodSource("io.spine.validate.option.RangeTest#invalidHalfHourMinutes")
        void doNotFitIntoExternalConstraintRange(long minute) {
            NumRanges msg = minuteRange(minute);
            assertNotValid(holderOf(msg));
        }

        private NumRanges minuteRange(long minute) {
            return validRange().setMinute(minute)
                               .build();
        }
    }

    private static RangesHolder holderOf(NumRanges ranges) {
        return RangesHolder
                .newBuilder()
                .setRanges(ranges)
                .build();
    }

    private static NumRanges.Builder validRange() {
        return NumRanges
                .newBuilder()
                .setHour(1)
                .setAngle(1)
                .setDegree(1)
                .setMinute(1);
    }

    private static Stream<Integer> invalidHalfDayHours() {
        return IntStream.of(-1, 13, 23, 24, Integer.MAX_VALUE, Integer.MIN_VALUE)
                        .boxed();
    }

    private static Stream<Integer> invalidHours() {
        return IntStream.of(-1, 24, Integer.MAX_VALUE, Integer.MIN_VALUE)
                        .boxed();
    }

    private static Stream<Integer> validHours() {
        return IntStream.range(0, 23)
                        .boxed();
    }

    private static Stream<Integer> validHalfDayHours() {
        return IntStream.range(0, 12)
                        .boxed();
    }

    private static Stream<Long> invalidHalfHourMinutes() {
        return LongStream.of(-1, 31, 59, 60, Long.MAX_VALUE, Long.MIN_VALUE)
                         .boxed();
    }

    private static Stream<Long> invalidMinutes() {
        return LongStream.of(-1, 60, 61, Long.MAX_VALUE, Long.MIN_VALUE)
                         .boxed();
    }

    private static Stream<Long> validMinutes() {
        return LongStream.range(0, 59)
                         .boxed();
    }

    private static Stream<Long> validHalfHourMinutes() {
        return LongStream.range(0, 29)
                         .boxed();
    }
}