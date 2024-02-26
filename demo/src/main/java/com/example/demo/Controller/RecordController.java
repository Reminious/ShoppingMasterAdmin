package com.example.demo.Controller;

import com.example.demo.Repository.RecordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecordController {
    @Autowired
    RecordRepository recordRepository;
    @GetMapping("/admin/viewRecord")
    public String showRecordList(Model model) {
        model.addAttribute("records", recordRepository.findAll());
        return "record";
    }

}
