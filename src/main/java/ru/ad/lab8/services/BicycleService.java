package ru.ad.lab8.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ad.lab8.exceptions.BicycleException;
import ru.ad.lab8.models.Bicycle;
import ru.ad.lab8.repositories.BicycleRepo;

@Service
@Transactional(readOnly = true)
public class BicycleService {

  private final BicycleRepo bicycleRepo;

  @Autowired
  public BicycleService(BicycleRepo bicycleRepo) {
    this.bicycleRepo = bicycleRepo;
  }

  public List<Bicycle> findAll() {
    return bicycleRepo.findAll();
  }

  public Bicycle findOne(int id) throws BicycleException {
    return bicycleRepo.findById(id).orElseThrow(() -> new BicycleException(id));
  }

  @Transactional
  public void save(Bicycle bicycle) {
    bicycleRepo.save(bicycle);
  }

  @Transactional
  public void update(int id, Bicycle updBicycle) throws BicycleException{
    if (bicycleRepo.existsById(id)) {
      updBicycle.setId(id);
      bicycleRepo.save(updBicycle);
      return;
    }
    throw new BicycleException(id);
  }

  @Transactional
  public void delete(int id) {
    if (bicycleRepo.existsById(id)) {
      bicycleRepo.deleteById(id);
      return;
    }
    throw new BicycleException(id);
  }

  @Transactional
  public void buy(int id) {
    if (bicycleRepo.existsById(id)) {
      bicycleRepo.deleteById(id);
      return;
    }
    throw new BicycleException(id);
  }

  public List<Bicycle> findByPrice(float price) {
    return bicycleRepo.findByPriceLessThan(price);
  }
}
