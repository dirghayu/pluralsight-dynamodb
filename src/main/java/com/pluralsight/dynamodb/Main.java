package com.pluralsight.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.pluralsight.dynamodb.dao.CommentDao;
import com.pluralsight.dynamodb.dao.ItemDao;
import com.pluralsight.dynamodb.dao.OrderDao;
import com.pluralsight.dynamodb.domain.Comment;
import com.pluralsight.dynamodb.domain.Item;
import com.pluralsight.dynamodb.domain.Order;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static void main(String args[]){
        try {

            AmazonDynamoDB client= AmazonDynamoDBClientBuilder
                    .standard()
                    .withCredentials(new ProfileCredentialsProvider("dynamo"))
                    .withRegion(Regions.fromName("ap-southeast-2"))
                    .build();
            Utils.createTable( client);
//            complexQueriesDemo(client);
//            highLevelDemo(client);
//            optimisticLockingDemo(client);
            streamDemo(client);
        } catch (Exception e){

            e.printStackTrace();
        }
    }

    private static void streamDemo(AmazonDynamoDB client) throws Exception{
        OrderDao orderDao = new OrderDao(client);
        Random random = new Random();
        while(true) {
            Order order = new Order();
            order.setItemsIds(Arrays.asList("1", "2"));
            order.setTotalPrice(random.nextInt(1000));
            orderDao.put(order);

            System.out.println("Order created: " + order);
            Thread.sleep(1000);

        }
    }
    private static void optimisticLockingDemo(AmazonDynamoDB client) {
        ItemDao itemDao = new ItemDao(client);
        Item item = itemDao.put(newItem("Computer", "good one"));
        Item item1 = itemDao.get(item.getId());
        Item item2 = itemDao.get(item.getId());
        updateTitle(item1, "Title updated", itemDao);
        updateDescription(item2, "Description updated", itemDao);

        System.out.println("Updated both items");

    }

    private static void updateDescription(Item item2, String description, ItemDao itemDao ) {
        item2.setDescription(description);
        itemDao.update(item2);
    }

    private static void updateTitle(Item item1, String title, ItemDao itemDao ) {
        item1.setName(title);
        itemDao.update(item1);
    }

    private static void complexQueriesDemo(AmazonDynamoDB client) {
        CommentDao commentDao = new CommentDao(client);
        removeAll(commentDao);
        Comment c1 = commentDao.put(newComment("1", "Delivered", "10", 4));
        Comment c2 = commentDao.put(newComment("1", "Good stuff", "11", 4));
        Comment c3 = commentDao.put(newComment("1", "Not described", "12", 2));
        Comment c4 = commentDao.put(newComment("2", "so so ", "12", 3));
        Comment c5 = commentDao.put(newComment("3", "Best ", "12", 5));
        print(commentDao.getAll());

        pause();

        print(commentDao.getAllForItem("1"));
        pause();

        print(commentDao.getAllForItemWithRating("1", 3));
        pause();

        print(commentDao.getAllForUser("12"));
    }

    private static Comment newComment(String itemId, String message, String user, int rating) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setMessage(message);
        comment.setUserId(user);
        comment.setRating(rating);
        comment.setDateTime(LocalDateTime.now());
        return comment;
    }

    private static void removeAll(CommentDao commentDao) {
        commentDao.getAll().stream().forEach(c-> commentDao.delete(c.getItemId(), c.getMessageId()));
    }

    private static void lowLevelDemo(AmazonDynamoDB client){
        ItemDao item = new ItemDao(client);

    }

    private static void highLevelDemo(AmazonDynamoDB client){
        ItemDao itemDao = new ItemDao(client);
        Item item1 = itemDao.put(newItem("Item 1", "1st Item"));
        Item item2 = itemDao.put(newItem("Item 2", "2nd Item"));
        Item item3 = itemDao.put(newItem("Item 3", "3rd Item"));

        print(itemDao.getAll());

        pause();
        itemDao.delete(item1.getId());
        pause();
        print(itemDao.getAll());



    }
    public static <T> void  print(List<T> all){
        System.out.println(all.stream()
        .map(t->t.toString())
        .collect(Collectors.joining("\n"))
        );
    }

    public static Item newItem(String name, String description){
        Item item = new Item();
//                .builder()
        item.setName(name);
        item.setDescription(description);
                return item;
    }
    public static void pause(){
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
