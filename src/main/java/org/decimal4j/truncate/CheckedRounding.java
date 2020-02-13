/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.truncate;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Provides rounding constants implementing {@link TruncationPolicy} for
 * {@link OverflowMode#CHECKED}. The constants are equivalent to the constants
 * defined by {@link RoundingMode}; the policy's {@link #getOverflowMode()}
 * method always returns {@link OverflowMode#CHECKED CHECKED} overflow mode.
 */
public enum CheckedRounding implements TruncationPolicy {
	/**
	 * Checked truncation policy with rounding mode to round away from zero.
	 * Always increments the digit prior to a non-zero discarded fraction. Note
	 * that this rounding mode never decreases the magnitude of the calculated
	 * value.
	 * 
	 * @see RoundingMode#UP
	 */
	UP {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.UP;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.UP;
		}
	},

	/**
	 * Checked truncation policy with rounding mode to round towards zero. Never
	 * increments the digit prior to a discarded fraction (i.e., truncates).
	 * Note that this rounding mode never increases the magnitude of the
	 * calculated value.
	 * 
	 * @see RoundingMode#DOWN
	 */
	DOWN {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.DOWN;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.DOWN;
		}
	},

	/**
	 * Checked truncation policy with rounding mode to round towards positive
	 * infinity. If the result is positive, behaves as for
	 * {@code RoundingMode.UP}; if negative, behaves as for
	 * {@code RoundingMode.DOWN}. Note that this rounding mode never decreases
	 * the calculated value.
	 * 
	 * @see RoundingMode#CEILING
	 */
	CEILING {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.CEILING;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.CEILING;
		}
	},

	/**
	 * Checked truncation policy with rounding mode to round towards negative
	 * infinity. If the result is positive, behave as for
	 * {@code RoundingMode.DOWN}; if negative, behave as for
	 * {@code RoundingMode.UP}. Note that this rounding mode never increases the
	 * calculated value.
	 * 
	 * @see RoundingMode#FLOOR
	 */
	FLOOR {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.FLOOR;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.FLOOR;
		}
	},

	/**
	 * Checked truncation policy with rounding mode to round towards
	 * {@literal "nearest neighbor"} unless both neighbors are equidistant, in
	 * which case round up. Behaves as for {@code RoundingMode.UP} if the
	 * discarded fraction is &ge; 0.5; otherwise, behaves as for
	 * {@code RoundingMode.DOWN}. Note that this is the rounding mode commonly
	 * taught at school.
	 * 
	 * @see RoundingMode#HALF_UP
	 */
	HALF_UP {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.HALF_UP;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.HALF_UP;
		}
	},

	/**
	 * Checked truncation policy with rounding mode to round towards
	 * {@literal "nearest neighbor"} unless both neighbors are equidistant, in
	 * which case round down. Behaves as for {@code RoundingMode.UP} if the
	 * discarded fraction is &gt; 0.5; otherwise, behaves as for
	 * {@code RoundingMode.DOWN}.
	 * 
	 * @see RoundingMode#HALF_DOWN
	 */
	HALF_DOWN {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.HALF_DOWN;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.HALF_DOWN;
		}
	},

	/**
	 * Checked truncation policy with rounding mode to round towards the
	 * {@literal "nearest neighbor"} unless both neighbors are equidistant, in
	 * which case, round towards the even neighbor. Behaves as for
	 * {@code RoundingMode.HALF_UP} if the digit to the left of the discarded
	 * fraction is odd; behaves as for {@code RoundingMode.HALF_DOWN} if it's
	 * even. Note that this is the rounding mode that statistically minimizes
	 * cumulative error when applied repeatedly over a sequence of calculations.
	 * It is sometimes known as {@literal "Banker's rounding,"} and is chiefly
	 * used in the USA. This rounding mode is analogous to the rounding policy
	 * used for {@code float} and {@code double} arithmetic in Java.
	 * 
	 * @see RoundingMode#HALF_EVEN
	 */
	HALF_EVEN {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.HALF_EVEN;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.HALF_EVEN;
		}
	},

	/**
	 * Checked truncation policy with rounding mode to assert that the requested
	 * operation has an exact result, hence no rounding is necessary. If this
	 * rounding mode is specified on an operation that yields an inexact result,
	 * an {@code ArithmeticException} is thrown.
	 * 
	 * @see RoundingMode#UNNECESSARY
	 */
	UNNECESSARY {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.UNNECESSARY;
		}
		@Override
		public final UncheckedRounding toUncheckedRounding() {
			return UncheckedRounding.UNNECESSARY;
		}
	};

	/**
	 * Returns {@link OverflowMode#CHECKED}.
	 * 
	 * @return CHECKED overflow mode
	 */
	@Override
	public final OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	/**
	 * Returns the policy with the same {@link #getRoundingMode() rounding mode}
	 * as this checked rounding policy but for {@link OverflowMode#UNCHECKED
	 * UNCHECKED} {@link #getOverflowMode() overflow mode}.
	 * 
	 * @return the {@link UncheckedRounding} counterpart to this policy.
	 */
	abstract public UncheckedRounding toUncheckedRounding();

	/**
	 * Returns "CHECKED/(name)" where {@code (name)} stands for the {@link #name()} of this constant.
	 * 
	 * @return a string like "CHECKED/HALF_UP"
	 */
	@Override
	public final String toString() {
		return "CHECKED/" + name();
	}

	/**
	 * Immutable set with all values of this enum. Avoids object creation in
	 * contrast to {@link #values()}.
	 */
	public static final Set<CheckedRounding> VALUES = Collections.unmodifiableSet(EnumSet.allOf(CheckedRounding.class));

	/**
	 * Returns the checked rounding constant for the given rounding mode.
	 * 
	 * @param roundingMode
	 *            the rounding mode
	 * @return the constant corresponding to the given rounding mode
	 */
	public static final CheckedRounding valueOf(RoundingMode roundingMode) {
		return ByRoundingMode.VALUES_BY_ROUNDING_MODE_ORDINAL[roundingMode.ordinal()];
	}

	private static class ByRoundingMode {
		private static final CheckedRounding[] VALUES_BY_ROUNDING_MODE_ORDINAL = sortByRoundingModeOrdinal();
	
		private static final CheckedRounding[] sortByRoundingModeOrdinal() {
			final CheckedRounding[] sorted = new CheckedRounding[VALUES.size()];
			for (final CheckedRounding dr : VALUES) {
				sorted[dr.getRoundingMode().ordinal()] = dr;
			}
			return sorted;
		}
	}
}
