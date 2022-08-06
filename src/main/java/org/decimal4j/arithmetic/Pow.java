/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Contains static methods to calculate powers of a Decimal number.
 */
final class Pow {

	/**
	 * Constant for {@code floor(sqrt(Long.MAX_VALUE))}
	 */
	private static final long FLOOR_SQRT_MAX_LONG = 3037000499L;

	private static final void checkExponent(int exponent) {
		if (exponent < -999999999 || exponent > 999999999) {
			throw new IllegalArgumentException("Exponent must be in [-999999999,999999999] but was: " + exponent);
		}
	}

	/**
	 * Calculates the power <code>(lBase<sup>exponent</sup>)</code>. Overflows are
	 * silently ignored.
	 * 
	 * @param arith
	 *            the arithmetic associated with {@code lBase}
	 * @param rounding
	 *            the rounding to apply if rounding is necessary for negative
	 *            exponents
	 * @param lBase
	 *            the unscaled decimal base value
	 * @param exponent
	 *            the exponent
	 * @return <code>round(lBase<sup>exponent</sup>)</code>
	 * @throws ArithmeticException
	 *             if {@code lBase==0} and the exponent is negative or if
	 *             {@code roundingMode==UNNECESSARY} and rounding is necessary
	 */
	public static final long powLong(DecimalArithmetic arith, DecimalRounding rounding, long lBase, int exponent) {
		checkExponent(exponent);
		final SpecialPowResult special = SpecialPowResult.getFor(arith, lBase, exponent);
		if (special != null) {
			return special.pow(arith, lBase, exponent);
		}
		return powLong(rounding, lBase, exponent);
	}

