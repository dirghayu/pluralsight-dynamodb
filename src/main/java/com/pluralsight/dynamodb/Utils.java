package com.pluralsight.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.StreamSpecification;
import com.amazonaws.services.dynamodbv2.model.StreamViewType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import com.pluralsight.dynamodb.domain.Comment;
import com.pluralsight.dynamodb.domain.Item;
import com.pluralsight.dynamodb.domain.Order;

public class Utils {
    public static void createTable(AmazonDynamoDB dynamoDB) {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDB);

        createTable(Item.class, dynamoDBMapper, dynamoDB, false);
        createTable(Comment.class, dynamoDBMapper, dynamoDB, false);
        createTable(Order.class, dynamoDBMapper, dynamoDB, true);
    }
    private static void createTable(Class<?> itemClass, DynamoDBMapper dynamoDBMapper, AmazonDynamoDB dynamoDB,
                                    boolean enableStreams) {
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(itemClass);
        createTableRequest.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        if (createTableRequest.getGlobalSecondaryIndexes() != null)
            for (GlobalSecondaryIndex gsi : createTableRequest.getGlobalSecondaryIndexes()) {
                gsi.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
                gsi.withProjection(new Projection().withProjectionType("ALL"));
            }

        if (createTableRequest.getLocalSecondaryIndexes() != null)
            for (LocalSecondaryIndex lsi : createTableRequest.getLocalSecondaryIndexes()) {
                lsi.withProjection(new Projection().withProjectionType("ALL"));
            }
        if(enableStreams) {
            StreamSpecification streamSpecification = new StreamSpecification();

            streamSpecification.setStreamEnabled(true);
            streamSpecification.setStreamViewType(StreamViewType.NEW_IMAGE);
            createTableRequest.setStreamSpecification(streamSpecification);
        }

        if (!tableExists(dynamoDB, createTableRequest))
            dynamoDB.createTable(createTableRequest);

        waitForTableCreated(createTableRequest.getTableName(), dynamoDB);
        System.out.println("Created table for: " + itemClass.getCanonicalName());

    }

    private static void waitForTableCreated(String tableName, AmazonDynamoDB dynamoDB) {
        while (true) {
            try {
                Thread.sleep(500);
                TableDescription table = dynamoDB.describeTable(new DescribeTableRequest(tableName)).getTable();
                if (table == null)
                    continue;

                String tableStatus = table.getTableStatus();
                if (tableStatus.equals(TableStatus.ACTIVE.toString()))
                    return;
            } catch (ResourceNotFoundException ex) {
                // ignore
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    private static boolean tableExists(AmazonDynamoDB dynamoDB, CreateTableRequest createTableRequest) {
        try {
            DescribeTableResult describeTableResult = dynamoDB.describeTable(createTableRequest.getTableName());
            return true;
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
