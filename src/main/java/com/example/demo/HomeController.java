package com.example.demo;


import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String ListPage(Model model){
        model.addAttribute("flights",flightRepository.findAll());
        return "list";
    }
    @PostMapping("/searchlist")
    public String SearchPage(Model model,@RequestParam("search") String search) {
        model.addAttribute("flights",flightRepository.findByDepartureContainingIgnoreCaseOrAirlineNameContainingIgnoreCase(search,search));
        return "searchIndex";
    }
    @GetMapping("/form")
    public String FormPage(Model model){
        model.addAttribute("flight",new Flight());
        return "form";
    }


    @PostMapping("/form")
    public String processMessage(@Valid Flight flight, BindingResult result,
                                 @RequestParam("file") MultipartFile file ){
        if(result.hasErrors()){
            return "form";
        }
        if (file.isEmpty()){
            flightRepository.save(flight);
            return "redirect:/";
        }
        try{
            Map uploadResult=cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype","auto"));
            flight.setPic(uploadResult.get("url").toString());
            flightRepository.save(flight);
        } catch (IOException e){
            e.printStackTrace();
            return "redirect:/form";
        }
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String ViewDetail(@PathVariable("id") long id,Model model){
        model.addAttribute("flight",flightRepository.findById(id).get());
        return "detail";
    }
    @RequestMapping("/update/{id}")
    public String Update(@PathVariable("id") long id,Model model){
        model.addAttribute("flight",flightRepository.findById(id).get());
        return "form";
    }
    @RequestMapping("/delete/{id}")
    public String Delete(@PathVariable("id") long id){
        flightRepository.deleteById(id);
        return "redirect:/";
    }


}
