/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 decimal4j (tools4j), Marco Terzer
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

/**
 * Helper class for arithmetic operations with overflow checks.
 */
final class Checked {

	/**
	 * Returns true if the addition {@code long1 + long2 = result} has resulted
	 * in an overflow.
	 * 
	 * @param long1
	 *            the first summand
	 * @param long2
	 *            the second summand
	 * @param result
	 *            the sum
	 * @return true if the calculation resulted in an overflow
	 */
	static final boolean isAddOverflow(long long1, long long2, long result) {
		return (long1 ^ long2) >= 0 & (long1 ^ result) < 0;
	}

	/**
	 * Returns true if the subtraction {@code minuend - subtrahend = result} has
	 * resulted in an overflow.
	 * 
	 * @param minuend
	 *            the minuend to subtract from
	 * @param subtrahend
	 *            the subtrahend to subtract
	 * @param result
	 *            the difference
	 * @return true if the calculation resulted in an overflow
	 */
	static final boolean isSubtractOverflow(long minuend, long subtrahend, long result) {
		return (minuend ^ subtrahend) < 0 & (minuend ^ result) < 0;
	}

	/**
	 * Returns true if the quotient {@code dividend / divisor} will result in an
	 * overflow.
	 * 
	 * @param dividend
	 *            the dividend
	 * @param divisor
	 *            the divisor
	 * @return true if the calculation will result in an overflow
	 */
	static final boolean isDivideOverflow(long dividend, long divisor) {
		return dividend == Long.MIN_VALUE & divisor == -1;
	}

