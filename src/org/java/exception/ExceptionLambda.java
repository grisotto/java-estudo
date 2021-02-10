package org.java.exception;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ExceptionLambda {

    public static void main(String[] args) {
        uglyWay();
        betterWay();
    }

    private static void uglyWay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = asList("2020-10-11", "2020-nov-12", "2020-12-01");

        List<Date> dates = dateList.stream().map(s -> {
            try {
                return format.parse(s);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(toList());

    }

    public static void betterWay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = asList("2020-10-11", "2020-nov-12", "2020-12-01");

        // interface. pq stream.map recebe um java.util.function.Function. Fiz a conversão
        ThrowingFunction<String, Date> p = format::parse;

        // Funcao que altera a exception checked para unchecked
        Function<String, Date> f = wrapAsRuntime(p);

        List<Date> dates = dateList.stream().map(f).collect(toList());


        // Jeito simplificado
        List<Date> dates0 = dateList.stream().map(wrapAsRuntime(s -> format.parse(s))).collect(toList());
    }

    // Isso pode ser útil em vários problemas para converter checked para unchecked
    private static <T, R> Function<T, R> wrapAsRuntime(ThrowingFunction<T, R> p) {
        return t -> {
            try {
                return p.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        };
    }

    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
