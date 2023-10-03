package com.MDL30MBDEPENL.service;

import com.MDL30MBDEPENL.model.Communities;
import com.MDL30MBDEPENL.repository.CommunitiesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service

public class CommunitiesService {
    @Autowired
    private CommunitiesRepository communitiesRepository;

    public Communities createCommunity(Communities communities){
        return communitiesRepository.save(communities);
    }
    public List<Communities> communitiesFindAll(){

        return communitiesRepository.findAll();
    }
    public void deleteCommunity(Communities communities){

        communitiesRepository.delete(communities);
    }

    public Optional<Communities> communityFindById(Long id){

        return communitiesRepository.findById(id);
    }
}