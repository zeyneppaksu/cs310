package com.example.howudoin.controller;

import com.example.howudoin.model.Group;
import com.example.howudoin.model.Message;
import com.example.howudoin.service.MessageService;
import com.example.howudoin.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        String groupName = (String) requestBody.get("groupName");
        List<String> memberEmails = (List<String>) requestBody.get("memberEmails");
        String creatorEmail = authentication.getName();  // Get the creator's email from the authenticated user

        Group group = groupService.createGroup(groupName, creatorEmail, memberEmails);
        return ResponseEntity.ok(group);
    }



    @PostMapping("/{groupId}/add-member")
    public ResponseEntity<?> addMemberToGroup(@PathVariable String groupId, @RequestBody Map<String, String> requestBody) {
        String memberEmail = requestBody.get("email");

        if (memberEmail == null || memberEmail.isEmpty()) {
            return ResponseEntity.badRequest().body("Member email is required");
        }

        try {
            // Call the service to add the member
            Group updatedGroup = groupService.addMemberToGroup(groupId, memberEmail);
            return ResponseEntity.ok(updatedGroup);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group or User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the member");
        }


    }


    @PostMapping("/{groupId}/send")
    public ResponseEntity<?> sendGroupMessage(@PathVariable String groupId, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        String senderEmail = authentication.getName();
        String content = requestBody.get("content");

        Message message = messageService.sendGroupMessage(senderEmail, groupId, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<Message>> getGroupMessages(@PathVariable String groupId) {
        List<Message> messages = messageService.getGroupMessages(groupId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<String>> getGroupMembers(@PathVariable String groupId) {
        List<String> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(members);
    }
}
