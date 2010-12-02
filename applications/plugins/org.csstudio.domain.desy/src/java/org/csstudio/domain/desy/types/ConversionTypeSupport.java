/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.types;

import javax.annotation.Nonnull;

/**
 *
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 01.12.2010
 */
public abstract class ConversionTypeSupport<T extends ICssValueType> extends TypeSupport<T> {

	private static boolean INSTALLED = false;

	public static void install() {
		if (INSTALLED) {
			return;
		}

		TypeSupport.addTypeSupport(CssDouble.class, new ConversionTypeSupport<CssDouble>() {
			@Override
			public CssDouble convertToDDouble(@Nonnull final CssDouble d) {
				return d;
			}
		});
		TypeSupport.addTypeSupport(CssLong.class, new ConversionTypeSupport<CssLong>() {
			@Override
			public CssDouble convertToDDouble(@Nonnull final CssLong l) {
				final Long value = l.getValue();
				return new CssDouble(value.doubleValue(), l.getTimestamp());
			}
		});
		TypeSupport.addTypeSupport(CssString.class, new ConversionTypeSupport<CssString>() {
			@Override
			public CssDouble convertToDDouble(@Nonnull final CssString s) throws ConversionTypeSupportException {
				final String value = s.getValue();
				try {
					return new CssDouble(Double.valueOf(value), s.getTimestamp());
				} catch(final NumberFormatException e) {
					throw new ConversionTypeSupportException(createConversionFailedMsg(s.getClass(), CssDouble.class), e);
				}
			}
		});

		INSTALLED = true;
	}

	protected static String createConversionFailedMsg(final Class<? extends ICssValueType> from,
			final Class<? extends ICssValueType> to) {
		return "Type conversion " + from.getName() + " to " + to.getName() + " failed.";
	}

	public abstract CssDouble convertToDDouble(@Nonnull final T value) throws ConversionTypeSupportException;
}
