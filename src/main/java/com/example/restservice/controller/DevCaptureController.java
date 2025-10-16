package com.example.restservice.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class DevCaptureController {
  @GetMapping("/dev/callback")
  String capture(@RequestParam String code, @RequestParam String state) {
    System.out.println("GOT CODE=" + code + " STATE=" + state);
    return "OK — code=" + code + " state=" + state;
  }
}