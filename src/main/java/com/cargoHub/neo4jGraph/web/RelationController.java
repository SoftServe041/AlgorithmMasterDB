package com.cargoHub.neo4jGraph.web;

import com.cargoHub.neo4jGraph.model.HubRequest;
import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hub/relation")
public class RelationController {

    @Autowired
    private RelationService relationService;

    @PostMapping
    public void postNewRealation(@RequestBody HubRequest hubRequest) {
        relationService.createNewRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
    }

    @DeleteMapping
    public void deleteRelation(@RequestBody HubRequest hubRequest) {
        relationService.deleteMutualRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<Location>> getAllConnectedHubs(@PathVariable String name) {
        return ResponseEntity.ok(relationService.getAllConnectedLocations(name));
    }
}
