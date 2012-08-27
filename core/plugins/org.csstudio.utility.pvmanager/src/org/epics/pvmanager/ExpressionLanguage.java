/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import org.epics.pvmanager.expression.ChannelExpressionList;
import org.epics.pvmanager.expression.ChannelExpression;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.WriteExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.WriteExpression;
import org.epics.pvmanager.expression.SourceRateExpression;
import org.epics.pvmanager.expression.SourceRateReadWriteExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpressionList;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpressionListImpl;
import org.epics.pvmanager.expression.SourceRateExpressionList;
import org.epics.pvmanager.expression.SourceRateReadWriteExpressionList;
import org.epics.pvmanager.expression.WriteExpressionList;
import org.epics.util.time.TimeDuration;

/**
 * Operators to constructs expression of PVs that the {@link PVManager} will
 * be able to monitor.
 *
 * @author carcassi
 */
public class ExpressionLanguage {

    static {
        // Install support for basic java types
        BasicTypeSupport.install();
    }
    
    private ExpressionLanguage() {}
    
    /**
     * Creates a constant expression that always return that object.
     * This is useful to test expressions or to introduce data that is available
     * at connection time at that will not change.
     * 
     * @param <T> type of the value
     * @param value the actual value
     * @return an expression that is always going to return the given value
     */
    public static <T> DesiredRateExpression<T> constant(T value) {
        return constant(value, value.toString());
    }
    
    /**
     * Creates a constant expression that always return that object, with the
     * given name for the expression.
     * This is useful to test expressions or to introduce data that is available
     * at connection time at that will not change.
     * 
     * @param <T> type of the value
     * @param value the actual value
     * @param name the name of the expression
     * @return an expression that is always going to return the given value
     */
    public static <T> DesiredRateExpression<T> constant(T value, String name) {
        Class<?> clazz = Object.class;
        if (value != null)
            clazz = value.getClass();
        @SuppressWarnings("unchecked")
        ValueCache<T> cache = (ValueCache<T>) new ValueCache(clazz);
        if (value != null)
            cache.setValue(value);
        return new DesiredRateExpressionImpl<T>(new DesiredRateExpressionListImpl<T>(), cache, name);
    }

    /**
     * A channel with the given name of any type. This expression can be
     * used both in a read and a write expression.
     *
     * @param name the channel name
     * @return an expression representing the channel
     */
    public static ChannelExpression<Object, Object> channel(String name) {
        return channel(name, Object.class, Object.class);
    }

    /**
     * A channel with the given name and type. This expression can be
     * used both in a read and a write expression.
     *
     * @param <R> read payload
     * @param <W> write payload
     * @param name the channel name
     * @param readType type being read
     * @param writeType type being written
     * @return an expression representing the channel
     */
    public static <R, W> ChannelExpression<R, W> channel(String name, Class<R> readType, Class<W> writeType) {
        if (name == null) {
            return new ChannelExpression<R, W>(readType, writeType);
        } else {
            return new ChannelExpression<R, W>(name, readType, writeType);
        }
    }

    /**
     * A list of channels with the given names of any type. This expression can be
     * used both in a read and a write expression.
     *
     * @param names the channel names; can't be null
     * @return an list of expressions representing the channels
     */
    public static ChannelExpressionList<Object, Object> channels(String... names) {
        return channels(Arrays.asList(names), Object.class, Object.class);
    }

    /**
     * A list of channels with the given names and type. This expression can be
     * used both in a read and a write expression.
     *
     * @param <R> read payload
     * @param <W> write payload
     * @param readType type being read
     * @param writeType type being written
     * @param names the channel names; can't be null
     * @return an list of expressions representing the channels
     */
    public static <R, W> ChannelExpressionList<R, W> channels(Collection<String> names, Class<R> readType, Class<W> writeType) {
        return new ChannelExpressionList<R, W>(names, readType, writeType);
    }

    /**
     * A list of channels with the given names of any type. This expression can be
     * used both in a read and a write expression.
     *
     * @param names the channel names; can't be null
     * @return an list of expressions representing the channels
     */
    public static ChannelExpressionList<Object, Object> channels(Collection<String> names) {
        return channels(names, Object.class, Object.class);
    }

