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
package org.decimal4j.op.convert;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.op.AbstractDoubleToDecimalTest;
import org.decimal4j.op.util.FloatAndDoubleUtil;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Test {@link DecimalArithmetic#fromDouble(double)} via
 * {@link DecimalFactory#valueOf(double)}, {@link MutableDecimal#set(double)}
 * and the static {@code valueOf(double)} methods of the Immutable Decimal
 * implementations. The same conversion method is also used in other operations
 * that are involving doubles.
 */
@RunWith(Parameterized.class)
public class FromDoubleTest extends AbstractDoubleToDecimalTest {

	public FromDoubleTest(ScaleMetrics s, RoundingMode mode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				final DecimalArithmetic arith = s.getArithmetic(mode);
				data.add(new Object[] { s, mode, arith });
			}
		}
		return data;
	}

	@Test
	public void run0p99999999999999994_failureBuild_513() {
		//failed for 0.99999999999999984 to 0.99999999999999994, scale 0, rounding UP / CEILING
		final double input = 0.99999999999999994;
		runTest(getScaleMetrics(), "0p99999999999999994_failureBuild_513", input);
	}

	@Test
	public void runNeg0p99999999999999994_failureBuild_513() {
		//failed for -0.99999999999999984 to -0.99999999999999994, scale 0, rounding UP, FLOOR
		final double input = -0.99999999999999994;
		runTest(getScaleMetrics(), "Neg0p99999999999999994_failureBuild_513", input);
	}

	@Override
	protected String operation() {
		return "fromDouble";
	}

	@Test
	public void testProblem1() {
		if (getScale() == 4 && getRoundingMode() == RoundingMode.HALF_DOWN) {
			runTest(getScaleMetrics(), "testProblem1", 3.354719257560035e-4);
		}
	}

	@Test
	public void testProblem2() {
		if (getScale() == 4 && getRoundingMode() == RoundingMode.HALF_DOWN) {
			runTest(getScaleMetrics(), "testProblem2", 3.9541250940045014e-4);
		}
	}

	@Override
	protected BigDecimal expectedResult(double operand) {
		return FloatAndDoubleUtil.doubleToBigDecimal(operand, getScale(), getRoundingMode());
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, double operand) {
		switch(RND.nextInt(4)) {
		case 0:
			//Factory, immutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return getDecimalFactory(scaleMetrics).valueOf(operand);
			} else {
				return getDecimalFactory(scaleMetrics).valueOf(operand, getRoundingMode());
			}
		case 1:
			//Factory, mutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return getDecimalFactory(scaleMetrics).newMutable().set(operand);
			} else {
				return getDecimalFactory(scaleMetrics).newMutable().set(operand, getRoundingMode());
			}
		case 2:
			//mutable constructor
			if (isRoundingDefault()) {
				return newMutableInstance(scaleMetrics, operand);
			}//else: fallthrough
		case 3://fall through
		default:
			//Immutable, valueOf method
			return valueOf(scaleMetrics, operand);
		}
	}

	private <S extends ScaleMetrics> Decimal<S> newMutableInstance(S scaleMetrics, double operand) {
		try {
			@SuppressWarnings("unchecked")
			final Class<Decimal<S>> clazz = (Class<Decimal<S>>) Class.forName(getMutableClassName());
			return clazz.getConstructor(double.class).newInstance(operand);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			throw new RuntimeException("could not invoke constructor, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke constructor, e=" + e, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <S extends ScaleMetrics> Decimal<S> valueOf(S scaleMetrics, double operand) {
		try {
			final Class<?> clazz = Class.forName(getImmutableClassName());
			if (isRoundingDefault() && RND.nextBoolean()) {
				return (Decimal<S>) clazz.getMethod("valueOf", double.class).invoke(null, operand);
			} else {
				return (Decimal<S>) clazz.getMethod("valueOf", double.class, RoundingMode.class).invoke(null, operand, getRoundingMode());
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException)e.getTargetException();
			}
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		}
	}

}
