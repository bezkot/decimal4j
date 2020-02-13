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
package org.decimal4j.op.arith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#pow(int)}
 */
@RunWith(Parameterized.class)
public class RoundTest extends AbstractDecimalToDecimalTest {
	
	private final int precision;
	
	public RoundTest(ScaleMetrics scaleMetrics, int precision, TruncationPolicy truncationPolicy, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.precision = precision;
	}

	@Parameters(name = "{index}: {0}, precision={1}, {2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			final int scale = s.getScale();
			for (int precision = (scale - 18) - 1; precision <= scale + 1; precision++) {
				for (final TruncationPolicy tp : TestSettings.POLICIES) {
					final DecimalArithmetic arith = s.getArithmetic(tp);
					data.add(new Object[] {s, precision, tp, arith});
				}
			}
		}
		return data;
	}
	
	@Override
	protected int getRandomTestCount() {
		return 1000;
	}
	
	@Override
	protected String operation() {
		return "round";
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		if (getScale() - precision <= 18) {
			return operand.setScale(precision, getRoundingMode()).setScale(getScale(), getRoundingMode());
		}
		throw new IllegalArgumentException("scale - precision must be <= 18 but was " + (getScale() - precision) + " for scale=" + getScale() + " and precision=" + precision);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> operand) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return operand.round(precision);
		} else {
			if (isUnchecked() && RND.nextBoolean()) {
				return operand.round(precision, getRoundingMode());
			} else {
				return operand.round(precision, getTruncationPolicy());
			}
		}
	}
	
}
