package com.pluralsight.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {
    private static final DateTimeFormatter datetimeFormatter= DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public String convert(LocalDateTime object) {
        return datetimeFormatter.format(object);
    }

    @Override
    public LocalDateTime unconvert(String object) {
        return LocalDateTime.parse(object, datetimeFormatter);
    }
}