	/**
	 * Returns the sum {@code (long1 + long2)} of the two {@code long} values
	 * throwing an exception if an overflow occurs.
	 * 
	 * @param long1
	 *            the first summand
	 * @param long2
	 *            the second summand
	 * @return the sum of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long addLong(long long1, long long2) {
		final long result = long1 + long2;
		if (isAddOverflow(long1, long2, result)) {
			throw new ArithmeticException("Overflow: " + long1 + " + " + long2 + " = " + result);
		}
		return result;
	}

	/**
	 * Returns the sum {@code (uDecimal1 + uDecimal2)} of the two unsigned
	 * decimal values throwing an exception if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the two unsigned decimals
	 * @param uDecimal1
	 *            the first summand
	 * @param uDecimal2
	 *            the second summand
	 * @return the sum of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long add(DecimalArithmetic arith, long uDecimal1, long uDecimal2) {
		final long result = uDecimal1 + uDecimal2;
		if ((uDecimal1 ^ uDecimal2) >= 0 & (uDecimal1 ^ result) < 0) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal1) + " + " + arith.toString(uDecimal2)
					+ " = " + arith.toString(result));
		}
		return result;
	}

	/**
	 * Returns the difference {@code (lMinuend - lSubtrahend)} of the two
	 * {@code long} values throwing an exception if an overflow occurs.
	 * 
	 * @param lMinuend
	 *            the minuend
	 * @param lSubtrahend
	 *            the subtrahend
	 * @return the difference of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long subtractLong(long lMinuend, long lSubtrahend) {
		final long result = lMinuend - lSubtrahend;
		if (isSubtractOverflow(lMinuend, lSubtrahend, result)) {
			throw new ArithmeticException("Overflow: " + lMinuend + " - " + lSubtrahend + " = " + result);
		}
		return result;
	}

	/**
	 * Returns the difference {@code (uDecimalMinuend - uDecimalSubtrahend)} of
	 * the two unscaled decimal values throwing an exception if an overflow
	 * occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the two unsigned decimals
	 * @param uDecimalMinuend
	 *            the minuend
	 * @param uDecimalSubtrahend
	 *            the subtrahend
	 * @return the difference of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long subtract(DecimalArithmetic arith, long uDecimalMinuend, long uDecimalSubtrahend) {
		final long result = uDecimalMinuend - uDecimalSubtrahend;
		if (isSubtractOverflow(uDecimalMinuend, uDecimalSubtrahend, result)) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalMinuend) + " - "
					+ arith.toString(uDecimalSubtrahend) + " = " + arith.toString(result));
		}
		return result;
	}

	/**
	 * Returns the product {@code (lValue1 * lValue2)} of the two {@code long}
	 * values throwing an exception if an overflow occurs.
	 * 
	 * @param lValue1
	 *            the first factor
	 * @param lValue2
	 *            the second factor
	 * @return the product of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long multiplyLong(long lValue1, long lValue2) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(lValue1) + Long.numberOfLeadingZeros(~lValue1)
				+ Long.numberOfLeadingZeros(lValue2) + Long.numberOfLeadingZeros(~lValue2);
		/*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's <
		 * Long.SIZE it's definitely bad. We do the leadingZeros check to avoid
		 * the division below if at all possible.
		 * 
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a
		 * are 0 and 1. We take care of all a < 0 with their own check, because
		 * in particular, the case a == -1 will incorrectly pass the division
		 * check below.
		 * 
		 * In all other cases, we check that either a is 0 or the result is
		 * consistent with division.
		 */
		final long result = lValue1 * lValue2;
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE || (lValue1 < 0 & lValue2 == Long.MIN_VALUE)
				|| (lValue1 != 0 && (result / lValue1) != lValue2)) {
			throw new ArithmeticException("Overflow: " + lValue1 + " * " + lValue2 + " = " + result);
		}
		return result;
	}

	/**
	 * Returns the product {@code (uDecimal * lValue)} of an unsigned decimal
	 * value and a {@code long} value throwing an exception if an overflow
	 * occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the first unsigned decimal
	 *            argument
	 * @param uDecimal
	 *            the first factor
	 * @param lValue
	 *            the second factor
	 * @return the product of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long multiplyByLong(DecimalArithmetic arith, long uDecimal, long lValue) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal) + Long.numberOfLeadingZeros(~uDecimal)
				+ Long.numberOfLeadingZeros(lValue) + Long.numberOfLeadingZeros(~lValue);
		/*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's <
		 * Long.SIZE it's definitely bad. We do the leadingZeros check to avoid
		 * the division below if at all possible.
		 * 
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a
		 * are 0 and 1. We take care of all a < 0 with their own check, because
		 * in particular, the case a == -1 will incorrectly pass the division
		 * check below.
		 * 
		 * In all other cases, we check that either a is 0 or the result is
		 * consistent with division.
		 */
		final long result = uDecimal * lValue;
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE || (uDecimal < 0 & lValue == Long.MIN_VALUE)
				|| (uDecimal != 0 && (result / uDecimal) != lValue)) {
			throw new ArithmeticException(
					"Overflow: " + arith.toString(uDecimal) + " * " + lValue + " = " + arith.toString(result));
		}
		return result;
	}

	/**
	 * Returns the quotient {@code (lDividend / lDivisor)} of the two
	 * {@code long} values throwing an exception if an overflow occurs.
	 * 
	 * @param lDividend
	 *            the dividend to divide
	 * @param lDivisor
	 *            the divisor to divide by
	 * @return the quotient of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long divideLong(long lDividend, long lDivisor) {
		if (lDivisor == -1 & lDividend == Long.MIN_VALUE) {
			throw new ArithmeticException("Overflow: " + lDividend + " / " + lDivisor + " = " + Long.MIN_VALUE);
		}
		return lDividend / lDivisor;
	}

	/**
	 * Returns the quotient {@code (uDecimalDividend / lDivisor)} of an unscaled
	 * decimal value and a {@code long} value throwing an exception if an
	 * overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the first unsigned decimal
	 *            argument
	 * @param uDecimalDividend
	 *            the dividend to divide
	 * @param lDivisor
	 *            the divisor to divide by
	 * @return the quotient of the two values
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long divideByLong(DecimalArithmetic arith, long uDecimalDividend, long lDivisor) {
		if (lDivisor == 0) {
			throw new ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + lDivisor);
		}
		if (lDivisor == -1 & uDecimalDividend == Long.MIN_VALUE) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalDividend) + " / " + lDivisor + " = "
					+ arith.toString(Long.MIN_VALUE));
		}
		return uDecimalDividend / lDivisor;
	}

	/**
	 * Returns the absolute value {@code |value|} throwing an exception if an
	 * overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param value
	 *            the number whose absolute value to return
	 * @return the absolute of the specified value
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long abs(DecimalArithmetic arith, long value) {
		final long abs = Math.abs(value);
		if (abs < 0) {
			throw new ArithmeticException("Overflow: abs(" + arith.toString(value) + ") = " + abs);
		}
		return abs;
	}

	/**
	 * Returns the negation {@code (-value)} throwing an exception if an
	 * overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param value
	 *            the number to negate
	 * @return the negation of the specified value
	 * @throws ArithmeticException
	 *             if the calculation results in an overflow
	 */
	public static final long negate(DecimalArithmetic arith, long value) {
		final long neg = -value;
		if (value != 0 & (value ^ neg) >= 0) {
			throw new ArithmeticException("Overflow: -" + arith.toString(value) + " = " + neg);
		}
		return neg;
	}

	// no instances
	private Checked() {
	}

}
