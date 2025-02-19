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
package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.util.UnscaledUtil;

/**
 * Base class for unit tests with an unscaled decimal operand.
 */
abstract public class AbstractUnscaledToDecimalTest extends AbstractLongValueToDecimalTest {

	protected final int scale;

	public AbstractUnscaledToDecimalTest(int scale, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.scale = scale;
	}

	@Override
	protected long[] getSpecialLongOperands() {
		return UnscaledUtil.getSpecialUnscaledOperands(scale);
	}

	@Override
	protected int getRandomTestCount() {
		return 1000;
	}
	
	protected BigDecimal toBigDecimal(long unscaled) {
		final BigDecimal result = BigDecimal.valueOf(unscaled, scale).setScale(getScale(), getRoundingMode());
		if (result.unscaledValue().bitLength() > 63) {
			throw new IllegalArgumentException("Overflow: " + result);
		}
		return result;
	}

}
