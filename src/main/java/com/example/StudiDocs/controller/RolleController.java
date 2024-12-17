package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Rolle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/rollen")
public class RolleController {

    @GetMapping
    public ResponseEntity<List<Rolle>> getAllRollen() {
        List<Rolle> rollen = Arrays.asList(Rolle.values());
        return ResponseEntity.ok(rollen);
    }
}