    /**
     * Returns all the new values generated by the expression source rate.
     *
     * @param <T> type being read
     * @param expressions source rate expressions
     * @return a new expression
     */
    public static <T> DesiredRateExpressionList<List<T>>
            newValuesOf(SourceRateExpressionList<T> expressions) {
        DesiredRateExpressionList<List<T>> list = new DesiredRateExpressionListImpl<List<T>>();
        for (SourceRateExpression<T> expression : expressions.getSourceRateExpressions()) {
            list.and(newValuesOf(expression));
        }
        return list;
    }

    /**
     * Returns all the new values generated by the expression source rate.
     *
     * @param <T> type being read
     * @param expression source rate expression
     * @return a new expression
     */
    public static <T> DesiredRateExpression<List<T>>
            newValuesOf(SourceRateExpression<T> expression) {
        return new DesiredRateExpressionImpl<List<T>>(expression,
                new QueueCollector<T>(expression.getFunction()),
                expression.getName());
    }

    /**
     * Returns up to maxValues new values generated by the expression source rate.
     *
     * @param <T> type being read
     * @param expression source rate expression
     * @param maxValues maximum number of values to send with each notification
     * @return a new expression
     */
    public static <T> DesiredRateExpression<List<T>>
            newValuesOf(SourceRateExpression<T> expression, int maxValues) {
        return new DesiredRateExpressionImpl<List<T>>(expression,
                new QueueCollector<T>(expression.getFunction(), maxValues),
                expression.getName());
    }

    /**
     * Returns all the values starting the latest value and older up to
     * the time different given by the interval.
     * 
     * @param <T> type being read
     * @param expression expression to read
     * @param maxIntervalBetweenSamples maximum time difference between values
     * @return a new expression
     */
    public static <T> DesiredRateExpression<List<T>>
            timedCacheOf(SourceRateExpression<T> expression, TimeDuration maxIntervalBetweenSamples) {
        return new DesiredRateExpressionImpl<List<T>>(expression,
                new TimedCacheCollector<T>(expression.getFunction(), maxIntervalBetweenSamples),
                expression.getName());
    }

    /**
     * Returns all the values starting the latest value and older up to
     * the time different given by the interval.
     * 
     * @param <T> type being read
     * @param expression expression to read
     * @param maxIntervalBetweenSamples maximum time difference between values
     * @return a new expression
     */
    public static <T> DesiredRateExpression<List<T>>
            timedCacheOf(SourceRateExpression<T> expression, org.epics.pvmanager.util.TimeDuration maxIntervalBetweenSamples) {
        return timedCacheOf(expression, org.epics.pvmanager.util.TimeDuration.asTimeDuration(maxIntervalBetweenSamples));
    }

    /**
     * Expression that returns (only) the latest value computed
     * from a {@code SourceRateExpression}.
     *
     * @param <T> type being read
     * @param expression expression read at the source rate
     * @return a new expression
     */
    public static <T> DesiredRateExpression<T> latestValueOf(SourceRateExpression<T> expression) {
        DesiredRateExpression<List<T>> queue = newValuesOf(expression, 1);
        return new DesiredRateExpressionImpl<T>(queue,
                new LastValueAggregator<T>((Collector<T>) queue.getFunction()),
                expression.getName());
    }

    /**
     * Expression that returns (only) the latest value computed
     * from a {@code SourceRateExpression}.
     *
     * @param <T> type being read
     * @param expressions expressions read at the source rate
     * @return an expression list
     */
    public static <T> DesiredRateExpressionList<T> latestValueOf(SourceRateExpressionList<T> expressions) {
        DesiredRateExpressionList<T> list = new DesiredRateExpressionListImpl<T>();
        for (SourceRateExpression<T> expression : expressions.getSourceRateExpressions()) {
            list.and(latestValueOf(expression));
        }
        return list;
    }

