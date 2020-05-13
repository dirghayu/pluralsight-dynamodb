package com.pluralsight.dynamodb.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.pluralsight.dynamodb.domain.Item;

import java.util.List;
import java.util.UUID;

public class ItemDao {

    private final DynamoDBMapper mapper;
    public ItemDao(AmazonDynamoDB client) {
        ListTablesResult listTablesResult = client.listTables();
        this.mapper = new DynamoDBMapper(client);

    }

    public Item put(Item item) {
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER)
                .build();
        mapper.save(item, config);
        return item;
    }
    public Item update(Item item) {
        mapper.save(item);
        return item;
    }

    public Item get(String id) {
        Item item = mapper.load(Item.class, id);
        return item;
    }
    public void delete(String id) {
        Item item = new Item();
        item.setId(id);
        mapper.delete(item);
    }

    public List<Item> getAll() {
        return mapper.scan(Item.class, new DynamoDBScanExpression());
    }

//    {
//            Item item = Item.builder()
//                    .id(UUID.randomUUID().toString())
//                    .name("Bitcoin Miner")
//                    .build();
//            mapper.load(item);
//        }
}
