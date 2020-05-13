package com.pluralsight.dynamodb.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.xspec.QueryExpressionSpec;
import com.pluralsight.dynamodb.domain.Comment;
import com.pluralsight.dynamodb.domain.Comment;

import java.util.List;

public class CommentDao {

    private final DynamoDBMapper mapper;
    public CommentDao(AmazonDynamoDB client) {
        this.mapper = new DynamoDBMapper(client);

    }

    public Comment put(Comment item) {
        mapper.save(item);
        return item;
    }

    public Comment get(String itemId, String messageId) {

        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setMessageId(messageId);
        Comment item = mapper.load(comment);
        return item;
    }
    public List<Comment> getAllForItem(String itemId) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        DynamoDBQueryExpression<Comment>  expression = new DynamoDBQueryExpression<Comment>()
                .withHashKeyValues(comment);

        return mapper.query(Comment.class, expression);
    }
    public List<Comment> getAllForUser(String user) {
        Comment comment = new Comment();
        comment.setUserId(user);
        DynamoDBQueryExpression<Comment>  expression = new DynamoDBQueryExpression<Comment>()
                .withHashKeyValues(comment)
                .withConsistentRead(false);

        return mapper.query(Comment.class, expression);
    }

    public List<Comment> getAllForItemWithRating(String itemId, int minRating) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        DynamoDBQueryExpression<Comment>  expression = new DynamoDBQueryExpression<Comment>()
                .withHashKeyValues(comment)
                .withRangeKeyCondition("rating",
                        new Condition()
                        .withComparisonOperator(ComparisonOperator.GE)
                        .withAttributeValueList(new AttributeValue().withN(Integer.toString(minRating)))
                );

        return mapper.query(Comment.class, expression);
    }

    public void delete(String id, String messageId) {
        Comment item = new Comment();
        item.setItemId(id);
        item.setMessageId(messageId);
        mapper.delete(item);
    }

    public List<Comment> getAll() {
        return mapper.scan(Comment.class, new DynamoDBScanExpression());
    }



}