    /**
     * For reads, returns (only) the latest value computed
     * from a {@code SourceRateReadWriteExpression}; for writes, same
     * as the given expression.
     *
     * @param <R> read payload
     * @param <W> write payload
     * @param expression expression read at the source rate
     * @return a new expression
     */
    public static <R, W> DesiredRateReadWriteExpression<R, W> latestValueOf(SourceRateReadWriteExpression<R, W> expression) {
        return new DesiredRateReadWriteExpressionImpl<R, W>(latestValueOf((SourceRateExpression<R>) expression), expression);
    }

    /**
     * For reads, returns (only) the latest value computed
     * from a {@code SourceRateReadWriteExpression}; for writes, same
     * as the given expression.
     *
     * @param <R> read payload
     * @param <W> write payload
     * @param expressions expressions read at the source rate
     * @return a new expression
     */
    public static <R, W> DesiredRateReadWriteExpressionList<R, W> latestValueOf(SourceRateReadWriteExpressionList<R, W> expressions) {
        DesiredRateReadWriteExpressionListImpl<R, W> list = new DesiredRateReadWriteExpressionListImpl<R, W>();
        for (SourceRateReadWriteExpression<R, W> expression : expressions.getSourceRateReadWriteExpressions()) {
            list.and(latestValueOf(expression));
        }
        return list;
    }
    
    /**
     * A user provided single argument function.
     *
     * @param <R> result type
     * @param <A> argument type
     */
    public static interface OneArgFunction<R, A> {
        /**
         * Calculates the new value.
         *
         * @param arg argument
         * @return result
         */
        R calculate(A arg);
    }

    /**
     * A user provided double argument function.
     *
     * @param <R> result type
     * @param <A1> first argument type
     * @param <A2> second argument type
     */
    public static interface TwoArgFunction<R, A1, A2> {
        /**
         * Calculates the new value.
         *
         * @param arg1 first argument
         * @param arg2 second argument
         * @return result
         */
        R calculate(A1 arg1, A2 arg2);
    }

    /**
     * An expression that represents the result of a user provided function.
     *
     * @param <R> result type
     * @param <A> argument type
     * @param function the user provided function
     * @param argExpression expression for the function argument
     * @return a new expression
     */
    public static <R, A> DesiredRateExpression<R> resultOf(final OneArgFunction<R, A> function,
            DesiredRateExpression<A> argExpression) {
        String name = function.getClass().getSimpleName() + "(" + argExpression.getName() + ")";
        final Function<A> arg = argExpression.getFunction();
        return new DesiredRateExpressionImpl<R>(argExpression, new Function<R>() {
            @Override
            public R getValue() {
                return function.calculate(arg.getValue());
            }
        }, name);
    }

    /**
     * An expression that represents the result of a user provided function.
     *
     * @param <R> result type
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param function the user provided function
     * @param arg1Expression expression for the first argument
     * @param arg2Expression expression for the second argument
     * @return a new expression
     */
    public static <R, A1, A2> DesiredRateExpression<R> resultOf(final TwoArgFunction<R, A1, A2> function,
            DesiredRateExpression<? extends A1> arg1Expression, DesiredRateExpression<? extends A2> arg2Expression) {
        return resultOf(function, arg1Expression, arg2Expression, function.getClass().getSimpleName() + "(" + arg1Expression.getName() +
                ", " + arg2Expression.getName() + ")");
    }

    /**
     * An expression that represents the result of a user provided function.
     *
     * @param <R> result type
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param function the user provided function
     * @param arg1Expression expression for the first argument
     * @param arg2Expression expression for the second argument
     * @param name expression name
     * @return a new expression
     */
    public static <R, A1, A2> DesiredRateExpression<R> resultOf(final TwoArgFunction<R, A1, A2> function,
            DesiredRateExpression<? extends A1> arg1Expression, DesiredRateExpression<? extends A2> arg2Expression, String name) {
        final Function<? extends A1> arg1 = arg1Expression.getFunction();
        final Function<? extends A2> arg2 = arg2Expression.getFunction();
        @SuppressWarnings("unchecked")
        DesiredRateExpressionList<? extends Object> argExpressions =
                new DesiredRateExpressionListImpl<Object>().and(arg1Expression).and(arg2Expression);
        return new DesiredRateExpressionImpl<R>(argExpressions,
                new Function<R>() {
                    @Override
                    public R getValue() {
                        return function.calculate(arg1.getValue(), arg2.getValue());
                    }
                }, name);
    }

