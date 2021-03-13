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

import java.io.IOException;

import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.ScaleMetrics;

/**
 * Base class for arithmetic implementations without overflow check for the special
 * case with {@link Scale0f}, that is, for longs.
 */
abstract public class AbstractUncheckedScale0fArithmetic extends AbstractUncheckedArithmetic {
	
	@Override
	public final ScaleMetrics getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public final int getScale() {
		return 0;
	}

	@Override
	public final long one() {
		return 1L;
	}

	@Override
	public final long addLong(long uDecimal, long lValue) {
		return uDecimal + lValue;
	}

	@Override
	public final long subtractLong(long uDecimal, long lValue) {
		return uDecimal - lValue;
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return uDecimal1 * uDecimal2;
	}
	
	@Override
	public final long square(long uDecimal) {
		return uDecimal * uDecimal;
	}

	@Override
	public final long fromLong(long value) {
		return value;
	}

	@Override
	public final long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public final String toString(long uDecimal) {
		return StringConversion.longToString(uDecimal);
	}

	@Override
	public final void toString(long uDecimal, Appendable appendable) throws IOException {
		StringConversion.longToString(uDecimal, appendable);
	}
}
