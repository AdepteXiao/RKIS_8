package ru.ad.lab8.controllers;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ad.lab8.exceptions.BicycleException;
import ru.ad.lab8.models.Bicycle;
import ru.ad.lab8.services.BicycleService;
import ru.ad.lab8.util.BicycleErrorResponse;

@RestController
@RequestMapping("/api/bicycles")
public class BicycleRestController {
  private final BicycleService bicycleService;

  @Autowired
  public BicycleRestController(BicycleService bicycleService) {
    this.bicycleService = bicycleService;
  }

  @GetMapping
  public ResponseEntity<List<Bicycle>> getAll(
      @RequestParam(name = "price", required = false) Float price) {
    if (price != null) {
      return new ResponseEntity<>(bicycleService.findByPrice(price), HttpStatus.OK);
    }
    return new ResponseEntity<>(bicycleService.findAll(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Bicycle> show(@PathVariable("id") int id) {
    return new ResponseEntity<>(bicycleService.findOne(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<HttpStatus> create(
      @RequestBody @Valid Bicycle bicycle,
      BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      throw new BicycleException(getError(bindingResult));
    }
    bicycleService.save(bicycle);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<HttpStatus> update(
      @RequestBody @Valid Bicycle bicycle,
      BindingResult bindingResult,
      @PathVariable("id") int id
  ) {
    if (bindingResult.hasErrors()) {
      throw new BicycleException(getError(bindingResult));
    }
    bicycleService.update(id, bicycle);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
    bicycleService.delete(id);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  private String getError(BindingResult bindingResult) {
    StringBuilder errorMsg = new StringBuilder();
    List<FieldError> errors = bindingResult.getFieldErrors();
    for (FieldError error:
        errors) {
      errorMsg
          .append(error.getField())
          .append(" - ")
          .append(error.getDefaultMessage())
          .append(";");
    }
    return errorMsg.toString();
  }

  @ExceptionHandler
  private ResponseEntity<BicycleErrorResponse> handleCreateException(BicycleException e) {
    return new ResponseEntity<>(new BicycleErrorResponse(e.getMessage(), System.currentTimeMillis()), HttpStatus.BAD_REQUEST);
  }
}
