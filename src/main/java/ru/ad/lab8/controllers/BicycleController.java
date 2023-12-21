package ru.ad.lab8.controllers;

import jakarta.validation.Valid;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ad.lab8.messages.Producer;
import ru.ad.lab8.models.Bicycle;
import ru.ad.lab8.services.BicycleService;

@Controller
@RequestMapping("/bicycles")
public class BicycleController {
  private final BicycleService bicycleService;

  private final Producer messageProducer;

  @Autowired
  public BicycleController(BicycleService bicycleService, Producer messageProducer) {
    this.bicycleService = bicycleService;
    this.messageProducer = messageProducer;
  }



  @GetMapping()
  public String index(@RequestParam(name = "price", required = false) Float price, Model model) {
    if (price != null) {
      model.addAttribute("bicycles", bicycleService.findByPrice(price));
    } else {
      model.addAttribute("bicycles", bicycleService.findAll());
    }
    return "bicycles/index";
  }


  @GetMapping("/{id}")
  public String show(@PathVariable("id") int id, Model model) {
    model.addAttribute("bicycle", bicycleService.findOne(id));
    return "bicycles/show";
  }


  @GetMapping("/{id}/edit")
  public String edit(@PathVariable("id") int id, Model model) {
    model.addAttribute("bicycle", bicycleService.findOne(id));
    return "bicycles/edit";
  }


  @GetMapping("/new")
  public String newInstrument(@ModelAttribute("bicycle") Bicycle bicycle) {
    return "bicycles/new";
  }


  @PostMapping()
  public String create(
      @ModelAttribute("bicycle") @Valid Bicycle bicycle,
      BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      String errors = bindingResult.getAllErrors()
          .stream()
          .map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(", "));
      messageProducer.sendMessage("Велосипед не был добавлен так как: " + errors);
      return "bicycles/new";
    }
    messageProducer.sendMessage("Добавлен велосипед: " + bicycle);
    bicycleService.save(bicycle);
    return "redirect:/bicycles";
  }


  @PatchMapping("/{id}")
  public String update(
      @ModelAttribute("bicycle") @Valid Bicycle bicycle,
      BindingResult bindingResult,
      @PathVariable("id") int id
  ) {
    if (bindingResult.hasErrors()) {
      String errors = bindingResult.getAllErrors()
          .stream()
          .map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(", "));

      messageProducer.sendMessage("Велосипед с id=" + id + " не был изменен так как: " + errors);
      return "bicycles/edit";
    }
    bicycleService.update(id, bicycle);
    messageProducer.sendMessage("Велосипед с id=" + id + " изменен на " + bicycle);
    return "redirect:/bicycles";
  }

  @DeleteMapping("/{id}")
  public String delete(@PathVariable("id") int id) {
    bicycleService.delete(id);
    messageProducer.sendMessage("Велосипед с id=" + id + " удален");
    return "redirect:/bicycles";
  }

  @PatchMapping("/{id}/buy")
  public String buy(@PathVariable("id") int id) {
    messageProducer.sendMessage("Куплен велосипед " + bicycleService.findOne(id));
    bicycleService.buy(id);
    return "redirect:/bicycles";
  }
}
