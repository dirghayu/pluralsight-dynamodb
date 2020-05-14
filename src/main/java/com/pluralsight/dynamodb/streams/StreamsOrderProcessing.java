package com.pluralsight.dynamodb.streams;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.streamsadapter.model.RecordAdapter;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.model.Record;

import java.util.List;
import java.util.Map;

public class StreamsOrderProcessing implements IRecordProcessor {
    @Override
    public void initialize(String shardId) {

    }

    @Override
    public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {

        for(Record record: records) {
                if(record instanceof RecordAdapter){
                    com.amazonaws.services.dynamodbv2.model.Record streamRecord = ((RecordAdapter) record).getInternalObject();
                    if("INSERT".equals(streamRecord.getEventName())) {
                        Map<String , AttributeValue> values =
                                streamRecord.getDynamodb().getNewImage();
                        int totalPrice = Integer.parseInt(values.get("totalPrice").getN());
                        if(totalPrice > 900) {
                            System.out.println("Very expensive order--->> please check");
                        }
                        checkpoint(checkpointer);
                    }
                }
        }
    }

    private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        try {
            checkpointer.checkpoint();
        } catch (InvalidStateException e) {
            e.printStackTrace();
        } catch (ShutdownException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
        if(reason == ShutdownReason.TERMINATE) {
            checkpoint(checkpointer);
        }
    }
}
