package com.harshil.aerospike_test_spring;

import com.aerospike.client.*;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class AerospikeTTLService {

    @Value("${fileNumberLimit}")
    private int fileNumberLimit;

    @Value("${aerospikeFileNumberLimit}")
    private int aerospikeFileNumberLimit;

    @Value("${aerospikeTTL}")
    private int aerospikeTTL;

    @Value("${AEROSPIKE_NAMESPACE}")
    private String AEROSPIKE_NAMESPACE;

    public String checkHits (String userId, int  fileCount) {

        AerospikeClient client = new AerospikeClient("127.0.0.1", 3000);

        long startTime = System.currentTimeMillis();

        Key key = new Key(AEROSPIKE_NAMESPACE, "userHitCounter", userId);
        Record getRecord = client.get(new Policy(), key);
        int initialFileCount = 0;
        boolean recordAlreadyExists = true;
        try {
            initialFileCount = getRecord.getInt("FileCount");
        } catch (Exception e) {
            recordAlreadyExists = false;
        }

        Bin fileCountBin = new Bin("FileCount", fileCount);



        if (recordAlreadyExists) {
            if (initialFileCount + fileCount == aerospikeFileNumberLimit + fileNumberLimit) {
                throw new TooManyRequestsException("You have reached your limit for uploading files, try again in 30 mins.");
            } else if (initialFileCount + fileCount > aerospikeFileNumberLimit) {
                throw new TooManyRequestsException("You can only upload " + (aerospikeFileNumberLimit-initialFileCount) + " files for now, try again in 30 mins to upload more.");
            } else {
                WritePolicy updateWritePolicy = new WritePolicy();
                updateWritePolicy.expiration = -2;
                client.operate(updateWritePolicy, key, Operation.add(fileCountBin));
            }
        } else {
            WritePolicy newWritePolicy = new WritePolicy();
            newWritePolicy.expiration = aerospikeTTL;
            client.put(newWritePolicy, key, fileCountBin);
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