    /**
     * Filters a data stream, removing updates that match the given function.
     * Looks for objects of a specific type,
     * and filters based on previous and current value.
     * 
     * @param <T> the type to cast to before the filtering
     */
    public static abstract class Filter<T> {

        private final Class<T> clazz;
        private final boolean filterUnmatched;

        Filter() {
            clazz = null;
            filterUnmatched = false;
        }

        /**
         * Creates a filter which looks for and cases data objects of the
         * given class.
         *
         * @param clazz the argument type of the filter
         */
        public Filter(Class<T> clazz) {
            this(clazz, false);
        }

        /**
         * Creates a filter which looks for and cases data objects of the
         * given class. If objects do not match, returns filterUnmatched.
         *
         * @param clazz the argument type of the filter
         * @param filterUnmatched whether objects that don't match the class
         * should be filtered or not
         */
        public Filter(Class<T> clazz, boolean filterUnmatched) {
            this.clazz = clazz;
            this.filterUnmatched = filterUnmatched;
        }

        // This is what the framework should actually call: it does the
        // type checking and casting
        boolean innerFilter(Object previousValue, Object currentValue) {
            if ((previousValue == null || clazz.isInstance(previousValue)) &&
                    (currentValue == null || clazz.isInstance(currentValue))) {
                return filter(clazz.cast(previousValue), clazz.cast(currentValue));
            }
            return filterUnmatched;
        }

        /**
         * Determines whether the new value should be filtered or not. The
         * filtering is done based on the previousValue, which is always a
         * value that passed the filtering. The first value ever to be
         * passed to the filter will have null for previousValue.
         *
         * @param previousValue the previous data update
         * @param currentValue the current data update
         * @return true if the current data update should be dropped
         */
        public abstract boolean filter(T previousValue, T currentValue);

        /**
         * Returns a new filter that is the logical AND of this and the given
         * one.
         *
         * @param filter another filter
         * @return a new filter that is the AND of the two
         */
        public Filter<?> and(final Filter<?> filter) {
            return new Filter<Object>() {

                @Override
                public boolean innerFilter(Object previousValue, Object currentValue) {
                    return super.innerFilter(previousValue, currentValue) &&
                            filter.innerFilter(previousValue, currentValue);
                }

                @Override
                public boolean filter(Object previousValue, Object currentValue) {
                    throw new UnsupportedOperationException("Not used.");
                }

            };
        }

        /**
         * Returns a new filter that is the logical OR of this and the given
         * one.
         *
         * @param filter another filter
         * @return a new filter that is the OR of the two
         */
        public Filter<?> or(final Filter<?> filter) {
            return new Filter<Object>() {

                @Override
                public boolean innerFilter(Object previousValue, Object currentValue) {
                    return super.innerFilter(previousValue, currentValue) ||
                            filter.innerFilter(previousValue, currentValue);
                }

                @Override
                public boolean filter(Object previousValue, Object currentValue) {
                    throw new UnsupportedOperationException("Not used.");
                }

            };
        }
    }

    /**
     * Filters a stream of updates with the given filter.
     *
     * @param <T> the type of data streaming in and out
     * @param filter the filtering function
     * @param expression the argument expression
     * @return a new expression for the filtering result
     */
    public static <T> DesiredRateExpression<List<T>> filterBy(final Filter<?> filter,
            DesiredRateExpression<List<T>> expression) {
        String name = expression.getName();
        final Function<List<T>> arg = expression.getFunction();
        return new DesiredRateExpressionImpl<List<T>>(expression,
                new Function<List<T>>() {

                    private T previousValue;

                    @Override
                    public List<T> getValue() {
                        List<T> list = arg.getValue();
                        List<T> newList = new ArrayList<T>();
                        for (T element : list) {
                            if (!filter.innerFilter(previousValue, element)) {
                                newList.add(element);
                                previousValue = element;
                            }
                        }
                        return newList;
                    }
                }, name);
    }
    
