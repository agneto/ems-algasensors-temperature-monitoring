package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors/{sensorId}/alert")
@RequiredArgsConstructor
public class SensorAlertController {

    private final SensorAlertRepository sensorAlertRepository;

    @GetMapping
    public SensorAlertOutput getDetail(@PathVariable final TSID sensorId) {
        final SensorAlert sensorAlert = this.sensorAlertRepository.findById(new SensorId(sensorId)).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return convertSensorAlertOutput(sensorAlert);
    }

    @PutMapping
    public SensorAlertOutput createOrUpdate(
            @PathVariable final TSID sensorId,
            @RequestBody final SensorAlertInput input) {
        final SensorAlert sensorAlert = this.sensorAlertRepository
                .findById(new SensorId(sensorId)).orElse(
                        SensorAlert.builder()
                        .id(new SensorId(sensorId))
                        .minTemperature(null)
                        .maxTemperature(null)
                        .build());

        sensorAlert.setMaxTemperature(input.maxTemperature());
        sensorAlert.setMinTemperature(input.minTemperature());

        this.sensorAlertRepository.saveAndFlush(sensorAlert);

        return this.convertSensorAlertOutput(sensorAlert);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final TSID sensorId) {
        final SensorAlert sensorAlert = this.sensorAlertRepository.findById(new SensorId(sensorId)).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        this.sensorAlertRepository.delete(sensorAlert);
    }


    private SensorAlertOutput convertSensorAlertOutput(final SensorAlert sensorAlertSaved) {
        return new SensorAlertOutput(
                sensorAlertSaved.getId().getValue(),
                sensorAlertSaved.getMaxTemperature(),
                sensorAlertSaved.getMinTemperature());
    }
}
