package com.example.howudoin.service;

import com.example.howudoin.model.Group;
import com.example.howudoin.model.User;
import com.example.howudoin.repository.GroupRepository;
import com.example.howudoin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public Group createGroup(String groupName, String creatorEmail, List<String> memberEmails) {
        // Find creator by email
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        // Initialize the member IDs with the creator's ID
        List<String> memberIds = new ArrayList<>();
        memberIds.add(creator.getId());

        // Add each member's ID by email lookup
        for (String email : memberEmails) {
            userRepository.findByEmail(email).ifPresent(user -> memberIds.add(user.getId()));
        }

        // Create and save the group
        Group group = new Group();
        group.setName(groupName);
        group.setCreatorId(creator.getId());
        group.setMemberIds(memberIds);
        group.setCreationTimestamp(LocalDateTime.now());

        return groupRepository.save(group);
    }




    public Group addMemberToGroup(String groupId, String memberEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        group.getMemberIds().add(member.getId());
        return groupRepository.save(group);
    }

    public List<String> getGroupMembers(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));

        return group.getMemberIds().stream().toList();
    }
}