    // Static collections (no change after expression creation

    /**
     * Converts a list of expressions to an expression that returns the list of results.
     * 
     * @param <T> type being read
     * @param expressions a list of expressions
     * @return an expression representing the list of results
     */
    public static <T> DesiredRateExpression<List<T>> listOf(DesiredRateExpressionList<T> expressions) {
        // Calculate all the needed functions to combine
        List<Function> functions = new ArrayList<Function>();
        for (DesiredRateExpression<T> expression : expressions.getDesiredRateExpressions()) {
            functions.add(expression.getFunction());
        }

        @SuppressWarnings("unchecked")
        DesiredRateExpression<List<T>> expression = new DesiredRateExpressionImpl<List<T>>(expressions,
                (Function<List<T>>) (Function) new ListOfFunction(functions), null);
        return expression;
    }
    
    /**
     * Converts a list of expressions to an expression that returns the map from
     * the name to the results.
     * 
     * @param <T> type being read
     * @param expressions a list of expressions
     * @return an expression representing a map from name to results
     */
    public static <T> DesiredRateExpression<Map<String, T>> mapOf(DesiredRateExpressionList<T> expressions) {
        // Calculate all the needed functions to combine
        List<String> names = new ArrayList<String>();
        List<Function<T>> functions = new ArrayList<Function<T>>();
        for (DesiredRateExpression<T> expression : expressions.getDesiredRateExpressions()) {
            names.add(expression.getName());
            functions.add(expression.getFunction());
        }

        @SuppressWarnings("unchecked")
        DesiredRateExpression<Map<String, T>> expression = new DesiredRateExpressionImpl<Map<String, T>>(expressions,
                new MapOfFunction(names, functions), null);
        return expression;
    }
    
    /**
     * Converts a list of expressions to an expression that returns the map from
     * the name to the results.
     * 
     * @param <T> type being read
     * @param expressions a list of expressions
     * @return an expression representing a map from name to results
     */
    public static <T> WriteExpression<Map<String, T>> mapOf(WriteExpressionList<T> expressions) {
        // Calculate all the needed functions to combine
        List<String> names = new ArrayList<String>();
        List<WriteFunction<T>> functions = new ArrayList<WriteFunction<T>>();
        for (WriteExpression<T> expression : expressions.getWriteExpressions()) {
            names.add(expression.getName());
            functions.add(expression.getWriteFunction());
        }

        @SuppressWarnings("unchecked")
        WriteExpression<Map<String, T>> expression = new WriteExpressionImpl<Map<String, T>>(expressions,
                new MapOfWriteFunction<T>(names, functions), null);
        return expression;
    }

    /**
     * Converts a list of expressions to an expression that returns the map from
     * the name to the results.
     * 
     * @param <R> read payload
     * @param <W> write payload
     * @param expressions a list of expressions
     * @return an expression representing a map from name to results
     */
    public static <R, W> DesiredRateReadWriteExpression<Map<String, R>, Map<String, W>> mapOf(DesiredRateReadWriteExpressionList<R, W> expressions) {
        // Calculate all the needed functions to combine
        List<String> names = new ArrayList<String>();
        List<Function<R>> functions = new ArrayList<Function<R>>();
        List<WriteFunction<W>> writefunctions = new ArrayList<WriteFunction<W>>();
        for (DesiredRateReadWriteExpression<R, W> expression : expressions.getDesiredRateReadWriteExpressions()) {
            names.add(expression.getName());
            functions.add(expression.getFunction());
            writefunctions.add(expression.getWriteFunction());
        }
        
        DesiredRateExpression<Map<String, R>> readExpression = new DesiredRateExpressionImpl<Map<String, R>>(expressions,
                new MapOfFunction<R>(names, functions), null);
        WriteExpression<Map<String, W>> writeExpression = new WriteExpressionImpl<Map<String, W>>(expressions,
                new MapOfWriteFunction<W>(names, writefunctions), null);
        
        return new DesiredRateReadWriteExpressionImpl<Map<String, R>, Map<String, W>>(readExpression, writeExpression);
    }
    
}
