package com.ericsson.aws.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class BusinessMessage {

	private String body;
	private String messageId;
}
