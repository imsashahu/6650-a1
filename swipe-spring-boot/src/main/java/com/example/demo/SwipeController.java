package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/swipe")
public class SwipeController {

    @GetMapping(value = "/left")
    public String doGet() {
        return "Hello swipe.";
    }

    @PostMapping(value = "/left", consumes = "application/json")
    public ResponseEntity<HttpStatus> swipeLeft(@RequestBody Swipe swipe) {
        // Validate the request body
        if (!isValidRequest(swipe)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/right", consumes = "application/json")
    public ResponseEntity<HttpStatus> swipeRight(@RequestBody Swipe swipe) {
        // Validate the request body
        if (!isValidRequest(swipe)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private boolean isValidRequest(Swipe swipe) {
        // Implement logic to validate the request body
        return (swipe.getSwipee() >= 1 && swipe.getSwipee() <= 1000000)
                && (swipe.getSwiper() >= 1 && swipe.getSwiper() <= 5000)
                && (swipe.getComment().length() == 256);
    }
}
