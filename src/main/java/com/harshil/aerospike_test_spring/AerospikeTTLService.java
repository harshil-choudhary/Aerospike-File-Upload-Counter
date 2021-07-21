package com.harshil.aerospike_test_spring;

import com.aerospike.client.*;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import org.springframework.stereotype.Service;


@Service
public class AerospikeTTLService {

    public String checkHits (String userId, int  fileCount) {

        AerospikeClient client = new AerospikeClient("127.0.0.1", 3000);

        long startTime = System.currentTimeMillis();

        Key key = new Key("test", "userHitCounter", userId);
        Record getRecord = client.get(new Policy(), key);
        int initialFileCount = 0;
        boolean recordAlreadyExists = true;
        try {
            initialFileCount = getRecord.getInt("FileCount");
        } catch (Exception e) {
            recordAlreadyExists = false;
        }

        Bin fileCountBin = new Bin("FileCount", fileCount);
        WritePolicy writePolicy = new WritePolicy();

        if (recordAlreadyExists) {
            if (initialFileCount + fileCount == 20) {
                System.out.println ("You have reached your limit for uploading files, try again in 30 mins.");
            } else if (initialFileCount + fileCount > 15) {
                System.out.println ("You can only upload " + (15-initialFileCount) + " files for now, try again in 30 mins to upload more.");
            } else {
                client.operate(writePolicy, key, Operation.add(fileCountBin));
            }
        } else {
            client.put(writePolicy, key, fileCountBin);
            System.out.println(key + " " + fileCountBin);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Time taken in sec: " + (endTime - startTime)/1000);

        Record updatedRecord = client.get(new Policy(), key);
        System.out.println("For user " + userId +", filecount is " + updatedRecord.getInt("FileCount"));

        client.close();

        return userId + fileCount;
    }
}
