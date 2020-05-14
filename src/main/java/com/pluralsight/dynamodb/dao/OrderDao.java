package com.pluralsight.dynamodb.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.pluralsight.dynamodb.domain.Order;

import java.util.List;

public class OrderDao {

    private final DynamoDBMapper mapper;
    public OrderDao(AmazonDynamoDB client) {
        ListTablesResult listTablesResult = client.listTables();
        this.mapper = new DynamoDBMapper(client);

    }

    public Order put(Order item) {
//        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
//                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER)
//                .build();
        mapper.save(item);
        return item;
    }
    public Order update(Order item) {
        mapper.save(item);
        return item;
    }

    public Order get(String id) {
        Order item = mapper.load(Order.class, id);
        return item;
    }
    public void delete(String id) {
        Order item = new Order();
        item.setId(id);
        mapper.delete(item);
    }

    public List<Order> getAll() {
        return mapper.scan(Order.class, new DynamoDBScanExpression());
    }

//    {
//            Order item = Order.builder()
//                    .id(UUID.randomUUID().toString())
//                    .name("Bitcoin Miner")
//                    .build();
//            mapper.load(item);
//        }
}
