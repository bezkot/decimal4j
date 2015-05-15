/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runners.Parameterized.Parameters;

/**
 * Base class for unit tests with an unscaled decimal operand.
 */
abstract public class AbstractUnscaledOperandTest extends AbstractDecimalLongToDecimalTest {

	protected final int scale;

	public AbstractUnscaledOperandTest(ScaleMetrics sm, TruncationPolicy tp, int scale, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.scale = scale;
	}

	@Parameters(name = "{index}: {0}, {1}, scale={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetic arith = s.getArithmetic(tp);
				for (int scale : getScales(s.getScale())) {
					data.add(new Object[] { s, tp, scale, arith });
				}
			}
		}
		return data;
	}

	private static Set<Integer> getScales(int scale) {
		final Set<Integer> vals = new TreeSet<Integer>();
		switch (TestSettings.TEST_CASES) {
		case TINY:
			vals.addAll(Arrays.asList(-1, 0, scale - 1, scale, scale + 1, 18, 19));
			break;
		case SMALL:
			vals.addAll(Arrays.asList(-1, 0, 1, scale - 1, scale, scale + 1, 17, 18, 19));
			break;
		case STANDARD:
			vals.addAll(Arrays.asList(-10, -1, 0, 1, scale - 1, scale, scale + 1, 17, 18, 19, 30));
			break;
		case ALL:
			vals.addAll(Arrays.asList(-100, -20, -10, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
					17, 18, 19, 20, 25, 30, 100));
			break;
		default:
			throw new RuntimeException("illegal test cases: " + TestSettings.TEST_CASES);
		}
		return vals;
	}

	@Override
	protected long randomLongOperand() {
		return RND.nextBoolean() ? RND.nextLong() : RND.nextInt();
	}

	@Override
	protected long[] getSpecialLongOperands() {
		return getSpecialValues(getScaleMetrics());
	}

	@Override
	protected int getRandomTestCount() {
		return 1000;
	}

	protected BigDecimal toBigDecimal(long unscaled) {
		BigDecimal other = BigDecimal.valueOf(unscaled, scale);
		if (scale != getScale()) {
			other = other.setScale(getScale(), getRoundingMode());
			if (isUnchecked()) {
				other = BigDecimal.valueOf(other.unscaledValue().longValue(), getScale());
			} else {
				// check for overflow
				JDKSupport.bigIntegerToLongValueExact(other.unscaledValue());
			}
		}
		return other;
	}

}
