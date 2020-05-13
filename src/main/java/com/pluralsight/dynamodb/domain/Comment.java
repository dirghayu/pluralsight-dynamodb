package com.pluralsight.dynamodb.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.pluralsight.dynamodb.LocalDateTimeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@DynamoDBTable(tableName = "Comments")
@Getter
@Setter
@ToString
public class Comment {

    @DynamoDBHashKey
    private String itemId;
    @DynamoDBRangeKey
    @DynamoDBAutoGeneratedKey
    private String messageId;

    @DynamoDBAttribute
    private String message;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime dateTime;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "UserId-Index")
    private String userId;

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "UserId-Index",
     localSecondaryIndexName = "Rating-Index")
    private int rating;

}