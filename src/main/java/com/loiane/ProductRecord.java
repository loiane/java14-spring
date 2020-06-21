package com.loiane;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Id;

public record ProductRecord(
        @JsonProperty Integer id,
        @JsonProperty String name,
        @JsonProperty int status) { }
