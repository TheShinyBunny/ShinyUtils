package com.shinybunny.utils;

import java.util.function.*;

public class Lambda {
	
	public static <T> Consumer<T> emptyConsumer() {
		return (t)->{};
	}
	
	public static <T> Predicate<T> alwaysTrue() {
		return t->true;
	}
	
	public static <T> Predicate<T> alwaysFalse() {
		return t->false;
	}

	public static final Supplier<?> NULL_SUPPLIER = ()->null;
	
	public static <T> Predicate<T> negate(Predicate<T> predicate) {
		return (t)->!predicate.test(t);
	}
	
	public static <A,B> BiPredicate<A, B> and(Predicate<A> first, Predicate<B> second) {
		return (a,b)->first.test(a) && second.test(b);
	}
	
	public static <A,B> BiPredicate<A, B> or(Predicate<A> first, Predicate<B> second) {
		return (a,b)->first.test(a) || second.test(b);
	}

	public static <A,B> BiPredicate<A,B> xor(Predicate<A> first, Predicate<B> second) {
		return (a,b)->first.test(a) != second.test(b);
	}
	
	public static <T> Function<T, T> noChange() {
		return t->t;
	}


	public static <T,P> Predicate<T> propertyEquals(Function<T,P> property, P obj) {
		return t->property.apply(t).equals(obj);
	}
}
