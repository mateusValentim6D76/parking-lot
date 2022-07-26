package com.api.parkingcontrol.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingcontrol.dto.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

	@Autowired
	private ParkingSpotService parkingSpotService;

	@PostMapping
	public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto) {
		if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use");
		}
		if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot Number is already in use");
		}
		if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Apartment and Block is already in use");
		}
		var parkingSpotModel = new ParkingSpotModel();
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
	}

	@GetMapping
	public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots() {
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") Long id) {
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
		if (!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteOneParkingSpot(@PathVariable(value = "id") Long id) {
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
		if (!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		parkingSpotService.delete(parkingSpotModelOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully");
	}

	/*
	 * Metodo tem o mesmo comportamento do metodo updateParkingSpot Localizado a
	 * partir da linha 102
	 */
//	@PutMapping("/{id}")
//	public ResponseEntity<Object> updateParkingSpotItemByItem(@PathVariable(value = "id") Long id,
//			@RequestBody @Valid ParkingSpotDto parkingSpotDto) {
//		Optional<ParkingSpotModel> parkingSpotOptional = parkingSpotService.findById(id);
//		if (!parkingSpotOptional.isPresent()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot Not Found");
//		}
//
//		var parkingSpotModel = parkingSpotOptional.get();
//		parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
//		parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
//		parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
//		parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
//		parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
//		parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
//		parkingSpotModel.setApartment(parkingSpotDto.getApartment());
//		parkingSpotModel.setBlock(parkingSpotDto.getBlock());
//		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
//	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") Long id,
			@RequestBody @Valid ParkingSpotDto parkingSpotDto) {
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
		if (!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot Not Found");
		}
		var parkingSpotModel = new ParkingSpotModel();
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
		parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
		parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());

		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));

	}

}