	private static final long powLong(DecimalRounding rounding, long lBase, int exponent) {
		if (exponent >= 0) {
			return powLongWithPositiveExponent(lBase, exponent);
		} else {
			// result is 1/powered
			// we have dealt with special cases above hence powered is neither
			// of 0, 1, -1
			// and everything else can't be 0.5 because sqrt_i(0.5) is not real
			final int sgn = lBase > 0 | (exponent & 0x1) == 0 ? 1 : -1;// lBase
																		// cannot
																		// be 0
			return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}

	/**
	 * Calculates the power <code>(lBase<sup>exponent</sup>)</code>. An exception is
	 * thrown if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with {@code lBase}
	 * @param rounding
	 *            the rounding to apply if rounding is necessary for negative
	 *            exponents
	 * @param lBase
	 *            the unscaled decimal base value
	 * @param exponent
	 *            the exponent
	 * @return <code>round(lBase<sup>exponent</sup>)</code>
	 * @throws ArithmeticException
	 *             if {@code lBase==0} and the exponent is negative, if
	 *             {@code roundingMode==UNNECESSARY} and rounding is necessary
	 *             or if an overflow occurs and the arithmetic's
	 *             {@link OverflowMode} is set to throw an exception
	 */
	public static final long powLongChecked(DecimalArithmetic arith, DecimalRounding rounding, long lBase, int exponent) {
		checkExponent(exponent);
		final SpecialPowResult special = SpecialPowResult.getFor(arith, lBase, exponent);
		if (special != null) {
			return special.pow(arith, lBase, exponent);
		}
		return powLongChecked(rounding, lBase, exponent);
	}

	private static final long powLongChecked(DecimalRounding rounding, long lBase, int exponent) {
		if (exponent >= 0) {
			return powLongCheckedWithPositiveExponent(lBase, exponent);
		} else {
			// result is 1/powered
			// we have dealt with special cases above hence powered is neither
			// of 0, 1, -1
			// and everything else can't be 0.5 because sqrt_i(0.5) is not real
			final int sgn = lBase > 0 | (exponent & 0x1) == 0 ? 1 : -1;// lBase
																		// cannot
																		// be 0
			return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}

	private static final long powLongCheckedOrUnchecked(OverflowMode overflowMode, DecimalRounding rounding, long longBase, int exponent) {
		return overflowMode == OverflowMode.UNCHECKED ? powLong(rounding, longBase, exponent)
				: powLongChecked(rounding, longBase, exponent);
	}

	/**
	 * Power function for checked or unchecked arithmetic. The result is within
	 * 1 ULP for positive exponents.
	 * 
	 * @param arith
	 *            the arithmetic
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimalBase
	 *            the unscaled base
	 * @param exponent
	 *            the exponent
	 * @return {@code uDecimalbase ^ exponent}
	 */
	public static final long pow(DecimalArithmetic arith, DecimalRounding rounding, long uDecimalBase, int exponent) {
		checkExponent(exponent);
		final SpecialPowResult special = SpecialPowResult.getFor(arith, uDecimalBase, exponent);
		if (special != null) {
			return special.pow(arith, uDecimalBase, exponent);
		}

		// some other special cases
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();

		final long intVal = scaleMetrics.divideByScaleFactor(uDecimalBase);
		final long fraVal = uDecimalBase - scaleMetrics.multiplyByScaleFactor(intVal);
		if (exponent >= 0 & fraVal == 0) {
			// integer
			final long result = powLongCheckedOrUnchecked(arith.getOverflowMode(), rounding, intVal, exponent);
			return longToUnscaledCheckedOrUnchecekd(arith, uDecimalBase, exponent, result);
		}
		if (exponent < 0 & intVal == 0) {
			final long one = scaleMetrics.getScaleFactor();
			if ((one % fraVal) == 0) {
				// inverted value is an integer
				final long result = powLongCheckedOrUnchecked(arith.getOverflowMode(), rounding, one / fraVal,
						-exponent);
				return longToUnscaledCheckedOrUnchecekd(arith, uDecimalBase, exponent, result);
			}
		}
		try {
			return powWithPrecision18(arith, rounding, intVal, fraVal, exponent);
		} catch (IllegalArgumentException e) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
		}
	}

	// PRECONDITION: n != 0 and n in [-999999999,999999999]
	private static final long powWithPrecision18(DecimalArithmetic arith, DecimalRounding rounding, long ival, long fval, int n) {
		// eliminate sign
		final int sgn = ((n & 0x1) != 0) ? Long.signum(ival | fval) : 1;
		final long absInt = Math.abs(ival);
		final long absFra = Math.abs(fval);
		final DecimalRounding powRounding = n >= 0 ? rounding : RoundingInverse.RECIPROCAL.invert(rounding);

		// 36 digit left hand side, initialized with base value
		final UnsignedDecimal9i36f lhs = UnsignedDecimal9i36f.THREAD_LOCAL_1.get().init(absInt, absFra,
				arith.getScaleMetrics());

		// 36 digit accumulator, initialized with one
		final UnsignedDecimal9i36f acc = UnsignedDecimal9i36f.THREAD_LOCAL_2.get().initOne();

		// ready to carry out power calculation...
		int mag = Math.abs(n);
		boolean seenbit = false; // avoid squaring ONE
		for (int i = 1;; i++) { // for each bit [top bit ignored]
			mag += mag; // shift left 1 bit
			if (mag < 0) { // top bit is set
				if (seenbit) {
					acc.multiply(sgn, lhs, powRounding);// acc=acc*x
				} else {
					seenbit = true;
					acc.init(lhs); // acc=x
				}
			}
			if (i == 31) {
				break; // that was the last bit
			}
			if (seenbit) {
				acc.multiply(sgn, acc, powRounding); // acc=acc*acc [square]
			}// else (!seenbit) no point in squaring ONE
		}

		if (n < 0) {
			return acc.getInverted(sgn, arith, rounding, powRounding);
		}
		return acc.getDecimal(sgn, arith, rounding);
	}

	private static final long powLongWithPositiveExponent(long lBase, int exponent) {
		assert(exponent > 0);

		long accum = 1;
		while (true) {
			switch (exponent) {
			case 0:
				return accum;
			case 1:
				return accum * lBase;
			default:
				if ((exponent & 1) != 0) {
					accum *= lBase;
				}
				exponent >>= 1;
				if (exponent > 0) {
					lBase *= lBase;
				}
			}
		}
	}

	private static final long powLongCheckedWithPositiveExponent(long lBase, int exponent) {
		assert(exponent > 0);
		if (lBase >= -2 & lBase <= 2) {
			switch ((int) lBase) {
			case 0:
				return (exponent == 0) ? 1 : 0;
			case 1:
				return 1;
			case (-1):
				return ((exponent & 1) == 0) ? 1 : -1;
			case 2:
				if (exponent >= Long.SIZE - 1) {
					throw new ArithmeticException("Overflow: " + lBase + "^" + exponent);
				}
				return 1L << exponent;
			case (-2):
				if (exponent >= Long.SIZE) {
					throw new ArithmeticException("Overflow: " + lBase + "^" + exponent);
				}
				return ((exponent & 1) == 0) ? (1L << exponent) : (-1L << exponent);
			default:
				throw new AssertionError();
			}
		}
		long accum = 1;
		while (true) {
			switch (exponent) {
			case 0:
				return accum;
			case 1:
				return Checked.multiplyLong(accum, lBase);
			default:
				if ((exponent & 1) != 0) {
					accum = Checked.multiplyLong(accum, lBase);
				}
				exponent >>= 1;
				if (exponent > 0) {
					if (lBase > FLOOR_SQRT_MAX_LONG | lBase < -FLOOR_SQRT_MAX_LONG) {
						throw new ArithmeticException("Overflow: " + lBase + "^" + exponent);
					}
					lBase *= lBase;
				}
			}
		}
	}

	private static final long longToUnscaledCheckedOrUnchecekd(DecimalArithmetic arith, long uBase, int exponent, long longResult) {
		if (!arith.getOverflowMode().isChecked()) {
			return LongConversion.longToUnscaledUnchecked(arith.getScaleMetrics(), longResult);
		}
		try {
			return LongConversion.longToUnscaled(arith.getScaleMetrics(), longResult);
		} catch (IllegalArgumentException e) {
			throw new ArithmeticException("Overflow: " + arith.toString(uBase) + "^" + exponent + "=" + longResult);
		}
	}

	// no instances
	private Pow() {
	}

}
