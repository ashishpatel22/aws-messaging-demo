package com.ericsson.aws.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import com.ericsson.aws.exception.SQSQueueInitializationException;
import com.ericsson.aws.models.BusinessMessage;

/**
 * 
 * @author ekumaaa
 *
 */
@Service
public class BusinessMessageProcessingService {

	private final Logger logger = LogManager.getLogger(BusinessMessageProcessingService.class);

	@Value("${businessmessage.queue.name}")
	private String businessmessageQueueName;

	private String queueUrl;
	private AmazonSQS sqs;

	@PostConstruct
	private void init() throws SQSQueueInitializationException {

		try {
			sqs = AmazonSQSClientBuilder.defaultClient();
			CreateQueueResult create_result = sqs.createQueue(businessmessageQueueName);

			if (logger.isDebugEnabled()) {
				logger.debug("Queue created with url:" + create_result.getQueueUrl());
			}

			queueUrl = sqs.getQueueUrl(businessmessageQueueName).getQueueUrl();
			if (sqs == null || !StringUtils.hasText(queueUrl)) {
				throw new SQSQueueInitializationException(
						"Could not initialized the SQS queue with name:" + businessmessageQueueName);
			}

			// Enable long polling on an existing queue
			SetQueueAttributesRequest set_attrs_request = new SetQueueAttributesRequest().withQueueUrl(queueUrl)
					.addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20");
			sqs.setQueueAttributes(set_attrs_request);
		} catch (AmazonSQSException e) {
			if (!e.getErrorCode().equals("QueueAlreadyExists")) {
				throw e;
			}
		}
	}

	public void processBusinessMessage(BusinessMessage businessMessage) {
		if (logger.isDebugEnabled()) {
			logger.debug("Recieved businessMessage:" + businessMessage);
		}
		submitMessageToAWSSQS(businessMessage);
	}

	private void submitMessageToAWSSQS(BusinessMessage businessMessage) {
		SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl)
				.withMessageBody(businessMessage.getBody()).withDelaySeconds(5);
		sqs.sendMessage(send_msg_request);
	}

	public List<BusinessMessage> fetchAllTheMessages() {
		// receive messages from the queue

		// Enable long polling on a message receipt
		ReceiveMessageRequest receive_request = new ReceiveMessageRequest().withQueueUrl(queueUrl)
				.withWaitTimeSeconds(20);

		ReceiveMessageResult messages = sqs.receiveMessage(receive_request);

		List<BusinessMessage> businessMessages = new ArrayList<>();
		// delete messages from the queue
		for (Message m : messages.getMessages()) {
			businessMessages.add(new BusinessMessage(m.getBody(), m.getMessageId()));
			sqs.deleteMessage(queueUrl, m.getReceiptHandle());
		}
		return businessMessages;
	}
}
