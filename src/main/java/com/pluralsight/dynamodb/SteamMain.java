package com.pluralsight.dynamodb;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.streamsadapter.AmazonDynamoDBStreamsAdapterClient;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.pluralsight.dynamodb.streams.StreamRecordProcessorFactory;

public class SteamMain {
    public static void main(String[] args) {
        String streamArn = "arn:aws:dynamodb:ap-southeast-2:795062535569:table/Orders/stream/2020-05-14T00:49:57.811";
        ProfileCredentialsProvider streamsCredentials =  new ProfileCredentialsProvider("dynamo");
        AmazonDynamoDBStreamsAdapterClient client =
                new AmazonDynamoDBStreamsAdapterClient(streamsCredentials,
                        new ClientConfiguration())
                ;
        client.setRegion(Region.getRegion(Regions.fromName("ap-southeast-2")));

        KinesisClientLibConfiguration workerConfig = new KinesisClientLibConfiguration(
                "streams-adapter-demo",
                streamArn,
                streamsCredentials,
                "stream-demo-worker"
        ).withInitialPositionInStream(InitialPositionInStream.LATEST)
                ;

        Worker worker = new Worker.Builder()
                .recordProcessorFactory(new StreamRecordProcessorFactory())
                .config(workerConfig)
                .kinesisClient(client)
                .build();

        worker.run();

    }
}
