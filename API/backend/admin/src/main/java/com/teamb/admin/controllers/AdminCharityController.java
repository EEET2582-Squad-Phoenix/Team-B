package com.teamb.admin.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.charity.dtos.CreateCharityDTO;
// import com.teamb.account.repositories.AccountRepository;
import com.teamb.charity.models.Charity;
import com.teamb.charity.services.CharityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.http.HttpStatus;



@RestController
@RequestMapping("/admin/charity")
public class AdminCharityController {

    @Autowired
    private CharityService service;

    @GetMapping("/all")
    public List<Charity> getAllCharities(){
        return service.getAllCharities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Charity> getCharityById(@PathVariable String id){
        Charity charity = service.getCharityByAccountId(id);
        return ResponseEntity.ok(charity);
    }

    // Fetch charity by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Charity> getCharityByEmail(@PathVariable String email){
        Charity charity = service.getCharityByEmail(email);
        return ResponseEntity.ok(charity);
    }

    //Create charity
    @PostMapping("")
    public ResponseEntity<Charity> createCharity(@RequestBody CreateCharityDTO newCharity) {
        var result = service.saveCharity(newCharity);        
        return ResponseEntity.ok(result);
    }

    // Update charity
    @PutMapping("/{id}")
    public ResponseEntity<Charity> updateCharity(@PathVariable String id, @RequestBody Charity updateCharity) {
        var result = service.updateCharity(id, updateCharity);
        return ResponseEntity.ok(result);
    }

    // Delete charity
    @DeleteMapping("/{id}")
    public ResponseEntity<ProblemDetail> deleteCharity(@PathVariable String id) {
        service.deleteCharity(id);
        ProblemDetail deletedMsg = ProblemDetail.forStatus(HttpStatus.OK);
        deletedMsg.setTitle("Charity deleted successfully");
        deletedMsg.setDetail(String.format("Charity with id %s deleted successfully", id));
        return ResponseEntity.ok(deletedMsg);    
    }

}
