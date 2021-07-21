package com.harshil.aerospike_test_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aerospike-ttl")
public class AerospikeController {

    @Autowired
    private AerospikeTTLService aerospikeTTLService;

    @PostMapping(value = "")
    public String aerospikeTTL (
            @RequestParam String userId,
            @RequestParam int fileCount
    ) {
        return aerospikeTTLService.checkHits(userId, fileCount);
    }
}
